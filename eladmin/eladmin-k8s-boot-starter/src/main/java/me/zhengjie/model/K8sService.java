package me.zhengjie.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;

public class K8sService {
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
         * service注解
         */
        private Map<String, String> annotations;
        /**
         * 外部访问的端口号
         */
        @NotNull
        private Integer port;
        /**
         * 应用服务端口号
         */
        @NotNull
        private Integer targetPort;
        /**
         * pod标签选择器
         */
        private Map<String, String> labelSelector = new HashMap<>(1);
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
