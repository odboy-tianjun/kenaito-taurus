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
import java.util.Date;

/**
 * <p>
 * 应用迭代记录
 * </p>
 *
 * @author odboy
 * @since 2024-11-15
 */
@Getter
@Setter
@TableName("devops_app_iteration")
public class AppIteration extends MyLogicEntity {

    /**
     * 自增ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 迭代名称
     */
    @TableField("iteration_name")
    private String iterationName;

    /**
     * 迭代编码
     */
    @TableField("iteration_code")
    private String iterationCode;

    /**
     * 迭代状态（1、未开始 2、进行中 3、已完成）
     */
    @TableField("iteration_status")
    private Integer iterationStatus;

    /**
     * 完成时间
     */
    @TableField("finish_time")
    private Date finishTime;

    /**
     * 版本号
     */
    @TableField("iteration_version")
    private String iterationVersion;

    /**
     * 发布分支的名称
     */
    @TableField("release_branch_name")
    private String releaseBranchName;

    /**
     * 应用名称
     */
    @TableField("app_name")
    private String appName;

    /**
     * 应用名称
     */
    @TableField("web_url")
    private String webUrl;

    @Data
    public static class QueryPage {
        private Long id;
        private String iterationName;
        private Integer iterationStatus;
        private Date finishTime;
        private String iterationVersion;
        private String releaseBranchName;
        @NotBlank(message = "必填")
        private String appName;
        private Date createTime;
        private String webUrl;
    }

    @Data
    public static class CreateArgs {
        @NotBlank(message = "必填")
        private String iterationName;
        @NotBlank(message = "必填")
        private String appName;
    }
}
