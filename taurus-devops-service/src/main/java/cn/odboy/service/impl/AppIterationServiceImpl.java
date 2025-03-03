package cn.odboy.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.odboy.constant.AppConfigEnum;
import cn.odboy.constant.AppIterationStatusEnum;
import cn.odboy.constant.BranchNameConst;
import cn.odboy.domain.AppIteration;
import cn.odboy.exception.BadRequestException;
import cn.odboy.mapper.AppIterationMapper;
import cn.odboy.mybatisplus.model.PageArgs;
import cn.odboy.repository.GitlabProjectBranchRepository;
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
 * 应用迭代记录 服务实现类
 * </p>
 *
 * @author odboy
 * @since 2024-11-15
 */
@Service
@RequiredArgsConstructor
public class AppIterationServiceImpl extends ServiceImpl<AppIterationMapper, AppIteration> implements AppIterationService {
    private final GitlabProjectBranchRepository gitlabProjectBranchRepository;

    @Override
    public IPage<AppIteration.QueryPage> queryPage(PageArgs<AppIteration> args) {
        AppIteration body = args.getBody();
        if (body == null) {
            throw new BadRequestException("查询条件为空");
        }
        if (StrUtil.isBlank(args.getBody().getAppName())) {
            throw new BadRequestException("appName为空");
        }
        LambdaQueryWrapper<AppIteration> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AppIteration::getAppName, body.getAppName());
        wrapper.like(StrUtil.isNotBlank(body.getIterationName()), AppIteration::getIterationName, body.getIterationName());
        wrapper.eq(body.getIterationStatus() != null, AppIteration::getIterationName, body.getIterationName());
        wrapper.orderByDesc(AppIteration::getId);
        Page<AppIteration> page = page(new Page<>(args.getPage(), args.getPageSize()), wrapper);
        return page.convert(m -> BeanUtil.copyProperties(m, AppIteration.QueryPage.class));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void create(AppIteration.CreateArgs args) {
        Date nowTime = new Date();
        String iterationName = args.getIterationName().trim();
        String pinyin = IterationHelper.getPinyin(iterationName);

        // 版本号
        String iterationVersion = DateUtil.format(nowTime, "yyyyMMddHHmmss");

        // 迭代记录
        AppIteration newRecord = new AppIteration();
        String branchName = String.format(BranchNameConst.RELEASE, pinyin, iterationVersion);
        Branch gitlabBranch;
        try {
            // 获取发布分支
            gitlabBranch = gitlabProjectBranchRepository.describeBranchByProjectName(args.getAppName(), branchName);
        } catch (Exception e) {
            // 新建发布分支
            gitlabBranch = gitlabProjectBranchRepository.createBranchByProjectName(args.getAppName(), branchName, AppConfigEnum.DEFAULT_BRANCH.getCode());
            if (existByBranchName(branchName, args.getAppName(), iterationVersion)) {
                newRecord.setIterationName(iterationName);
                newRecord.setIterationCode(pinyin);
                newRecord.setIterationStatus(AppIterationStatusEnum.READY.getCode());
                newRecord.setIterationVersion(iterationVersion);
                newRecord.setReleaseBranchName(gitlabBranch.getName());
                newRecord.setAppName(args.getAppName());
                newRecord.setWebUrl(gitlabBranch.getWebUrl());
                save(newRecord);
            }
        }
        if (gitlabBranch != null) {
            if (existByBranchName(branchName, args.getAppName(), iterationVersion)) {
                newRecord.setIterationName(iterationName);
                newRecord.setIterationCode(pinyin);
                newRecord.setIterationStatus(AppIterationStatusEnum.READY.getCode());
                newRecord.setIterationVersion(iterationVersion);
                newRecord.setReleaseBranchName(gitlabBranch.getName());
                newRecord.setAppName(args.getAppName());
                newRecord.setWebUrl(gitlabBranch.getWebUrl());
                save(newRecord);
            }
        }
    }

    @Override
    public AppIteration queryById(Long iterationId) {
        return getById(iterationId);
    }

    private boolean existByBranchName(String branchName, String appName, String iterationVersion) {
        return !exists(new LambdaQueryWrapper<AppIteration>()
                .eq(AppIteration::getAppName, appName)
                .eq(AppIteration::getReleaseBranchName, branchName)
                .eq(AppIteration::getIterationVersion, iterationVersion)
        );
    }
}
