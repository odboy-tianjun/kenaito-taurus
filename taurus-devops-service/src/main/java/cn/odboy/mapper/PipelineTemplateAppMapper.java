package cn.odboy.mapper;

import cn.odboy.domain.PipelineTemplateApp;
import cn.odboy.domain.PipelineTemplateLanguageConfig;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 应用各环境的流水线模板 Mapper 接口
 * </p>
 *
 * @author odboy
 * @since 2024-11-22
 */
@Mapper
public interface PipelineTemplateAppMapper extends BaseMapper<PipelineTemplateApp> {
    IPage<PipelineTemplateApp.QueryPage> selectTemplateDetailList(Page<?> page, @Param("args") PipelineTemplateApp.QueryTemplateDetailArgs args);

    List<PipelineTemplateLanguageConfig.AppPipelineNodeConfigBtnStr> selectAppDeployPipelineNodeConfigByEnvAndApp(@Param("envCode") String envCode, @Param("appName") String appName);

    List<String> selectBindEnvPipelineListByAppName(@Param("appName") String appName);
}
