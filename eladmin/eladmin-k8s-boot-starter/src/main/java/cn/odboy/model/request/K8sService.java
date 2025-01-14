package cn.odboy.model.request;

import lombok.Data;
import lombok.EqualsAndHashCode;
import cn.odboy.model.MyObject;

import javax.validation.constraints.NotBlank;
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
        @NotBlank(message = "集群编码不能为空")
        private String clusterCode;
        /**
         * 应用名称
         */
        @NotBlank(message = "应用名称不能为空")
        private String appName;
        /**
         * service注解
         */
        private Map<String, String> annotations;
        /**
         * 外部访问的端口号
         */
        @NotNull(message = "外部访问的端口号不能为空")
        private Integer port;
        /**
         * 应用服务端口号
         */
        @NotNull(message = "应用服务端口号不能为空")
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
