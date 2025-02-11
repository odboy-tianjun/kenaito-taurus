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
package cn.odboy.modules.devops.domain;

import cn.odboy.base.MyLogicEntity;
import cn.odboy.model.MyObject;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * <p>
 * k8s集群配置
 * </p>
 *
 * @author codegen
 * @since 2025-01-12
 */
@Getter
@Setter
@TableName("devops_k8s_cluster_config")
@ApiModel(value = "K8sClusterConfig对象", description = "k8s集群配置")
public class K8sClusterConfig extends MyLogicEntity {
    @ApiModelProperty("自增ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 例如: daily
     */
    @ApiModelProperty("环境编码")
    @TableField("env_code")
    private String envCode;
    /**
     * 例如: v20250113、aliyunxx、tencentxx、huaweixx
     */
    @ApiModelProperty("集群编码")
    @TableField("cluster_code")
    private String clusterCode;
    /**
     * 例如: 阿里云日常环境集群、腾讯日常环境集群、华为日常环境集群
     */
    @ApiModelProperty("集群名称")
    @TableField("cluster_name")
    private String clusterName;
    /**
     * cat ~/.kube/config
     */
    @ApiModelProperty("配置内容")
    @TableField("config_content")
    private byte[] configContent;
    @ApiModelProperty("健康状态(1、健康 0、不健康)")
    @TableField("status")
    private Boolean status;

    @Data
    @EqualsAndHashCode(callSuper = false)
    public static class CreateArgs extends MyObject {
        @NotBlank(message = "环境不能为空")
        private String envCode;
        @NotBlank(message = "集群编码不能为空")
        private String clusterCode;
        @NotBlank(message = "集群名称不能为空")
        private String clusterName;
        @NotBlank(message = "配置内容不能为空")
        private String configContent;
    }
}
