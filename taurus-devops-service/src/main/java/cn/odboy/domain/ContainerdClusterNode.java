package cn.odboy.domain;

import cn.odboy.mybatisplus.model.MyLogicEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 容器集群node节点
 * </p>
 *
 * @author odboy
 * @since 2024-11-18
 */
@Getter
@Setter
@TableName("devops_containerd_cluster_node")
public class ContainerdClusterNode extends MyLogicEntity {

    /**
     * 自增ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 集群配置id
     */
    @TableField("cluster_config_id")
    private Long clusterConfigId;

    /**
     * 环境编码
     */
    @TableField("env_code")
    private String envCode;

    /**
     * 节点名称
     */
    @TableField("node_name")
    private String nodeName;

    /**
     * 节点状态
     */
    @TableField("node_status")
    private String nodeStatus;

    /**
     * 节点所属角色列表
     */
    @TableField("node_roles")
    private String nodeRoles;

    /**
     * 节点存活时间
     */
    @TableField("node_age")
    private String nodeAge;

    /**
     * 节点上k8s工具版本
     */
    @TableField("node_k8s_version")
    private String nodeK8sVersion;

    /**
     * 节点内部ip
     */
    @TableField("node_internal_ip")
    private String nodeInternalIp;

    /**
     * 节点主机名称
     */
    @TableField("node_hostname")
    private String nodeHostname;

    /**
     * 节点上所使用系统名称
     */
    @TableField("node_os_image")
    private String nodeOsImage;

    /**
     * 节点上所使用系统内核版本
     */
    @TableField("node_os_kernel_version")
    private String nodeOsKernelVersion;

    /**
     *
     */
    @TableField("node_os_architecture")
    private String nodeOsArchitecture;

    /**
     * 节点上所使用运行时
     */
    @TableField("node_container_runtime")
    private String nodeContainerRuntime;

    /**
     * 节点上pod的cidr
     */
    @TableField("node_pod_cidr")
    private String nodePodCidr;

    /**
     * 节点上pod的数量
     */
    @TableField("node_pod_size")
    private Integer nodePodSize;
}
