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

import cn.odboy.mybatisplus.model.MyObject;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
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
    @Builder
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
         * deployment注解
         */
        private Map<String, String> annotations;
        /**
         * 镜像地址
         */
        @NotBlank(message = "镜像地址不能为空")
        private String image;
        /**
         * 副本数量
         */
        @NotNull(message = "副本数量不能为空")
        @Min(value = 0, message = "副本数量不能小于0")
        private Integer replicas;
        /**
         * 容器服务端口号
         */
        @NotNull(message = "容器服务端口号不能为空")
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

    /**
     * 修改Deployment镜像地址
     */
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
        @NotBlank(message = "新镜像地址不能为空")
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
        @NotBlank(message = "集群编码不能为空")
        private String clusterCode;
        /**
         * 应用名称
         */
        @NotBlank(message = "应用名称不能为空")
        private String appName;
        /**
         * CPU数
         */
        @NotNull
        @Min(value = 1, message = "CPU数不能小于0")
        private Integer cpuNum;
        /**
         * 内存数
         */
        @NotNull
        @Min(value = 1, message = "内存数不能小于0")
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

    /**
     * 从yaml文件加载Deployment
     */
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
