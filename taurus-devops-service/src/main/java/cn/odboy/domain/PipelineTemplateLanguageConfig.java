package cn.odboy.domain;

import cn.odboy.mybatisplus.model.MyLogicEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * <p>
 * 流水线模板配置
 * </p>
 *
 * @author odboy
 * @since 2024-11-22
 */
@Getter
@Setter
@TableName("devops_pipeline_template_language_config")
public class PipelineTemplateLanguageConfig extends MyLogicEntity {
    /**
     * 自增ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 环境编码
     */
    @TableField("env_code")
    private String envCode;

    /**
     * 流水线模板id
     */
    @TableField("template_id")
    private Long templateId;

    /**
     * 流水线模板语言id
     */
    @TableField("template_language_id")
    private Long templateLanguageId;

    /**
     * 节点名称
     */
    @TableField("node_name")
    private String nodeName;

    /**
     * 节点类型(调用服务 service、跳转三方地址 jumpUrl)
     */
    @TableField("node_type")
    private String nodeType;

    /**
     * 是否可以点击(true、是 false、否)
     */
    @TableField("is_click")
    private Boolean isClick;

    /**
     * 是否可以重试(true、是 false、否)
     */
    @TableField("is_retry")
    private Boolean isRetry;

    /**
     * 是否存在交互卡点(true、是 false、否)
     */
    @TableField("is_judge")
    private Boolean isJudge;

    /**
     * 交互卡点中按钮的标题和执行的方法
     */
    @TableField
    private String judgeBtnList;

    /**
     * 执行的方法名称
     */
    @TableField("handle_method")
    private String handleMethod;

    /**
     * 所需参数列表
     */
    @TableField("handle_parameters")
    private String handleParameters;

    /**
     * 排序序号
     */
    @TableField("order_num")
    private Integer orderNum;

    @Data
    public static class QueryArgs {
        @NotBlank(message = "必填")
        private String envCode;
        @NotNull(message = "必填")
        private Long templateId;
        @NotNull(message = "必填")
        private Long templateLanguageId;
    }

    /**
     * 应用流水线节点配置
     */
    @Data
    public static class AppPipelineNodeConfig {
        private Long id;
        private String envCode;
        private Long templateId;
        private Long templateLanguageId;
        private String nodeName;
        private String nodeType;
        private Boolean isClick;
        private Boolean isRetry;
        private Boolean isJudge;
        private List<JudgeBtn> judgeBtnList;
        private String handleMethod;
        private String handleParameters;
        private String appName;
        private Integer orderNum;

        /**
         * 交互按钮
         */
        @Data
        public static class JudgeBtn {
            /**
             * 标题
             */
            private String title;
            /**
             * 颜色 primary / success / warning / danger / info
             */
            private String color = "info";
            /**
             * 类型：httpGet、service
             */
            private String type;
            /**
             * 当类型为service时
             */
            private String handleMethod;
            /**
             * 当类型为httpGet时
             */
            private String requestUrl;
        }
    }

    @Data
    public static class AppPipelineNodeConfigBtnStr {
        private Long id;
        private String envCode;
        private Long templateId;
        private Long templateLanguageId;
        private String nodeName;
        private String nodeType;
        private Boolean isClick;
        private Boolean isRetry;
        private Boolean isJudge;
        private String judgeBtnList;
        private String handleMethod;
        private String handleParameters;
        private String appName;
        private Integer orderNum;

    }
}
