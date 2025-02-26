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
 * k8s集群配置
 * </p>
 *
 * @author odboy
 * @since 2024-11-15
 */
@Getter
@Setter
@TableName("devops_containerd_cluster_config")
public class ContainerdClusterConfig extends MyLogicEntity {
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
     * 集群编码
     */
    @TableField("cluster_code")
    private String clusterCode;

    /**
     * 集群名称
     */
    @TableField("cluster_name")
    private String clusterName;

    /**
     * 配置内容
     */
    @TableField("config_content")
    private String configContent;

    /**
     * 健康状态
     */
    @TableField("env_status")
    private Integer envStatus;

    @Data
    public static class QueryPage {
        private Long id;
        private String envCode;
        private String clusterCode;
        private String clusterName;
        private String configContent;
        private Integer envStatus;
    }

    @Data
    public static class CreateArgs {
        @NotBlank(message = "必填")
        private String envCode;
        @NotBlank(message = "必填")
        private String clusterCode;
        @NotBlank(message = "必填")
        private String clusterName;
        @NotBlank(message = "必填")
        private String configContent;
    }

    @Data
    public static class ModifyArgs {
        @NotNull(message = "必填")
        private Long id;
        @NotBlank(message = "必填")
        private String configContent;
    }

    @Data
    public static class RemoveArgs {
        @NotNull(message = "必填")
        private Long id;
    }
}
