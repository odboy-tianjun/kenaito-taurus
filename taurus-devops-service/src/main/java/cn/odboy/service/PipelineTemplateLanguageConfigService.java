package cn.odboy.service;

import cn.odboy.domain.PipelineTemplateLanguageConfig;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 流水线模板配置 服务类
 * </p>
 *
 * @author odboy
 * @since 2024-11-22
 */
public interface PipelineTemplateLanguageConfigService extends IService<PipelineTemplateLanguageConfig> {
    void existBindRelative(Long templateLanguageId, Long templateId);

    List<PipelineTemplateLanguageConfig> queryNodeList(PipelineTemplateLanguageConfig.QueryArgs args);

    void saveNodeConfig(List<PipelineTemplateLanguageConfig.AppPipelineNodeConfig> args);
}
