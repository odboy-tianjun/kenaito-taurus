package cn.odboy.service;

import cn.odboy.domain.PipelineTemplateApp;
import cn.odboy.model.PageArgs;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 应用各环境的流水线模板 服务类
 * </p>
 *
 * @author odboy
 * @since 2024-11-22
 */
public interface PipelineTemplateAppService extends IService<PipelineTemplateApp> {
    void configurePipeline(PipelineTemplateApp.CreateArgs args);

    IPage<PipelineTemplateApp.QueryPage> queryTemplateDetailList(PageArgs<PipelineTemplateApp.QueryTemplateDetailArgs> args);

    PipelineTemplateApp.QueryAppDeployPipelineEnvConfigResp queryAppDeployPipelineEnvConfigList(PipelineTemplateApp.QueryAppDeployPipelineEnvConfigArgs args);

    void remove(PipelineTemplateApp.RemoveArgs args);
}
