/*
 *  Copyright 2022-2025 Tian Jun
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
package cn.odboy.repository;

import cn.hutool.core.lang.Assert;
import com.alibaba.fastjson.JSON;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1Namespace;
import io.kubernetes.client.openapi.models.V1NamespaceBuilder;
import io.kubernetes.client.openapi.models.V1NamespaceList;
import io.kubernetes.client.util.Yaml;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import cn.odboy.constant.K8sActionReasonCodeEnum;
import cn.odboy.context.K8sClientAdmin;
import cn.odboy.infra.exception.BadRequestException;
import cn.odboy.model.request.K8sNamespace;
import cn.odboy.model.response.K8sResource;
import cn.odboy.util.K8sDryRunUtil;
import cn.odboy.util.ValidationUtil;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Kubernetes Namespace Repository
 *
 * @author odboy
 * @date 2024-11-15
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class K8sNamespaceRepository {
    private final K8sClientAdmin k8SClientAdmin;

    /**
     * 获取Namespace列表
     *
     * @return /
     */
    public List<K8sResource.Namespace> listNamespaces(String clusterCode) {
        Assert.notEmpty(clusterCode, "集群编码不能为空");
        try {
            ApiClient apiClient = k8SClientAdmin.getClient(clusterCode);
            CoreV1Api coreV1Api = new CoreV1Api(apiClient);
            V1NamespaceList namespaceList = coreV1Api.listNamespace("false", true, null, null, null, null, null, null, null, false);
            return namespaceList.getItems().stream().map(m -> {
                K8sResource.Namespace namespace = new K8sResource.Namespace();
                namespace.setSpec(m.getSpec());
                namespace.setKind(m.getKind());
                namespace.setMetadata(m.getMetadata());
                namespace.setStatus(m.getStatus());
                return namespace;
            }).collect(Collectors.toList());
        } catch (ApiException e) {
            String responseBody = e.getResponseBody();
            log.error("获取Namespace列表失败: {}", responseBody, e);
            K8sResource.ActionExceptionBody actionExceptionBody = JSON.parseObject(responseBody, K8sResource.ActionExceptionBody.class);
            if (actionExceptionBody != null) {
                throw new BadRequestException("获取Namespace列表失败, 原因：" + actionExceptionBody.getReason());
            }
            throw new BadRequestException("获取Namespace列表失败");
        } catch (Exception e) {
            log.error("获取Namespace列表失败:", e);
            throw new BadRequestException("获取Namespace列表失败");
        }
    }

    /**
     * 根据appName获取Namespace
     *
     * @return /
     */
    public K8sResource.Namespace describeNamespaceByAppName(String clusterCode, String appName) {
        Assert.notEmpty(clusterCode, "集群编码不能为空");
        Assert.notEmpty(appName, "应用名称不能为空");
        try {
            ApiClient apiClient = k8SClientAdmin.getClient(clusterCode);
            CoreV1Api coreV1Api = new CoreV1Api(apiClient);
            V1Namespace v1Namespace = coreV1Api.readNamespace(appName, "false", null, null);
            if (v1Namespace == null) {
                return null;
            }
            K8sResource.Namespace namespace = new K8sResource.Namespace();
            namespace.setSpec(v1Namespace.getSpec());
            namespace.setKind(v1Namespace.getKind());
            namespace.setMetadata(v1Namespace.getMetadata());
            namespace.setStatus(v1Namespace.getStatus());
            return namespace;
        } catch (ApiException e) {
            String responseBody = e.getResponseBody();
            K8sResource.ActionExceptionBody actionExceptionBody = JSON.parseObject(responseBody, K8sResource.ActionExceptionBody.class);
            if (actionExceptionBody != null) {
                if (actionExceptionBody.getReason().contains(K8sActionReasonCodeEnum.NotFound.getCode())) {
                    return null;
                }
                log.error("根据appName获取Namespace失败: {}", responseBody, e);
                throw new BadRequestException("根据appName获取Namespace失败, 原因：" + actionExceptionBody.getReason());
            }
            throw new BadRequestException("根据appName获取Namespace失败");
        } catch (Exception e) {
            log.error("根据appName获取Namespace失败:", e);
            throw new BadRequestException("根据appName获取Namespace失败");
        }
    }

    /**
     * 根据name获取Namespace
     *
     * @return /
     */
    public K8sResource.Namespace describeNamespaceByName(String clusterCode, String name) {
        Assert.notEmpty(clusterCode, "集群编码不能为空");
        Assert.notEmpty(name, "名称不能为空");
        try {
            ApiClient apiClient = k8SClientAdmin.getClient(clusterCode);
            CoreV1Api coreV1Api = new CoreV1Api(apiClient);
            V1Namespace v1Namespace = coreV1Api.readNamespace(name, "false", null, null);
            if (v1Namespace == null) {
                return null;
            }
            K8sResource.Namespace namespace = new K8sResource.Namespace();
            namespace.setSpec(v1Namespace.getSpec());
            namespace.setKind(v1Namespace.getKind());
            namespace.setMetadata(v1Namespace.getMetadata());
            namespace.setStatus(v1Namespace.getStatus());
            return namespace;
        } catch (ApiException e) {
            String responseBody = e.getResponseBody();
            K8sResource.ActionExceptionBody actionExceptionBody = JSON.parseObject(responseBody, K8sResource.ActionExceptionBody.class);
            if (actionExceptionBody != null) {
                if (actionExceptionBody.getReason().contains(K8sActionReasonCodeEnum.NotFound.getCode())) {
                    return null;
                }
                log.error("根据name获取Namespace失败: {}", responseBody, e);
                throw new BadRequestException("根据name获取Namespace失败, 原因：" + actionExceptionBody.getReason());
            }
            throw new BadRequestException("根据name获取Namespace失败");
        } catch (Exception e) {
            log.error("根据name获取Namespace失败:", e);
            throw new BadRequestException("根据name获取Namespace失败");
        }
    }

    /**
     * 创建Namespace
     *
     * @param args /
     */
    public K8sResource.Namespace createNamespace(K8sNamespace.CreateArgs args) {
        ValidationUtil.validate(args);
        try {
            ApiClient apiClient = k8SClientAdmin.getClient(args.getClusterCode());
            CoreV1Api coreV1Api = new CoreV1Api(apiClient);
            V1Namespace newNamespace = new V1NamespaceBuilder()
                    .withApiVersion("v1")
                    .withKind("Namespace")
                    .withNewMetadata()
                    .withName(args.getAppName())
                    .endMetadata()
                    .build();
            V1Namespace v1Namespace = coreV1Api.createNamespace(newNamespace, "false", K8sDryRunUtil.transferState(args.getDryRun()), null);
            K8sResource.Namespace simpleNamespace = new K8sResource.Namespace();
            simpleNamespace.setSpec(v1Namespace.getSpec());
            simpleNamespace.setKind(v1Namespace.getKind());
            simpleNamespace.setMetadata(v1Namespace.getMetadata());
            simpleNamespace.setStatus(v1Namespace.getStatus());
            return simpleNamespace;
        } catch (ApiException e) {
            String responseBody = e.getResponseBody();
            log.error("创建namespace失败: {}", responseBody, e);
            K8sResource.ActionExceptionBody actionExceptionBody = JSON.parseObject(responseBody, K8sResource.ActionExceptionBody.class);
            if (actionExceptionBody != null) {
                throw new BadRequestException("创建namespace失败, 原因：" + actionExceptionBody.getReason());
            }
            throw new BadRequestException("创建namespace失败");
        } catch (Exception e) {
            log.error("创建namespace失败:", e);
            throw new BadRequestException("创建namespace失败");
        }
    }

    public K8sResource.Namespace loadNamespaceFromYaml(K8sNamespace.LoadFromYamlArgs args) {
        ValidationUtil.validate(args);
        try {
            V1Namespace v1Namespace = Yaml.loadAs(args.getYamlContent(), V1Namespace.class);
            ApiClient apiClient = k8SClientAdmin.getClient(args.getClusterCode());
            CoreV1Api coreV1Api = new CoreV1Api(apiClient);
            String namespaceName = Objects.requireNonNull(v1Namespace.getMetadata()).getName();
            K8sResource.Namespace localNamespace = describeNamespaceByName(args.getClusterCode(), namespaceName);
            if (localNamespace == null) {
                coreV1Api.createNamespace(v1Namespace, "false", K8sDryRunUtil.transferState(args.getDryRun()), null);
            } else {
                coreV1Api.replaceNamespace(namespaceName, v1Namespace, "false", K8sDryRunUtil.transferState(args.getDryRun()), null);
            }
            K8sResource.Namespace simpleNamespace = new K8sResource.Namespace();
            simpleNamespace.setSpec(v1Namespace.getSpec());
            simpleNamespace.setKind(v1Namespace.getKind());
            simpleNamespace.setMetadata(v1Namespace.getMetadata());
            simpleNamespace.setStatus(v1Namespace.getStatus());
            return simpleNamespace;
        } catch (ApiException e) {
            String responseBody = e.getResponseBody();
            log.error("从yml加载Namespace失败: {}", responseBody, e);
            K8sResource.ActionExceptionBody actionExceptionBody = JSON.parseObject(responseBody, K8sResource.ActionExceptionBody.class);
            if (actionExceptionBody != null) {
                throw new BadRequestException("从yml加载Namespace失败, 原因：" + actionExceptionBody.getReason());
            }
            throw new BadRequestException("从yml加载Namespace失败");
        } catch (Exception e) {
            log.error("从yml加载Namespace失败:", e);
            throw new BadRequestException("从yml加载Namespace失败");
        }
    }
}
