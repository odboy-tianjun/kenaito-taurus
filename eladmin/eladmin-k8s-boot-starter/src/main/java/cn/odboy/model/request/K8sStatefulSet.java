/*
 *  Copyright 2021-2025 Tian Jun
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package cn.odboy.model.request;

import lombok.Data;
import lombok.EqualsAndHashCode;
import cn.odboy.model.MyObject;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Map;

/**
 * K8s StatefulSet
 *
 * @author odboy
 * @date 2024-10-01
 */
public class K8sStatefulSet {
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
         * statefulset注解
         */
        private Map<String, String> annotations;
        /**
         * 副本数量
         */
        @NotNull(message = "副本数量不能为空")
        @Min(value = 0, message = "副本数量不能小于0")
        private Integer replicas;
        /**
         * 镜像地址
         */
        @NotBlank(message = "镜像地址不能为空")
        private String image;
        /**
         * 容器服务端口号
         */
        @NotNull(message = "容器服务端口号不能为空")
        private Integer port;
        /**
         * 需求cpu数
         */
        @NotNull(message = "需求cpu数不能为空")
        @Min(value = 1, message = "需求cpu数不能小于1")
        private Integer requestCpuNum;
        /**
         * 需求memory数
         */
        @NotNull(message = "需求memory数不能为空")
        @Min(value = 1, message = "需求memory数不能小于1")
        private Integer requestMemNum;
        /**
         * cpu上限
         */
        @NotNull(message = "cpu上限不能为空")
        private Integer limitsCpuNum;
        /**
         * memory上限
         */
        @NotNull(message = "memory上限不能为空")
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
        @NotBlank(message = "集群编码不能为空")
        private String clusterCode;
        /**
         * 应用名称
         */
        @NotBlank(message = "应用名称不能为空")
        private String appName;
        /**
         * 新副本数量
         */
        @NotNull(message = "新副本数量不能为空")
        @Min(value = 0, message = "新副本数量不能小于0")
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
        @NotBlank(message = "集群编码不能为空")
        private String clusterCode;
        /**
         * 应用名称
         */
        @NotBlank(message = "应用名称不能为空")
        private String appName;
        /**
         * 新镜像地址
         */
        @NotBlank(message = "镜像地址不能为空")
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
        @NotBlank(message = "集群编码不能为空")
        private String clusterCode;
        /**
         * 应用名称
         */
        @NotBlank(message = "应用名称不能为空")
        private String appName;
        /**
         * 需求cpu数
         */
        @NotNull(message = "需求cpu数不能为空")
        @Min(value = 1, message = "需求cpu数不能小于1")
        private Integer requestCpuNum;
        /**
         * 需求memory数
         */
        @NotNull(message = "需求memory数不能为空")
        @Min(value = 1, message = "需求memory数不能小于1")
        private Integer requestMemNum;
        /**
         * cpu上限
         */
        @NotNull(message = "cpu上限不能为空")
        private Integer limitsCpuNum;
        /**
         * memory上限
         */
        @NotNull(message = "memory上限不能为空")
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
