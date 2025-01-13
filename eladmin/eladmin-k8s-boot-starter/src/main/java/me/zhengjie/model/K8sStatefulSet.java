package me.zhengjie.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;
import java.util.Map;

public class K8sStatefulSet {
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
         * statefulset注解
         */
        private Map<String, String> annotations;
        /**
         * 副本数量
         */
        @NotNull
        private Integer replicas;
        /**
         * 镜像地址
         */
        @NotNull
        private String image;
        /**
         * 容器服务端口号
         */
        @NotNull
        private Integer port;
        /**
         * 需求cpu
         */
        @NotNull
        private Integer requestCpuNum;
        /**
         * 需求memory数
         */
        @NotNull
        private Integer requestMemNum;
        /**
         * cpu上限
         */
        @NotNull
        private Integer limitsCpuNum;
        /**
         * memory上限
         */
        @NotNull
        private Integer limitsMemNum;

        /**
         * 是否运行测试
         */
        private Boolean dryRun = false;
    }

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

    @Data
    @EqualsAndHashCode(callSuper = false)
    public static class ChangeSpecsArgs extends MyObject {
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
         * 需求cpu
         */
        @NotNull
        private Integer requestCpuNum;
        /**
         * 需求memory数
         */
        @NotNull
        private Integer requestMemNum;
        /**
         * cpu上限
         */
        @NotNull
        private Integer limitsCpuNum;
        /**
         * memory上限
         */
        @NotNull
        private Integer limitsMemNum;
        /**
         * 是否运行测试
         */
        private Boolean dryRun = false;
    }

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
         * 应用名称
         */
        @NotNull
        private String appName;
        /**
         * 是否运行测试
         */
        private Boolean dryRun = false;
    }

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
         * 应用名称
         */
        @NotNull
        private String yamlContent;
        /**
         * 是否运行测试
         */
        private Boolean dryRun = false;
    }
}
