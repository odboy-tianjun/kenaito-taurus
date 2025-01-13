package me.zhengjie.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.Map;

/**
 * 用途: 主要用于无状态应用程序的部署和管理，例如 web 服务器、API 服务等。
 * 应用场景: 适用于需要快速扩展、无状态的服务，即使 Pod 在不同的节点上重启或被重新调度，也不会影响应用的正常运行。
 *
 * @author odboy
 * @date 2025-01-13
 */
public class K8sDeployment {
    /**
     * 创建Deployment
     */
    @Data
    @EqualsAndHashCode(callSuper = false)
    public static class CreateArgs extends MyObject {
        /**
         * 集群编码
         */
        @NotNull
        private String clusterCode;
        /**
         * 命名空间
         */
        @NotNull
        private String namespace;
        /**
         * 应用名称
         */
        @NotNull
        private String appName;
        /**
         * deployment注解
         */
        private Map<String, String> annotations;
        /**
         * 镜像地址
         */
        @NotNull
        private String image;
        /**
         * 副本数量
         */
        @NotNull
        private Integer replicas;
        /**
         * 容器服务端口号
         */
        @NotNull
        private Integer port;
        /**
         * 是否运行测试
         */
        private Boolean dryRun = false;
    }

    /**
     * 修改Deployment副本数量
     */
    @Data
    @EqualsAndHashCode(callSuper = false)
    public static class ChangeReplicasArgs extends MyObject {
        /**
         * 集群编码
         */
        @NotNull
        private String clusterCode;
        /**
         * 命名空间
         */
        @NotNull
        private String namespace;
        /**
         * 应用名称
         */
        @NotNull
        private String appName;
        /**
         * 新副本数量
         */
        @NotNull
        private Integer newReplicas;
        /**
         * 是否运行测试
         */
        private Boolean dryRun = false;
    }

    /**
     * 修改Deployment镜像地址
     */
    @Data
    @EqualsAndHashCode(callSuper = false)
    public static class ChangeImageArgs extends MyObject {
        /**
         * 集群编码
         */
        @NotNull
        private String clusterCode;
        /**
         * 命名空间
         */
        @NotNull
        private String namespace;
        /**
         * 应用名称
         */
        @NotNull
        private String appName;
        /**
         * 新镜像地址
         */
        @NotNull
        private String newImage;
        /**
         * 是否运行测试
         */
        private Boolean dryRun = false;
    }

    /**
     * 修改Deployment规格
     */
    @Data
    @EqualsAndHashCode(callSuper = false)
    public static class ChangePodSpecsArgs extends MyObject {
        /**
         * 集群编码
         */
        @NotNull
        private String clusterCode;
        /**
         * 命名空间
         */
        @NotNull
        private String namespace;
        /**
         * 应用名称
         */
        @NotNull
        private String appName;
        /**
         * CPU数
         */
        @NotNull
        @Min(value = 1)
        private Integer cpuNum;
        /**
         * 内存数
         */
        @NotNull
        @Min(value = 1)
        private Integer memoryNum;
        /**
         * 是否运行测试
         */
        private Boolean dryRun = false;
    }

    /**
     * 删除Deployment
     */
    @Data
    @EqualsAndHashCode(callSuper = false)
    public static class DeleteArgs extends MyObject {
        /**
         * 集群编码
         */
        @NotNull
        private String clusterCode;
        /**
         * 命名空间
         */
        @NotNull
        private String namespace;
        /**
         * deployment名称
         */
        @NotNull
        private String deploymentName;
        /**
         * 是否运行测试
         */
        private Boolean dryRun = false;
    }

    /**
     * 从yaml文件加载Deployment
     */
    @Data
    @EqualsAndHashCode(callSuper = false)
    public static class LoadFromYamlArgs extends MyObject {
        /**
         * 集群编码
         */
        @NotNull
        private String clusterCode;
        /**
         * 命名空间
         */
        @NotNull
        private String namespace;
        /**
         * yaml文件内容
         */
        @NotNull
        private String yamlContent;
        /**
         * 是否运行测试
         */
        private Boolean dryRun = false;
    }
}
