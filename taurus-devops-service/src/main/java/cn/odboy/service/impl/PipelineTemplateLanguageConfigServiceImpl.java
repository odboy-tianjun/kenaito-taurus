package cn.odboy.service.impl;

import cn.odboy.domain.PipelineTemplateLanguageConfig;
import cn.odboy.mapper.PipelineTemplateLanguageConfigMapper;
import cn.odboy.service.PipelineTemplateLanguageConfigService;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.ws.rs.BadRequestException;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 流水线模板配置 服务实现类
 * </p>
 *
 * @author odboy
 * @since 2024-11-22
 */
@Service
public class PipelineTemplateLanguageConfigServiceImpl extends ServiceImpl<PipelineTemplateLanguageConfigMapper, PipelineTemplateLanguageConfig> implements PipelineTemplateLanguageConfigService {

    @Override
    public void existBindRelative(Long templateLanguageId, Long templateId) {
        boolean exists = exists(new LambdaQueryWrapper<PipelineTemplateLanguageConfig>()
                .eq(PipelineTemplateLanguageConfig::getTemplateLanguageId, templateLanguageId)
                .eq(PipelineTemplateLanguageConfig::getTemplateId, templateId)
        );
        if (exists) {
            throw new BadRequestException("该流水线语言模板下存在流水线节点配置，无法删除");
        }
    }

    @Override
    public List<PipelineTemplateLanguageConfig> queryNodeList(PipelineTemplateLanguageConfig.QueryArgs args) {
        return list(new LambdaQueryWrapper<PipelineTemplateLanguageConfig>()
                .eq(PipelineTemplateLanguageConfig::getEnvCode, args.getEnvCode())
                .eq(PipelineTemplateLanguageConfig::getTemplateId, args.getTemplateId())
                .eq(PipelineTemplateLanguageConfig::getTemplateLanguageId, args.getTemplateLanguageId())
        );
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveNodeConfig(List<PipelineTemplateLanguageConfig.AppPipelineNodeConfig> args) {
        if (args == null || args.isEmpty()) {
            throw new BadRequestException("至少需要一个节点");
        }
        PipelineTemplateLanguageConfig.AppPipelineNodeConfig nodeConfig = args.stream().findFirst().orElse(null);
        remove(new LambdaQueryWrapper<PipelineTemplateLanguageConfig>()
                .eq(PipelineTemplateLanguageConfig::getEnvCode, nodeConfig.getEnvCode())
                .eq(PipelineTemplateLanguageConfig::getTemplateId, nodeConfig.getTemplateId())
                .eq(PipelineTemplateLanguageConfig::getTemplateLanguageId, nodeConfig.getTemplateLanguageId())
        );
        List<PipelineTemplateLanguageConfig> configs = new ArrayList<>(1);
        for (PipelineTemplateLanguageConfig.AppPipelineNodeConfig arg : args) {
            PipelineTemplateLanguageConfig config = new PipelineTemplateLanguageConfig();
            config.setEnvCode(arg.getEnvCode());
            config.setTemplateId(arg.getTemplateId());
            config.setTemplateLanguageId(arg.getTemplateLanguageId());
            config.setNodeName(arg.getNodeName());
            config.setNodeType(arg.getNodeType());
            config.setIsClick(arg.getIsClick());
            config.setIsRetry(arg.getIsRetry());
            config.setIsJudge(arg.getIsJudge());
            if (arg.getJudgeBtnList() != null && !arg.getJudgeBtnList().isEmpty()) {
                config.setJudgeBtnList(JSON.toJSONString(arg.getJudgeBtnList()));
            }
            config.setHandleMethod(arg.getHandleMethod());
            config.setHandleParameters(arg.getHandleParameters());
            config.setOrderNum(arg.getOrderNum());
            configs.add(config);
        }
        saveBatch(configs);
    }
}
