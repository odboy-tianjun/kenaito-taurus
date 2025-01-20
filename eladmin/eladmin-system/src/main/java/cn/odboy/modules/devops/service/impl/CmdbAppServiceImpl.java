package cn.odboy.modules.devops.service.impl;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import cn.odboy.constant.AppLanguageEnum;
import cn.odboy.model.CommonModel;
import cn.odboy.model.GitlabProject;
import cn.odboy.model.PageResult;
import cn.odboy.modules.devops.contanst.GitAppSourceEnum;
import cn.odboy.modules.devops.domain.CmdbApp;
import cn.odboy.modules.devops.mapper.CmdbAppMapper;
import cn.odboy.modules.devops.service.CmdbAppService;
import cn.odboy.repository.GitlabGroupRepository;
import cn.odboy.repository.GitlabProjectRepository;
import cn.odboy.repository.K8sStatefulSetRepository;
import cn.odboy.util.PageUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.gitlab4j.api.models.Group;
import org.gitlab4j.api.models.Project;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 * 应用列表 服务实现类
 * </p>
 *
 * @author codegen
 * @since 2025-02-11
 */
@Service
@RequiredArgsConstructor
public class CmdbAppServiceImpl extends ServiceImpl<CmdbAppMapper, CmdbApp> implements CmdbAppService {
    private final K8sStatefulSetRepository k8sStatefulSetRepository;
    private final GitlabProjectRepository gitlabProjectRepository;
    private final GitlabGroupRepository gitlabGroupRepository;

    @Override
    public PageResult<CmdbApp> searchApps(CmdbApp args, Page<CmdbApp> page) {
        LambdaQueryWrapper<CmdbApp> wrapper = new LambdaQueryWrapper<>();
        if (args != null) {
            wrapper.like(StrUtil.isNotBlank(args.getAppName()), CmdbApp::getAppName, args.getAppName());
            wrapper.eq(StrUtil.isNotBlank(args.getAppLanguage()), CmdbApp::getAppLanguage, args.getAppLanguage());
            wrapper.eq(StrUtil.isNotBlank(args.getAppLevel()), CmdbApp::getAppLevel, args.getAppLevel());
            wrapper.like(StrUtil.isNotBlank(args.getDescription()), CmdbApp::getDescription, args.getDescription());
        }
        return PageUtil.toPage(baseMapper.selectPage(page, wrapper));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createApp(CmdbApp.CreateArgs args) {
        CmdbApp cmdbApp = new CmdbApp();
        // 是否支持的语言
        AppLanguageEnum appLanguageEnum = AppLanguageEnum.getByCode(args.getAppLanguage());
        Assert.notNull(appLanguageEnum, "不支持 %s 语言", args.getAppLanguage());
        // 是否是空应用
        if (appLanguageEnum != null && !appLanguageEnum.getCode().equals(args.getAppLanguage())) {
            Assert.isTrue(args.getGitlabGroupId() != null, "请%sGitGroup", GitAppSourceEnum.Create.getCode().equals(args.getGitlabGroupSource()) ? "输入" : "选择");
            Assert.isTrue(args.getGitlabProjectId() != null, "请%sProject", GitAppSourceEnum.Create.getCode().equals(args.getGitlabGroupSource()) ? "输入" : "选择");
        }
        // 应用是否存在
        String newAppName = args.getAppName().trim();
        Project project = gitlabProjectRepository.describeProjectByProjectName(newAppName);
        Assert.isNull(project, "应用 %s 已存在", newAppName);
        // 新建或配置GitGroup
        if (GitAppSourceEnum.Create.getCode().equals(args.getGitlabGroupSource())) {
            Group localGroup = gitlabGroupRepository.describeGroupByGroupName(args.getGitlabGroupName());
            Assert.isNull(localGroup, "Git分组 %s 已存在");
            Group newGroup = gitlabGroupRepository.createGroup(args.getGitlabGroupName());
            cmdbApp.setGitlabGroupId(newGroup.getId());
            cmdbApp.setGitlabGroupName(newGroup.getName());
        } else {
            cmdbApp.setGitlabGroupId(args.getGitlabGroupId());
            cmdbApp.setGitlabGroupName(args.getGitlabGroupName());
        }
        // 新建或配置Git项目
        if (GitAppSourceEnum.Create.getCode().equals(args.getGitlabProjectSource())) {
            Project localProject = gitlabProjectRepository.describeProjectByProjectName(args.getGitlabProjectName());
            Assert.isNull(localProject, "Git项目 %s 已存在");
            GitlabProject.CreateArgs createArgs = new GitlabProject.CreateArgs();
            createArgs.setName(newAppName);
            createArgs.setAppName(newAppName);
            createArgs.setDescription(args.getDescription());
            createArgs.setGroupOrUserId(cmdbApp.getGitlabGroupId());
            GitlabProject.CreateResp createResp = gitlabProjectRepository.createProject(createArgs);
            cmdbApp.setGitlabProjectId(createResp.getProjectId());
            cmdbApp.setGitlabProjectName(createResp.getProjectName());
        } else {
            cmdbApp.setGitlabProjectId(args.getGitlabProjectId());
            cmdbApp.setGitlabProjectName(args.getGitlabProjectName());
        }
        // 保存记录
        cmdbApp.setAppName(newAppName);
        cmdbApp.setDeptId(args.getDeptId());
        cmdbApp.setAppLanguage(args.getAppLanguage());
        cmdbApp.setAppLevel(args.getAppLevel());
        cmdbApp.setDescription(args.getDescription());
        save(cmdbApp);
        // TODO 异步创建k8s资源
    }

    @Override
    public void offlineApp(CommonModel.IdArgs args) {
        // TODO 域名下线、删除容器、备份配置数据等
    }
}
