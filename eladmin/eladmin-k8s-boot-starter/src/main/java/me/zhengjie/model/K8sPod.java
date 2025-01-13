package me.zhengjie.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

public class K8sPod {
    @Data
    @EqualsAndHashCode(callSuper = false)
    public static class RebuildArgs extends MyObject {
        @NotNull
        private String clusterCode;
        @NotNull
        private String namespace;
        @NotNull
        private String podName;
        /**
         * 是否运行测试
         */
        private Boolean dryRun = false;
    }
}
