package me.zhengjie.model.request;

import lombok.Data;
import lombok.EqualsAndHashCode;
import me.zhengjie.model.MyObject;

import javax.validation.constraints.NotBlank;

public class K8sPod {
    @Data
    @EqualsAndHashCode(callSuper = false)
    public static class RebuildArgs extends MyObject {
        /**
         * 集群编码
         */
        @NotBlank(message = "集群编码不能为空")
        private String clusterCode;
        /**
         * 命名空间
         */
        @NotBlank(message = "命名空间不能为空")
        private String namespace;
        /**
         * pod名称
         */
        @NotBlank(message = "pod名称不能为空")
        private String podName;
        /**
         * 是否运行测试
         */
        private Boolean dryRun = false;
    }
}
