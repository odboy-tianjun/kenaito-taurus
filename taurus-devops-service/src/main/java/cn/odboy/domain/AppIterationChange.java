package cn.odboy.domain;

import cn.odboy.base.MyLogicEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * <p>
 * 应用迭代变更列表
 * </p>
 *
 * @author odboy
 * @since 2024-11-15
 */
@Getter
@Setter
@TableName("devops_app_iteration_change")
public class AppIterationChange extends MyLogicEntity {
    /**
     * 自增ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 迭代记录id
     */
    @TableField("iteration_id")
    private Long iterationId;

    /**
     * 变更标题
     */
    @TableField("title")
    private String title;

    /**
     * 变更分支名称
     */
    @TableField("feature_branch_name")
    private String featureBranchName;

    /**
     * 变更编码
     */
    @TableField("change_code")
    private String changeCode;

    /**
     * 变更版本
     */
    @TableField("change_version")
    private String changeVersion;

    /**
     * 分支url
     */
    @TableField("web_url")
    private String webUrl;

    /**
     * 应用名称
     */
    @TableField("app_name")
    private String appName;

    /**
     * 是否集成
     */
    @TableField("ci_status")
    private Integer ciStatus;

    @Data
    public static class QueryPage {
        private Long id;
        private Long iterationId;
        private String title;
        private String featureBranchName;
        private String changeCode;
        private String changeVersion;
        private String webUrl;
        private String appName;
        private Integer ciStatus;
    }

    @Data
    public static class CreateArgs {
        @NotNull(message = "必填")
        private Long iterationId;
        @NotBlank(message = "必填")
        private String title;
    }

    @Data
    public static class ExistCiAreaArgs {
        @NotNull(message = "必填")
        private Long id;
    }

    @Data
    public static class JoinCiAreaArgs {
        @NotNull(message = "必填")
        private Long id;
    }
}
