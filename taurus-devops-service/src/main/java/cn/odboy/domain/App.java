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
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 应用
 * </p>
 *
 * @author odboy
 * @since 2024-09-13
 */
@Getter
@Setter
@TableName("devops_app")
public class App extends MyLogicEntity {
    /**
     * 自增ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 应用名称
     */
    @TableField("app_name")
    private String appName;

    /**
     * 所属产品线
     */
    @TableField("product_line_id")
    private Long productLineId;

    /**
     * 开发语言
     */
    @TableField("`language`")
    private String language;

    /**
     * 应用描述
     */
    @TableField("`description`")
    private String description;

    /**
     * 应用等级
     */
    @TableField("app_level")
    private String appLevel;

    /**
     * git仓库地址
     */
    @TableField("git_repo_url")
    private String gitRepoUrl;

    /**
     * git项目id
     */
    @TableField("git_project_id")
    private Long gitProjectId;

    /**
     * git项目默认分支
     */
    @TableField("git_default_branch")
    private String gitDefaultBranch;

    /**
     * git项目创建时间
     */
    @TableField("git_create_at")
    private Date gitCreateAt;

    /**
     * git项目中文名称
     */
    @TableField("git_project_name")
    private String gitProjectName;

    @Data
    public static class QueryPage {
        private Long id;
        private String appName;
        private String gitProjectName;
        private Long productLineId;
        private String productLineName;
        private String appLevel;
        private String appLevelDesc;
        private String language;
        private String languageDesc;
        private String description;
        private String gitRepoUrl;
        private Date createTime;
        private Boolean isCollect;
    }

    @Data
    public static class CreateArgs {
        @NotNull(message = "必填")
        private Long productLineId;
        private String appChName;
        private Long projectId;
        @NotBlank(message = "必填")
        private String appName;
        @NotBlank(message = "必填")
        private String appLevel;
        @NotBlank(message = "必填")
        private String appLanguage;
        private String description;
    }

    @Data
    public static class ChangeCollectArgs {
        @NotNull(message = "必填")
        private Long appId;
        @NotNull(message = "必填")
        private Boolean status;
    }

    @Data
    public static class BindMemberArgs {
        @NotNull(message = "必填")
        private Long appId;
        @NotBlank(message = "必填")
        private String roleCode;
        @NotNull(message = "必填")
        private List<Long> userIds;
    }

    @Data
    public static class UnBindMemberArgs {
        @NotNull(message = "必填")
        private Long appId;
        @NotBlank(message = "必填")
        private String roleCode;
        @NotNull(message = "必填")
        private Long userId;
    }

    @Data
    public static class QueryMemberRoleGroup {
        @NotNull(message = "必填")
        private Long id;
        private String roleCode;
        private Long userId;
        private String username;
        private String nickName;
    }

    @Data
    public static class OfflineArgs {
    }

    @Data
    public static class QueryProjectUrlListArgs {
        private String key;
    }
}
