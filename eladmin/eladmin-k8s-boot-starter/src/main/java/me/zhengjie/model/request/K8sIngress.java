package me.zhengjie.model.request;

import lombok.Data;
import lombok.EqualsAndHashCode;
import me.zhengjie.model.MyObject;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Map;

public class K8sIngress {

    @Data
    @EqualsAndHashCode(callSuper = false)
    public static class CreateArgs extends MyObject {
        /**
         * 集群编码
         */
        @NotBlank(message = "集群编码不能为空")
        private String clusterCode;
        /**
         * 应用名称
         */
        @NotBlank(message = "应用名称不能为空")
        private String appName;
        /**
         * ingress注解
         */
        private Map<String, String> annotations;
        /**
         * 匹配的路径
         */
        @NotBlank(message = "匹配的路径不能为空")
        private String path;
        /**
         * 绑定的域名
         */
        @NotBlank(message = "绑定的域名不能为空")
        private String hostname;
        /**
         * 路由到的服务名称
         */
        @NotBlank(message = "路由到的服务名称不能为空")
        private String serviceName;
        /**
         * 路由到的服务端口
         */
        @NotNull(message = "路由到的服务端口不能为空")
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
        @NotBlank(message = "集群编码不能为空")
        private String clusterCode;
        /**
         * 应用名称
         */
        @NotBlank(message = "应用名称不能为空")
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
        @NotBlank(message = "集群编码不能为空")
        private String clusterCode;
        /**
         * yaml文件内容
         */
        @NotBlank(message = "yaml文件内容不能为空")
        private String yamlContent;
        /**
         * 是否运行测试
         */
        private Boolean dryRun = false;
    }
}
