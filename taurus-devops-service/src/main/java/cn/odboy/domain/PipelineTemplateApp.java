package cn.odboy.domain;

import cn.odboy.model.MyObject;
import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 应用各环境的流水线模板
 * </p>
 *
 * @author odboy
 * @since 2024-11-22
 */
@Getter
@Setter
@TableName("devops_pipeline_template_app")
public class PipelineTemplateApp extends MyObject {

    /**
     * 环境编码
     */
    @TableField("env_code")
    private String envCode;

    /**
     * 应用名称
     */
    @TableField("app_name")
    private String appName;

    /**
     * 流水线模板id
     */
    @TableField("template_id")
    private Long templateId;

    /**
     * 流水线语言模板id
     */
    @TableField("template_language_id")
    private Long templateLanguageId;

    @CreatedBy
    @TableField(value = "create_by", fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建人", hidden = true)
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String createBy;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建时间", hidden = true)
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @Data
    public static class QueryPage {
        private String appName;
        private String envCode;
        private Long templateId;
        private Long templateLanguageId;
        private String appLanguage;
        private String templateName;
        private Date createTime;
        private String createBy;
    }

    @Data
    public static class CreateArgs {
        @NotBlank(message = "必填")
        private String envCode;
        @NotBlank(message = "必填")
        private String appName;
        @NotNull(message = "必填")
        private Long templateId;
        @NotNull(message = "必填")
        private Long templateLanguageId;
    }

    @Data
    public static class QueryTemplateDetailArgs {
        @NotBlank(message = "必填")
        private String appName;
    }

    @Data
    public static class RemoveArgs {
        @NotBlank(message = "必填")
        private String envCode;
        @NotBlank(message = "必填")
        private String appName;
        @NotNull(message = "必填")
        private Long templateId;
        @NotNull(message = "必填")
        private Long templateLanguageId;
    }

    @Data
    public static class QueryAppDeployPipelineEnvConfigArgs {
        @NotBlank(message = "必填")
        private String appName;
    }

    @Data
    public static class QueryAppDeployPipelineEnvConfigResp {
        private List<String> envList;
        private Map<String,List<PipelineTemplateLanguageConfig.AppPipelineNodeConfig>> envNodeConfigList;
    }
}
