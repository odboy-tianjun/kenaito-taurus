package cn.odboy.modules.devops.domain;

import cn.odboy.base.MyEntity;
import cn.odboy.model.MyObject;
import cn.odboy.model.request.K8sStatefulSet;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * <p>
 * 应用列表
 * </p>
 *
 * @author codegen
 * @since 2025-02-11
 */
@Getter
@Setter
@TableName("devops_cmdb_app")
@ApiModel(value = "CmdbApp对象", description = "应用列表")
public class CmdbApp extends MyEntity {

    @ApiModelProperty("id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty("应用名称")
    @TableField("app_name")
    private String appName;

    @ApiModelProperty("所属产品线")
    @TableField("dept_id")
    private Long deptId;

    @ApiModelProperty("开发语言")
    @TableField("app_language")
    private String appLanguage;

    @ApiModelProperty("应用等级")
    @TableField("app_level")
    private String appLevel;

    @ApiModelProperty("GitGroupId")
    @TableField("gitlab_group_id")
    private Long gitlabGroupId;

    @ApiModelProperty("GitGroupName")
    @TableField("gitlab_group_name")
    private String gitlabGroupName;

    @ApiModelProperty("GtiProjectId")
    @TableField("gitlab_project_id")
    private Long gitlabProjectId;

    @ApiModelProperty("GitProjectName")
    @TableField("gitlab_project_name")
    private String gitlabProjectName;

    @ApiModelProperty("描述")
    @TableField("`description`")
    private String description;

    @Data
    @EqualsAndHashCode(callSuper = false)
    public static class CreateArgs extends MyObject {
        @NotBlank(message = "应用名称不能为空")
        private String appName;
        @NotNull(message = "产品线不能为空")
        private Long deptId;
        /**
         * java,python,go,dotnet,nodejs,empty
         */
        @NotNull(message = "应用语言不能为空")
        private String appLanguage;
        /**
         * 核心、非核心、三方
         */
        private String appLevel;
        /**
         * Git Group 来源：新建create、已有exist
         */
        private String gitlabGroupSource;
        private Long gitlabGroupId;
        private String gitlabGroupName;
        /**
         * Git Project 来源：新建create、已有exist
         */
        private String gitlabProjectSource;
        private Long gitlabProjectId;
        private String gitlabProjectName;
        @NotBlank(message = "描述不能为空")
        private String description;
//        /**
//         * 部署方式：k8s、host(主机，比如dotnet这种)
//         */
//        private String deployMode;
        /**
         * 申请资源列表
         */
        @NotNull(message = "资源不能为空")
        @Size(min = 1, message = "至少有一个环境的资源")
        private List<K8sStatefulSet.CreateArgs> requestResources;
    }
}
