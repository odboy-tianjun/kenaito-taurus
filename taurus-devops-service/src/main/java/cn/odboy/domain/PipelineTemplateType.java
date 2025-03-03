package cn.odboy.domain;

import cn.odboy.mybatisplus.model.MyLogicEntity;
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
 * 流水线模板类别
 * </p>
 *
 * @author odboy
 * @since 2024-11-22
 */
@Getter
@Setter
@TableName("devops_pipeline_template_type")
public class PipelineTemplateType extends MyLogicEntity {

    /**
     * 自增ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 模板名称
     */
    @TableField("template_name")
    private String templateName;

    /**
     * 模板编码(应用发布app_deploy模板、资源申请resource_apply模板等)
     */
    @TableField("template_code")
    private String templateCode;

    @Data
    public static class CreateArgs {
        @NotBlank(message = "必填")
        private String templateName;
        @NotBlank(message = "必填")
        private String templateCode;
    }

    @Data
    public static class RemoveArgs {
        @NotNull(message = "必填")
        private Long id;
    }

    @Data
    public static class QueryPage {
        private Long id;
        private String templateName;
        private String templateCode;
        @JSONField(format = "yyyy-MM-dd HH:mm:ss")
        @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private String createBy;
        @JSONField(format = "yyyy-MM-dd HH:mm:ss")
        @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private Date createTime;
    }
}
