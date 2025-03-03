package cn.odboy.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.odboy.constant.AppConfigEnum;
import cn.odboy.constant.AppIterationChangeStatusEnum;
import cn.odboy.constant.AppIterationStatusEnum;
import cn.odboy.constant.BranchNameConst;
import cn.odboy.domain.AppIteration;
import cn.odboy.domain.AppIterationChange;
import cn.odboy.exception.BadRequestException;
import cn.odboy.mybatisplus.model.PageArgs;
import cn.odboy.mapper.AppIterationChangeMapper;
import cn.odboy.repository.GitlabProjectBranchRepository;
import cn.odboy.service.AppIterationChangeService;
import cn.odboy.service.AppIterationService;
import cn.odboy.util.IterationHelper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.gitlab4j.api.models.Branch;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * <p>
 * 应用迭代变更列表 服务实现类
 * </p>
 *
 * @author odboy
 * @since 2024-11-15
 */
@Service
@RequiredArgsConstructor
public class AppIterationChangeServiceImpl extends ServiceImpl<AppIterationChangeMapper, AppIterationChange> implements AppIterationChangeService {
    private final AppIterationService appIterationService;
    private final GitlabProjectBranchRepository gitlabProjectBranchRepository;

    @Override
    public IPage<AppIterationChange.QueryPage> queryPage(PageArgs<AppIterationChange> args) {
        AppIterationChange body = args.getBody();
        if (body == null) {
            throw new BadRequestException("查询条件为空");
        }
        if (StrUtil.isBlank(args.getBody().getAppName())) {
            throw new BadRequestException("appName为空");
        }
        LambdaQueryWrapper<AppIterationChange> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AppIterationChange::getAppName, body.getAppName());
        wrapper.eq(AppIterationChange::getCiStatus, AppIterationChangeStatusEnum.ENABLE.getCode());
        wrapper.orderByDesc(AppIterationChange::getId);
        Page<AppIterationChange> page = page(new Page<>(args.getPage(), args.getPageSize()), wrapper);
        return page.convert(m -> BeanUtil.copyProperties(m, AppIterationChange.QueryPage.class));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void create(AppIterationChange.CreateArgs args) {
        Long iterationId = args.getIterationId();
        String iterationChangeName = args.getTitle().trim();
        AppIteration appIteration = appIterationService.queryById(iterationId);
        if (appIteration == null) {
            throw new BadRequestException("无效迭代记录，请重新进入应用迭代页面");
        }
        Date nowTime = new Date();
        String pinyin = IterationHelper.getPinyin(iterationChangeName);

        // 版本号
        String iterationChangeVersion = DateUtil.format(nowTime, "yyyyMMddHHmmss");

        // 变更记录
        AppIterationChange newRecord = new AppIterationChange();
        String branchName = String.format(BranchNameConst.FEATURE, pinyin, iterationChangeVersion);
        Branch gitlabBranch;
        try {
            // 获取变更分支
            gitlabBranch = gitlabProjectBranchRepository.describeBranchByProjectName(appIteration.getAppName(), branchName);
        } catch (Exception e) {
            // 新建变更分支
            gitlabBranch = gitlabProjectBranchRepository.createBranchByProjectName(appIteration.getAppName(), branchName, AppConfigEnum.DEFAULT_BRANCH.getCode());
            if (existByBranchName(iterationId, branchName, appIteration.getAppName(), iterationChangeVersion)) {
                newRecord.setIterationId(iterationId);
                newRecord.setTitle(iterationChangeName);
                newRecord.setFeatureBranchName(branchName);
                newRecord.setChangeCode(pinyin);
                newRecord.setChangeVersion(iterationChangeVersion);
                newRecord.setWebUrl(gitlabBranch.getWebUrl());
                newRecord.setAppName(appIteration.getAppName());
                save(newRecord);
            }
        }
        if (gitlabBranch != null) {
            if (existByBranchName(iterationId, branchName, appIteration.getAppName(), iterationChangeVersion)) {
                newRecord.setIterationId(iterationId);
                newRecord.setTitle(iterationChangeName);
                newRecord.setFeatureBranchName(branchName);
                newRecord.setChangeCode(pinyin);
                newRecord.setChangeVersion(iterationChangeVersion);
                newRecord.setWebUrl(gitlabBranch.getWebUrl());
                newRecord.setAppName(appIteration.getAppName());
                save(newRecord);
            }
        }
    }

    @Override
    public IPage<AppIterationChange.QueryPage> queryUnChangePage(PageArgs<AppIterationChange> args) {
        AppIterationChange body = args.getBody();
        if (body == null) {
            throw new BadRequestException("查询条件为空");
        }
        if (StrUtil.isBlank(args.getBody().getAppName())) {
            throw new BadRequestException("appName为空");
        }
        LambdaQueryWrapper<AppIterationChange> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AppIterationChange::getAppName, body.getAppName());
        wrapper.eq(AppIterationChange::getCiStatus, AppIterationChangeStatusEnum.DISABLE.getCode());
        wrapper.orderByDesc(AppIterationChange::getId);
        Page<AppIterationChange> page = page(new Page<>(args.getPage(), args.getPageSize()), wrapper);
        return page.convert(m -> BeanUtil.copyProperties(m, AppIterationChange.QueryPage.class));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void exitCiArea(AppIterationChange.ExistCiAreaArgs args) {
        AppIterationChange appIterationChange = getById(args.getId());
        if (appIterationChange == null) {
            throw new BadRequestException("退出集成区失败");
        }
        AppIteration appIteration = appIterationService.getById(appIterationChange.getIterationId());
        if (AppIterationStatusEnum.FINISH.getCode().equals(appIteration.getIterationStatus())) {
            throw new BadRequestException("迭代已完成，无法退出集成区");
        }
        AppIterationChange updRecord = new AppIterationChange();
        updRecord.setId(args.getId());
        updRecord.setCiStatus(AppIterationChangeStatusEnum.DISABLE.getCode());
        updateById(updRecord);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void joinCiArea(AppIterationChange.JoinCiAreaArgs args) {
        AppIterationChange appIterationChange = getById(args.getId());
        if (appIterationChange == null) {
            throw new BadRequestException("加入集成区失败");
        }
        AppIteration appIteration = appIterationService.getById(appIterationChange.getIterationId());
        if (AppIterationStatusEnum.FINISH.getCode().equals(appIteration.getIterationStatus())) {
            throw new BadRequestException("迭代已完成，无法加入集成区");
        }
        AppIterationChange updRecord = new AppIterationChange();
        updRecord.setId(args.getId());
        updRecord.setCiStatus(AppIterationChangeStatusEnum.ENABLE.getCode());
        updateById(updRecord);
    }

    private boolean existByBranchName(Long iterationId, String branchName, String appName, String iterationChangeVersion) {
        return !exists(new LambdaQueryWrapper<AppIterationChange>()
                .eq(AppIterationChange::getIterationId, iterationId)
                .eq(AppIterationChange::getFeatureBranchName, branchName)
                .eq(AppIterationChange::getAppName, appName)
                .eq(AppIterationChange::getChangeVersion, iterationChangeVersion)
        );
    }
}
