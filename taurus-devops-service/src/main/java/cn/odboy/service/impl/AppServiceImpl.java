package cn.odboy.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.odboy.constant.AppLanguageEnum;
import cn.odboy.constant.AppLevelEnum;
import cn.odboy.constant.AppUserRoleEnum;
import cn.odboy.domain.App;
import cn.odboy.domain.AppUser;
import cn.odboy.domain.AppUserCollect;
import cn.odboy.infra.exception.BadRequestException;
import cn.odboy.model.GitlabProject;
import cn.odboy.model.MetaOption;
import cn.odboy.model.PageArgs;
import cn.odboy.mapper.AppMapper;
import cn.odboy.mapper.AppUserCollectMapper;
import cn.odboy.modules.system.domain.User;
import cn.odboy.modules.system.service.UserService;
import cn.odboy.repository.GitlabProjectRepository;
import cn.odboy.service.AppService;
import cn.odboy.service.AppUserService;
import cn.odboy.service.ProductLineAppService;
import cn.odboy.util.SecurityUtil;
import com.aliyun.dingtalkworkflow_1_0.models.SelectOption;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.gitlab4j.api.models.Project;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * <p>
 * 应用 服务实现类
 * </p>
 *
 * @author odboy
 * @since 2024-09-13
 */
@Service
@RequiredArgsConstructor
public class AppServiceImpl extends ServiceImpl<AppMapper, App> implements AppService {
    private final GitlabProjectRepository gitlabProjectRepository;
    private final ProductLineAppService productLineAppService;
    private final AppUserCollectMapper appUserCollectMapper;
    private final AppUserService appUserService;
    private final UserService userService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void create(App.CreateArgs args) {
        if (existByAppName(args.getAppName())) {
            throw new BadRequestException("应用 " + args.getAppName() + " 已存在，请确认后再试");
        }
        if (args.getProjectId() == null) {
            GitlabProject.CreateArgs createArgs = new GitlabProject.CreateArgs();
            createArgs.setName(args.getAppChName());
            createArgs.setAppName(args.getAppName());
            createArgs.setDescription(args.getDescription());
            GitlabProject.CreateResp createResp = gitlabProjectRepository.createProject(createArgs);
            gitlabProjectRepository.initGitIgnoreFile(createResp.getProjectId(), createResp.getDefaultBranch(), args.getAppLanguage());
            gitlabProjectRepository.initGitCiFile(createResp.getProjectId(), createResp.getDefaultBranch(), args.getAppLanguage(), createResp.getAppName());

            App newApp = new App();
            newApp.setAppName(createResp.getAppName());
            newApp.setProductLineId(args.getProductLineId());
            newApp.setLanguage(args.getAppLanguage());
            newApp.setDescription(args.getDescription());
            newApp.setAppLevel(args.getAppLevel());
            newApp.setGitRepoUrl(createResp.getHttpUrlToRepo());
            newApp.setGitProjectId(createResp.getProjectId());
            newApp.setGitDefaultBranch(createResp.getDefaultBranch());
            newApp.setGitCreateAt(createResp.getCreatedAt());
            newApp.setGitProjectName(args.getAppChName());
            save(newApp);

            User currUser = userService.findByName(SecurityUtil.getCurrentUsername());
            AppUser bindMember = new AppUser();
            bindMember.setAppId(newApp.getId());
            bindMember.setRoleCode(AppUserRoleEnum.OWNER.getCode());
            bindMember.setUserId(currUser.getId());
            appUserService.save(bindMember);
            productLineAppService.bindApp(newApp.getId(), args.getProductLineId());
        } else {
            Project gitlabProject = gitlabProjectRepository.describeProjectById(args.getProjectId());
            String appName = args.getAppName().trim();
            gitlabProjectRepository.initGitCiFile(gitlabProject.getId(), gitlabProject.getDefaultBranch(), args.getAppLanguage(), appName);

            App newApp = new App();
            newApp.setAppName(appName);
            newApp.setProductLineId(args.getProductLineId());
            newApp.setLanguage(args.getAppLanguage());
            newApp.setDescription(args.getDescription());
            newApp.setAppLevel(args.getAppLevel());
            newApp.setGitRepoUrl(gitlabProject.getHttpUrlToRepo());
            newApp.setGitProjectId(gitlabProject.getId());
            newApp.setGitDefaultBranch(gitlabProject.getDefaultBranch());
            newApp.setGitCreateAt(gitlabProject.getCreatedAt());
            newApp.setGitProjectName(args.getAppChName());
            save(newApp);

            User currUser = userService.findByName(SecurityUtil.getCurrentUsername());
            AppUser bindMember = new AppUser();
            bindMember.setAppId(newApp.getId());
            bindMember.setRoleCode(AppUserRoleEnum.OWNER.getCode());
            bindMember.setUserId(currUser.getId());
            appUserService.save(bindMember);
            productLineAppService.bindApp(newApp.getId(), args.getProductLineId());
        }
    }

    private boolean existByAppName(String appName) {
        return exists(new LambdaQueryWrapper<App>().eq(App::getAppName, appName));
    }

    @Override
    public IPage<App.QueryPage> queryPage(PageArgs<App> args) {
        App body = args.getBody();
        LambdaQueryWrapper<App> wrapper = new LambdaQueryWrapper<>();
        if (body != null) {
            if (StrUtil.isNotBlank(body.getAppName())) {
                wrapper.or(c -> c.like(App::getAppName, body.getAppName())
                        .or()
                        .like(App::getDescription, body.getAppName())
                );
            }
        }
        wrapper.orderByDesc(App::getId);

        User currUser = userService.findByName(SecurityUtil.getCurrentUsername());
        List<Long> appCollectIds = appUserCollectMapper.selectList(new LambdaQueryWrapper<AppUserCollect>().eq(AppUserCollect::getUserId, currUser.getId()))
                .stream()
                .map(AppUserCollect::getAppId)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        Page<App> page = page(new Page<>(args.getPage(), args.getPageSize()), wrapper);
        return page.convert(m -> {
            App.QueryPage queryPage = BeanUtil.copyProperties(m, App.QueryPage.class);
            queryPage.setLanguageDesc(AppLanguageEnum.getDescByCode(m.getLanguage()));
            queryPage.setAppLevelDesc(AppLevelEnum.getDescByCode(m.getAppLevel()));
            queryPage.setIsCollect(appCollectIds.contains(m.getId()));
            return queryPage;
        });
    }

    @Override
    public List<MetaOption> queryLevelList() {
        List<MetaOption> result = new ArrayList<>();
        for (AppLevelEnum level : AppLevelEnum.values()) {
            MetaOption option = new MetaOption();
            option.setLabel(level.getDesc());
            option.setValue(level.getCode());
            result.add(option);
        }
        return result;
    }

    @Override
    public List<MetaOption> queryLanguageList() {
        List<MetaOption> result = new ArrayList<>();
        for (AppLanguageEnum level : AppLanguageEnum.values()) {
            MetaOption option = new MetaOption();
            option.setLabel(level.getDesc());
            option.setValue(level.getCode());
            result.add(option);
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void changeCollect(App.ChangeCollectArgs args) {
        User currUser = userService.findByName(SecurityUtil.getCurrentUsername());
        if (args.getStatus()) {
            appUserCollectMapper.delete(new LambdaQueryWrapper<AppUserCollect>()
                    .eq(AppUserCollect::getAppId, args.getAppId())
                    .eq(AppUserCollect::getUserId, currUser.getId())
            );
            AppUserCollect record = new AppUserCollect();
            record.setUserId(currUser.getId());
            record.setAppId(args.getAppId());
            appUserCollectMapper.insert(record);
        } else {
            appUserCollectMapper.delete(new LambdaQueryWrapper<AppUserCollect>()
                    .eq(AppUserCollect::getAppId, args.getAppId())
                    .eq(AppUserCollect::getUserId, currUser.getId())
            );
        }
    }

    @Override
    public List<App.QueryPage> queryCollectList() {
        User currUser = userService.findByName(SecurityUtil.getCurrentUsername());
        List<App.QueryPage> queryPages = getBaseMapper().selectCollectList(currUser.getId());
        return queryPages.stream().map(m -> {
            App.QueryPage queryPage = BeanUtil.copyProperties(m, App.QueryPage.class);
            queryPage.setLanguageDesc(AppLanguageEnum.getDescByCode(m.getLanguage()));
            queryPage.setAppLevelDesc(AppLevelEnum.getDescByCode(m.getAppLevel()));
            return queryPage;
        }).collect(Collectors.toList());
    }

    @Override
    public List<MetaOption> queryRoleList() {
        List<MetaOption> result = new ArrayList<>();
        for (AppUserRoleEnum level : AppUserRoleEnum.values()) {
            MetaOption option = new MetaOption();
            option.setLabel(level.getDesc());
            option.setValue(level.getCode());
            result.add(option);
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void bindMember(App.BindMemberArgs args) {
        if (AppUserRoleEnum.OWNER.getCode().equals(args.getRoleCode()) && args.getUserIds().size() > 1) {
            throw new BadRequestException("应用负责人只能有一个");
        }
        boolean appUserExist = appUserService.existsBy(args.getAppId(), args.getRoleCode());
        if (AppUserRoleEnum.OWNER.getCode().equals(args.getRoleCode()) && appUserExist) {
            throw new BadRequestException("关联失败, 已绑定应用负责人, 只能进行转让操作");
        }
        List<Long> userIds = args.getUserIds();
        for (Long userId : userIds) {
            appUserService.removeBy(args.getAppId(), args.getRoleCode(), userId);
        }
        List<AppUser> records = new ArrayList<>();
        for (Long userId : userIds) {
            AppUser record = new AppUser();
            record.setAppId(args.getAppId());
            record.setUserId(userId);
            record.setRoleCode(args.getRoleCode());
            records.add(record);
        }
        appUserService.saveBatch(records);
    }

    @Override
    public Map<String, List<App.QueryMemberRoleGroup>> queryMemberRoleGroup(App.QueryMemberRoleGroup args) {
        List<App.QueryMemberRoleGroup> records = appUserService.queryMemberRoleGroup(args);
        return records.stream().collect(Collectors.groupingBy(App.QueryMemberRoleGroup::getRoleCode));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void unBindMember(App.UnBindMemberArgs args) {
        appUserService.removeBy(args.getAppId(), args.getRoleCode(), args.getUserId());
    }

    @Override
    public List<MetaOption> queryProjectUrlList(App.QueryProjectUrlListArgs args) {
        List<Project> projects = gitlabProjectRepository.listProjectsByAppName(args.getKey());
        return projects.stream().map(m -> MetaOption.builder()
                .label(m.getHttpUrlToRepo())
                .value(m.getId() + "")
                .build()).collect(Collectors.toList());
    }
}
