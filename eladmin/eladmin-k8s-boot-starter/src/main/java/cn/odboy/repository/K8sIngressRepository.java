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
import cn.odboy.model.request.K8sIngress;
import com.alibaba.fastjson.JSON;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.NetworkingV1Api;
import io.kubernetes.client.openapi.models.*;
import io.kubernetes.client.util.Yaml;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import cn.odboy.constant.K8sActionReasonCodeEnum;
import cn.odboy.context.K8sClientAdmin;
import cn.odboy.infra.exception.BadRequestException;
import cn.odboy.model.response.K8sResource;
import cn.odboy.util.K8sDryRunUtil;
import cn.odboy.util.K8sResourceNameUtil;
import cn.odboy.util.ValidationUtil;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * Kubernetes Ingress Repository
 *
 * @author odboy
 * @date 2024-09-26
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class K8sIngressRepository {
    private final K8sClientAdmin k8SClientAdmin;

    /**
     * 创建k8s V1Ingress
     *
     * @param args /
     */
    public V1Ingress createIngress(K8sIngress.CreateArgs args) {
        ValidationUtil.validate(args);
        try {
            // 构建ingress的yaml对象
            V1Ingress ingress = new V1IngressBuilder()
                    .withNewMetadata()
                    .withName(K8sResourceNameUtil.getIngressName(args.getAppName(), k8SClientAdmin.getEnvCode(args.getClusterCode())))
                    .withNewNamespace(args.getAppName())
                    .withAnnotations(args.getAnnotations())
                    .endMetadata()
                    .withNewSpec()
                    .addNewRule()
                    .withHost(args.getHostname())
                    .withHttp(new V1HTTPIngressRuleValueBuilder().addToPaths(new V1HTTPIngressPathBuilder()
                            .withPath(args.getPath())
                            .withPathType("ImplementationSpecific")
                            .withBackend(new V1IngressBackendBuilder()
                                    .withService(new V1IngressServiceBackendBuilder()
                                            .withName(args.getServiceName())
                                            .withPort(new V1ServiceBackendPortBuilder()
                                                    .withNumber(args.getServicePort()).build()).build()).build()).build()).build())
                    .endRule()
                    .endSpec()
                    .build();
            ApiClient apiClient = k8SClientAdmin.getClient(args.getClusterCode());
            NetworkingV1Api networkingV1Api = new NetworkingV1Api(apiClient);
            return networkingV1Api.createNamespacedIngress(args.getAppName(), ingress, "false", K8sDryRunUtil.transferState(args.getDryRun()), null);
        } catch (ApiException e) {
            String responseBody = e.getResponseBody();
            log.error("创建Ingress失败: {}", responseBody, e);
            K8sResource.ActionExceptionBody actionExceptionBody = JSON.parseObject(responseBody, K8sResource.ActionExceptionBody.class);
            if (actionExceptionBody != null) {
                throw new BadRequestException("创建Ingress失败, 原因：" + actionExceptionBody.getReason());
            }
            throw new BadRequestException("创建Ingress失败");
        } catch (Exception e) {
            log.error("创建Ingress失败:", e);
            throw new BadRequestException("创建Ingress失败");
        }
    }

    /**
     * 删除Ingress
     *
     * @param args /
     */
    public void deleteIngress(K8sIngress.DeleteArgs args) {
        ValidationUtil.validate(args);
        try {
            ApiClient apiClient = k8SClientAdmin.getClient(args.getClusterCode());
            NetworkingV1Api networkingV1Api = new NetworkingV1Api(apiClient);
            networkingV1Api.deleteNamespacedIngress(K8sResourceNameUtil.getIngressName(args.getAppName(), k8SClientAdmin.getEnvCode(args.getClusterCode())), args.getAppName(), "false", K8sDryRunUtil.transferState(args.getDryRun()), null, null, null, null);
        } catch (ApiException e) {
            String responseBody = e.getResponseBody();
            log.error("删除Ingress失败: {}", responseBody, e);
            K8sResource.ActionExceptionBody actionExceptionBody = JSON.parseObject(responseBody, K8sResource.ActionExceptionBody.class);
            if (actionExceptionBody != null) {
                throw new BadRequestException("删除Ingress失败, 原因：" + actionExceptionBody.getReason());
            }
            throw new BadRequestException("删除Ingress失败");
        } catch (Exception e) {
            log.error("删除Ingress失败:", e);
            throw new BadRequestException("删除Ingress失败");
        }
    }

    /**
     * 根据appName获取Ingress
     *
     * @return /
     */
    public V1Ingress describeIngressByAppName(String clusterCode, String appName) {
        Assert.notEmpty(clusterCode, "集群编码不能为空");
        Assert.notEmpty(appName, "应用名称不能为空");
        try {
            ApiClient apiClient = k8SClientAdmin.getClient(clusterCode);
            NetworkingV1Api networkingV1Api = new NetworkingV1Api(apiClient);
            return networkingV1Api.readNamespacedIngress(K8sResourceNameUtil.getIngressName(appName, k8SClientAdmin.getEnvCode(clusterCode)), appName, "false", null, null);
        } catch (ApiException e) {
            String responseBody = e.getResponseBody();
            K8sResource.ActionExceptionBody actionExceptionBody = JSON.parseObject(responseBody, K8sResource.ActionExceptionBody.class);
            if (actionExceptionBody != null) {
                if (actionExceptionBody.getReason().contains(K8sActionReasonCodeEnum.NotFound.getCode())) {
                    return null;
                }
                log.error("根据appName获取Ingress失败: {}", responseBody, e);
                throw new BadRequestException("根据appName获取Ingress失败, 原因：" + actionExceptionBody.getReason());
            }
            throw new BadRequestException("根据appName获取Ingress失败");
        } catch (Exception e) {
            log.error("根据appName获取Ingress失败:", e);
            throw new BadRequestException("根据appName获取Ingress失败");
        }
    }

    /**
     * 根据ingress名称获取Ingress
     *
     * @param clusterCode 集群编码
     * @param name        ingress名称
     * @param namespace   命名空间
     * @return /
     */
    public V1Ingress describeIngressByName(String clusterCode, String name, String namespace) {
        Assert.notEmpty(clusterCode, "集群编码不能为空");
        Assert.notEmpty(name, "名称不能为空");
        Assert.notEmpty(namespace, "命名空间不能为空");
        try {
            ApiClient apiClient = k8SClientAdmin.getClient(clusterCode);
            NetworkingV1Api networkingV1Api = new NetworkingV1Api(apiClient);
            return networkingV1Api.readNamespacedIngress(name, namespace, "false", null, null);
        } catch (ApiException e) {
            String responseBody = e.getResponseBody();
            K8sResource.ActionExceptionBody actionExceptionBody = JSON.parseObject(responseBody, K8sResource.ActionExceptionBody.class);
            if (actionExceptionBody != null) {
                if (actionExceptionBody.getReason().contains(K8sActionReasonCodeEnum.NotFound.getCode())) {
                    return null;
                }
                log.error("根据ingress名称获取Ingress失败: {}", responseBody, e);
                throw new BadRequestException("根据ingress名称获取Ingress失败, 原因：" + actionExceptionBody.getReason());
            }
            throw new BadRequestException("根据ingress名称获取Ingress失败");
        } catch (Exception e) {
            log.error("根据ingress名称获取Ingress失败:", e);
            throw new BadRequestException("根据ingress名称获取Ingress失败");
        }
    }

    /**
     * 从yaml文件内容加载Ingress
     *
     * @param args /
     */
    public V1Ingress loadIngressFromYaml(K8sIngress.LoadFromYamlArgs args) {
        ValidationUtil.validate(args);
        try {
            V1Ingress ingress = Yaml.loadAs(args.getYamlContent(), V1Ingress.class);
            ApiClient apiClient = k8SClientAdmin.getClient(args.getClusterCode());
            NetworkingV1Api networkingV1Api = new NetworkingV1Api(apiClient);
            String ingressName = Objects.requireNonNull(ingress.getMetadata()).getName();
            String namespace = Objects.requireNonNull(ingress.getMetadata()).getNamespace();
            V1Ingress v1Ingress = describeIngressByName(args.getClusterCode(), ingressName, namespace);
            if (v1Ingress == null) {
                networkingV1Api.createNamespacedIngress(namespace, ingress, "false", K8sDryRunUtil.transferState(args.getDryRun()), null);
            } else {
                networkingV1Api.replaceNamespacedIngress(ingressName, namespace, ingress, "false", K8sDryRunUtil.transferState(args.getDryRun()), null);
            }
            return ingress;
        } catch (ApiException e) {
            String responseBody = e.getResponseBody();
            log.error("从yml加载Ingress失败: {}", responseBody, e);
            K8sResource.ActionExceptionBody actionExceptionBody = JSON.parseObject(responseBody, K8sResource.ActionExceptionBody.class);
            if (actionExceptionBody != null) {
                throw new BadRequestException("从yml加载Ingress失败, 原因：" + actionExceptionBody.getReason());
            }
            throw new BadRequestException("从yml加载Ingress失败");
        } catch (Exception e) {
            log.error("从yml加载Ingress失败:", e);
            throw new BadRequestException("从yml加载Ingress失败");
        }
    }
}
