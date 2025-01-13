package me.zhengjie.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;
import java.util.Map;

public class K8sIngress {

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
         * ingress注解
         */
        private Map<String, String> annotations;
        /**
         * 匹配的路径
         */
        @NotNull
        private String path;
        /**
         * 绑定的域名
         */
        private String hostname;
        /**
         * 路由到的服务名称
         */
        @NotNull
        private String serviceName;
        /**
         * 路由到的服务端口
         */
        @NotNull
        private Integer servicePort;
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
