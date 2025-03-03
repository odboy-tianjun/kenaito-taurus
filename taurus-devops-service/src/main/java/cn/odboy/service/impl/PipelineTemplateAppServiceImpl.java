package cn.odboy.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.odboy.domain.PipelineTemplateApp;
import cn.odboy.domain.PipelineTemplateLanguageConfig;
import cn.odboy.exception.BadRequestException;
import cn.odboy.mybatisplus.model.PageArgs;
import cn.odboy.mapper.PipelineTemplateAppMapper;
import cn.odboy.service.PipelineTemplateAppService;
import cn.odboy.common.util.CollUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 应用各环境的流水线模板 服务实现类
 * </p>
 *
 * @author odboy
 * @since 2024-11-22
 */
@Service
public class PipelineTemplateAppServiceImpl extends ServiceImpl<PipelineTemplateAppMapper, PipelineTemplateApp> implements PipelineTemplateAppService {
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void configurePipeline(PipelineTemplateApp.CreateArgs args) {
        try {
            save(BeanUtil.copyProperties(args, PipelineTemplateApp.class));
        } catch (Exception e) {
            log.error("新增流水线配置失败", e);
            throw new BadRequestException("流水线已存在，请确认后再试");
        }
    }

    @Override
    public IPage<PipelineTemplateApp.QueryPage> queryTemplateDetailList(PageArgs<PipelineTemplateApp.QueryTemplateDetailArgs> args) {
        if (args == null || args.getBody() == null) {
            throw new BadRequestException("参数必填");
        }
        if (StrUtil.isBlank(args.getBody().getAppName())) {
            throw new BadRequestException("参数 appName 必填");
        }
        return getBaseMapper().selectTemplateDetailList(new Page<>(args.getPage(), args.getPageSize()), args.getBody());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void remove(PipelineTemplateApp.RemoveArgs args) {
        LambdaQueryWrapper<PipelineTemplateApp> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PipelineTemplateApp::getEnvCode, args.getEnvCode());
        wrapper.eq(PipelineTemplateApp::getAppName, args.getAppName());
        wrapper.eq(PipelineTemplateApp::getTemplateId, args.getTemplateId());
        wrapper.eq(PipelineTemplateApp::getTemplateLanguageId, args.getTemplateLanguageId());
        remove(wrapper);
    }

    @Override
    public PipelineTemplateApp.QueryAppDeployPipelineEnvConfigResp queryAppDeployPipelineEnvConfigList(PipelineTemplateApp.QueryAppDeployPipelineEnvConfigArgs args) {
        PipelineTemplateApp.QueryAppDeployPipelineEnvConfigResp resp = new PipelineTemplateApp.QueryAppDeployPipelineEnvConfigResp();
        List<String> envList = getBaseMapper().selectBindEnvPipelineListByAppName(args.getAppName());
        Map<String, List<PipelineTemplateLanguageConfig.AppPipelineNodeConfig>> envNodeConfigList = new HashMap<>(1);
        for (String envCode : envList) {
            List<PipelineTemplateLanguageConfig.AppPipelineNodeConfigBtnStr> nodeConfigs = getBaseMapper().selectAppDeployPipelineNodeConfigByEnvAndApp(envCode, args.getAppName());
            if (CollUtil.isEmpty(nodeConfigs)) {
                envNodeConfigList.put(envCode, new ArrayList<>());
            } else {
                List<PipelineTemplateLanguageConfig.AppPipelineNodeConfig> newNodeConfigs = new ArrayList<>();
                for (PipelineTemplateLanguageConfig.AppPipelineNodeConfigBtnStr nodeConfig : nodeConfigs) {
                    PipelineTemplateLanguageConfig.AppPipelineNodeConfig newNodeConfig = new PipelineTemplateLanguageConfig.AppPipelineNodeConfig();
                    newNodeConfig.setEnvCode(nodeConfig.getEnvCode());
                    newNodeConfig.setTemplateId(nodeConfig.getTemplateId());
                    newNodeConfig.setTemplateLanguageId(nodeConfig.getTemplateLanguageId());
                    newNodeConfig.setNodeName(nodeConfig.getNodeName());
                    newNodeConfig.setNodeType(nodeConfig.getNodeType());
                    newNodeConfig.setIsClick(nodeConfig.getIsClick());
                    newNodeConfig.setIsRetry(nodeConfig.getIsRetry());
                    newNodeConfig.setIsJudge(nodeConfig.getIsJudge());
                    if (StrUtil.isNotBlank(nodeConfig.getJudgeBtnList())) {
                        newNodeConfig.setJudgeBtnList(JSON.parseArray(nodeConfig.getJudgeBtnList(), PipelineTemplateLanguageConfig.AppPipelineNodeConfig.JudgeBtn.class));
                    }
                    newNodeConfig.setHandleMethod(nodeConfig.getHandleMethod());
                    newNodeConfig.setHandleParameters(nodeConfig.getHandleParameters());
                    newNodeConfig.setAppName(nodeConfig.getAppName());
                    newNodeConfig.setOrderNum(nodeConfig.getOrderNum());
                    newNodeConfigs.add(newNodeConfig);
                }
                envNodeConfigList.put(envCode, newNodeConfigs);
            }
        }
        resp.setEnvList(envList);
        resp.setEnvNodeConfigList(envNodeConfigList);
        return resp;
    }
}
