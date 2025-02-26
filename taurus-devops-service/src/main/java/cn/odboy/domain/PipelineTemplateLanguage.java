package cn.odboy.domain;

import cn.odboy.base.MyLogicEntity;
import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * <p>
 * 流水线各语言模版
 * </p>
 *
 * @author odboy
 * @since 2024-11-22
 */
@Getter
@Setter
@TableName("devops_pipeline_template_language")
public class PipelineTemplateLanguage extends MyLogicEntity {
    /**
     * 自增ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 流水线模版id
     */
    @TableField("template_id")
    private Long templateId;

    /**
     * 环境编码
     */
    @TableField("env_code")
    private String envCode;

    /**
     * 应用语言
     */
    @TableField("app_language")
    private String appLanguage;

    /**
     * 模版名称
     */
    @TableField("template_name")
    private String templateName;

    @Data
    public static class QueryArgs {
        @NotNull(message = "必填")
        private Long templateId;
        @NotBlank(message = "必填")
        private String envCode;
        @NotBlank(message = "必填")
        private String appLanguage;
    }

    @Data
    public static class QueryPage {
        private Long id;
        private Long templateId;
        private String envCode;
        private String appLanguage;
        private String templateName;
        @JSONField(format = "yyyy-MM-dd HH:mm:ss")
        @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private String createBy;
        @JSONField(format = "yyyy-MM-dd HH:mm:ss")
        @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private Date createTime;
    }

    @Data
    public static class CreateArgs {
        @NotNull(message = "必填")
        private Long templateId;
        @NotBlank(message = "必填")
        private String envCode;
        @NotBlank(message = "必填")
        private String appLanguage;
        @NotBlank(message = "必填")
        private String templateName;
    }

    @Data
    public static class RemoveArgs {
        @NotNull(message = "必填")
        private Long id;
    }
}
