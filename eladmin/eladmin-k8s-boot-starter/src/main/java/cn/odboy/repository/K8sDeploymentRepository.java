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

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Assert;
import com.alibaba.fastjson.JSON;
import io.kubernetes.client.custom.Quantity;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.AppsV1Api;
import io.kubernetes.client.openapi.models.*;
import io.kubernetes.client.util.Yaml;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import cn.odboy.constant.K8sActionReasonCodeEnum;
import cn.odboy.constant.K8sPodStatusEnum;
import cn.odboy.context.K8sClientAdmin;
import cn.odboy.infra.exception.BadRequestException;
import cn.odboy.model.request.K8sDeployment;
import cn.odboy.model.request.K8sPod;
import cn.odboy.model.response.K8sResource;
import cn.odboy.util.K8sDryRunUtil;
import cn.odboy.util.K8sResourceNameUtil;
import cn.odboy.util.ValidationUtil;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * K8s Deployment
 *
 * @author odboy
 * @date 2024-10-01
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class K8sDeploymentRepository {
    private final K8sClientAdmin k8SClientAdmin;
    private final K8sPodRepository k8sPodRepository;

    /**
     * 创建Deployment
     *
     * @param args /
     */
    public V1Deployment createDeployment(K8sDeployment.CreateArgs args) {
        ValidationUtil.validate(args);
        try {
            Map<String, String> labels = K8sResourceNameUtil.getLabelsMap(args.getAppName());
            String deploymentName = K8sResourceNameUtil.getDeploymentName(args.getAppName(), k8SClientAdmin.getEnvCode(args.getClusterCode()));
            // 构建deployment的yaml对象
            V1Deployment deployment = new V1DeploymentBuilder()
                    .withNewMetadata()
                    .withName(deploymentName)
                    .withNamespace(args.getAppName())
                    .withAnnotations(args.getAnnotations())
                    .endMetadata()
                    .withNewSpec()
                    .withReplicas(args.getReplicas())
                    .withSelector(new V1LabelSelector().matchLabels(labels))
                    .withNewTemplate()
                    .withNewMetadata()
                    .withLabels(labels)
                    .endMetadata()
                    .withNewSpec()
                    .withContainers(new V1Container()
                            .name(K8sResourceNameUtil.getPodName(args.getAppName(), k8SClientAdmin.getEnvCode(args.getClusterCode())))
                            .image(args.getImage())
                            .ports(CollUtil.newArrayList(new V1ContainerPort().containerPort(args.getPort())))
                    )
                    .endSpec()
                    .endTemplate()
                    .endSpec()
                    .build();
            ApiClient apiClient = k8SClientAdmin.getClient(args.getClusterCode());
            AppsV1Api appsV1Api = new AppsV1Api(apiClient);
            return appsV1Api.createNamespacedDeployment(args.getAppName(), deployment, "false", K8sDryRunUtil.transferState(args.getDryRun()), null);
        } catch (ApiException e) {
            String responseBody = e.getResponseBody();
            log.error("创建Deployment失败: {}", responseBody, e);
            K8sResource.ActionExceptionBody actionExceptionBody = JSON.parseObject(responseBody, K8sResource.ActionExceptionBody.class);
            if (actionExceptionBody != null) {
                throw new BadRequestException("创建Deployment失败, 原因：" + actionExceptionBody.getReason());
            }
            throw new BadRequestException("创建Deployment失败");
        } catch (Exception e) {
            log.error("创建Deployment失败:", e);
            throw new BadRequestException("创建Deployment失败");
        }
    }

    /**
     * 变更Deployment副本数量
     *
     * @param args /
     */
    public V1Deployment changeDeploymentReplicas(K8sDeployment.ChangeReplicasArgs args) {
        ValidationUtil.validate(args);
        try {
            ApiClient apiClient = k8SClientAdmin.getClient(args.getClusterCode());
            AppsV1Api appsV1Api = new AppsV1Api(apiClient);
            String deploymentName = K8sResourceNameUtil.getDeploymentName(args.getAppName(), k8SClientAdmin.getEnvCode(args.getClusterCode()));
            V1Deployment deployment = appsV1Api.readNamespacedDeployment(deploymentName, args.getAppName(), "false", null, null);
            if (deployment == null || deployment.getSpec() == null) {
                throw new BadRequestException(deploymentName + " 不存在");
            }
            deployment.getSpec().setReplicas(args.getNewReplicas());
            return appsV1Api.replaceNamespacedDeployment(deploymentName, args.getAppName(), deployment, "false", K8sDryRunUtil.transferState(args.getDryRun()), null);
        } catch (ApiException e) {
            String responseBody = e.getResponseBody();
            log.error("变更Deployment副本数量失败: {}", responseBody, e);
            K8sResource.ActionExceptionBody actionExceptionBody = JSON.parseObject(responseBody, K8sResource.ActionExceptionBody.class);
            if (actionExceptionBody != null) {
                throw new BadRequestException("变更Deployment副本数量失败, 原因：" + actionExceptionBody.getReason());
            }
            throw new BadRequestException("变更Deployment副本数量失败");
        } catch (Exception e) {
            log.error("变更Deployment副本数量失败:", e);
            throw new BadRequestException("变更Deployment副本数量失败");
        }
    }

    /**
     * 变更所有Deployment镜像地址
     *
     * @param args /
     */
    public V1Deployment changeDeploymentImage(K8sDeployment.ChangeImageArgs args) {
        ValidationUtil.validate(args);
        try {
            ApiClient apiClient = k8SClientAdmin.getClient(args.getClusterCode());
            AppsV1Api appsV1Api = new AppsV1Api(apiClient);
            String deploymentName = K8sResourceNameUtil.getDeploymentName(args.getAppName(), k8SClientAdmin.getEnvCode(args.getClusterCode()));
            V1Deployment deployment = appsV1Api.readNamespacedDeployment(deploymentName, args.getAppName(), "false", null, null);
            if (deployment == null || deployment.getSpec() == null) {
                throw new BadRequestException(deploymentName + " 不存在");
            }
            V1PodSpec podSpec = deployment.getSpec().getTemplate().getSpec();
            if (podSpec != null) {
                List<V1Container> containers = deployment.getSpec().getTemplate().getSpec().getContainers();
                if (containers.isEmpty()) {
                    throw new BadRequestException("Pod中不包含任何容器");
                }
                containers.get(0).setImage(args.getNewImage());
            }
            V1Deployment v1Deployment = appsV1Api.replaceNamespacedDeployment(deploymentName, args.getAppName(), deployment, "false", K8sDryRunUtil.transferState(args.getDryRun()), null);
            // 手动删除Pod, 触发重新调度, 最佳实践是分批删除
            /// 这里手动删除的原因是：改变image路径并没有触发deployment重建, 那只能出此下策
            /// 事实表明, 处于Pending状态的Pod, 就算添加了新的annotation, 或者label, 也不会生效
            /// 事实表明, 只有处于running中的Pod才会正常的重建
            List<K8sResource.Pod> podList = k8sPodRepository.listPods(args.getClusterCode(), args.getAppName(), deploymentName);
            for (K8sResource.Pod pod : podList) {
                if (K8sPodStatusEnum.Pending.getCode().equals(pod.getStatus())) {
                    K8sPod.RebuildArgs rebuildArgs = new K8sPod.RebuildArgs();
                    rebuildArgs.setClusterCode(args.getClusterCode());
                    rebuildArgs.setPodName(pod.getName());
                    rebuildArgs.setNamespace(pod.getNamespace());
                    k8sPodRepository.rebuildPod(rebuildArgs);
                }
            }
            return v1Deployment;
        } catch (ApiException e) {
            String responseBody = e.getResponseBody();
            log.error("变更Deployment镜像地址失败: {}", responseBody, e);
            K8sResource.ActionExceptionBody actionExceptionBody = JSON.parseObject(responseBody, K8sResource.ActionExceptionBody.class);
            if (actionExceptionBody != null) {
                throw new BadRequestException("变更Deployment镜像地址失败, 原因：" + actionExceptionBody.getReason());
            }
            throw new BadRequestException("变更Deployment镜像地址失败");
        } catch (Exception e) {
            log.error("变更Deployment镜像地址失败:", e);
            throw new BadRequestException("变更Deployment镜像地址失败");
        }
    }

    /**
     * 变更Deployment规格
     *
     * @param args /
     */
    public V1Deployment changeDeploymentSpecs(K8sDeployment.ChangePodSpecsArgs args) {
        ValidationUtil.validate(args);
        try {
            ApiClient apiClient = k8SClientAdmin.getClient(args.getClusterCode());
            AppsV1Api appsV1Api = new AppsV1Api(apiClient);
            String deploymentName = K8sResourceNameUtil.getDeploymentName(args.getAppName(), k8SClientAdmin.getEnvCode(args.getClusterCode()));
            V1Deployment deployment = appsV1Api.readNamespacedDeployment(deploymentName, args.getAppName(), "false", null, null);
            if (deployment == null || deployment.getSpec() == null) {
                throw new BadRequestException(deploymentName + " 不存在");
            }
            V1PodSpec podSpec = deployment.getSpec().getTemplate().getSpec();
            if (podSpec != null) {
                List<V1Container> containers = podSpec.getContainers();
                if (containers.isEmpty()) {
                    throw new BadRequestException("Pod中不包含任何容器");
                }
                for (V1Container container : containers) {
                    if (container.getName().contains(args.getAppName())) {
                        V1ResourceRequirements resources = container.getResources();
                        if (resources == null) {
                            resources = new V1ResourceRequirements();
                        }
                        resources
                                .putRequestsItem("cpu", new Quantity("10m"))
                                .putRequestsItem("memory", new Quantity("1G"))
                                .putLimitsItem("cpu", new Quantity(String.valueOf(args.getCpuNum())))
                                .putLimitsItem("memory", new Quantity(args.getMemoryNum() + "G"));
                        break;
                    }
                }
            }
            V1Deployment v1Deployment = appsV1Api.replaceNamespacedDeployment(deploymentName, args.getAppName(), deployment, "false", K8sDryRunUtil.transferState(args.getDryRun()), null);
            List<K8sResource.Pod> pods = k8sPodRepository.listPods(args.getClusterCode(), args.getAppName(), args.getAppName());
            for (K8sResource.Pod pod : pods) {
                if (K8sPodStatusEnum.Pending.getCode().equals(pod.getStatus())) {
                    K8sPod.RebuildArgs rebuildArgs = new K8sPod.RebuildArgs();
                    rebuildArgs.setClusterCode(args.getClusterCode());
                    rebuildArgs.setNamespace(pod.getNamespace());
                    rebuildArgs.setPodName(pod.getName());
                    k8sPodRepository.rebuildPod(rebuildArgs);
                }
            }
            return v1Deployment;
        } catch (ApiException e) {
            String responseBody = e.getResponseBody();
            log.error("变更Deployment规格失败: {}", responseBody, e);
            K8sResource.ActionExceptionBody actionExceptionBody = JSON.parseObject(responseBody, K8sResource.ActionExceptionBody.class);
            if (actionExceptionBody != null) {
                throw new BadRequestException("变更Deployment规格失败: {}, 原因：" + actionExceptionBody.getReason());
            }
            throw new BadRequestException("变更Deployment规格失败: {}");
        } catch (Exception e) {
            log.error("变更Deployment规格失败:", e);
            throw new BadRequestException("变更Deployment规格失败");
        }
    }

    /**
     * 删除Deployment
     *
     * @param args /
     */
    public V1Status deleteDeployment(K8sDeployment.DeleteArgs args) {
        ValidationUtil.validate(args);
        try {
            ApiClient apiClient = k8SClientAdmin.getClient(args.getClusterCode());
            AppsV1Api appsV1Api = new AppsV1Api(apiClient);
            String deploymentName = K8sResourceNameUtil.getDeploymentName(args.getAppName(), k8SClientAdmin.getEnvCode(args.getClusterCode()));
            return appsV1Api.deleteNamespacedDeployment(deploymentName, args.getAppName(), "false", K8sDryRunUtil.transferState(args.getDryRun()), null, null, null, null);
        } catch (ApiException e) {
            String responseBody = e.getResponseBody();
            log.error("删除Deployment失败: {}", responseBody, e);
            K8sResource.ActionExceptionBody actionExceptionBody = JSON.parseObject(responseBody, K8sResource.ActionExceptionBody.class);
            if (actionExceptionBody != null) {
                throw new BadRequestException("删除Deployment失败, 原因：" + actionExceptionBody.getReason());
            }
            throw new BadRequestException("删除Deployment失败");
        } catch (Exception e) {
            log.error("删除Deployment失败:", e);
            throw new BadRequestException("删除Deployment失败");
        }
    }

    /**
     * 通过appName查询deployment失败
     *
     * @param clusterCode 集群编码
     * @param appName     应用名称
     * @return /
     */
    public V1Deployment describeDeploymentByAppName(String clusterCode, String appName) {
        Assert.notEmpty(clusterCode, "集群编码不能为空");
        Assert.notEmpty(appName, "应用名称不能为空");
        try {
            ApiClient apiClient = k8SClientAdmin.getClient(clusterCode);
            AppsV1Api appsV1Api = new AppsV1Api(apiClient);
            String deploymentName = K8sResourceNameUtil.getDeploymentName(appName, clusterCode);
            return appsV1Api.readNamespacedDeployment(deploymentName, appName, "false", null, null);
        } catch (ApiException e) {
            String responseBody = e.getResponseBody();
            K8sResource.ActionExceptionBody actionExceptionBody = JSON.parseObject(responseBody, K8sResource.ActionExceptionBody.class);
            if (actionExceptionBody != null) {
                if (actionExceptionBody.getReason().contains(K8sActionReasonCodeEnum.NotFound.getCode())) {
                    return null;
                }
                log.error("通过deployment名称查询deployment失败: {}", responseBody, e);
                throw new BadRequestException("通过deployment名称查询deployment失败, 原因：" + actionExceptionBody.getReason());
            }
            throw new BadRequestException("通过deployment名称查询deployment失败");
        } catch (Exception e) {
            log.error("通过deployment名称查询deployment失败:", e);
            throw new BadRequestException("通过deployment名称查询deployment失败");
        }
    }

    /**
     * 通过appName查询deployment失败
     *
     * @param clusterCode 集群编码
     * @param name        deployment名称
     * @param namespace   命名空间
     * @return /
     */
    public V1Deployment describeDeploymentByName(String clusterCode, String name, String namespace) {
        Assert.notEmpty(clusterCode, "集群编码不能为空");
        Assert.notEmpty(name, "名称不能为空");
        Assert.notEmpty(namespace, "命名空间不能为空");
        try {
            ApiClient apiClient = k8SClientAdmin.getClient(clusterCode);
            AppsV1Api appsV1Api = new AppsV1Api(apiClient);
            return appsV1Api.readNamespacedDeployment(name, namespace, "false", null, null);
        } catch (ApiException e) {
            String responseBody = e.getResponseBody();
            K8sResource.ActionExceptionBody actionExceptionBody = JSON.parseObject(responseBody, K8sResource.ActionExceptionBody.class);
            if (actionExceptionBody != null) {
                if (actionExceptionBody.getReason().contains(K8sActionReasonCodeEnum.NotFound.getCode())) {
                    return null;
                }
                log.error("通过deployment名称查询deployment失败: {}", responseBody, e);
                throw new BadRequestException("通过deployment名称查询deployment失败, 原因：" + actionExceptionBody.getReason());
            }
            throw new BadRequestException("通过deployment名称查询deployment失败");
        } catch (Exception e) {
            log.error("通过deployment名称查询deployment失败:", e);
            throw new BadRequestException("通过deployment名称查询deployment失败");
        }
    }

    public V1Deployment loadDeploymentFromYaml(K8sDeployment.LoadFromYamlArgs args) {
        ValidationUtil.validate(args);
        try {
            V1Deployment deployment = Yaml.loadAs(args.getYamlContent(), V1Deployment.class);
            ApiClient apiClient = k8SClientAdmin.getClient(args.getClusterCode());
            AppsV1Api appsV1Api = new AppsV1Api(apiClient);
            String deploymentName = Objects.requireNonNull(deployment.getMetadata()).getName();
            String namespace = Objects.requireNonNull(deployment.getMetadata()).getNamespace();
            V1Deployment v1Deployment = describeDeploymentByName(args.getClusterCode(), deploymentName, namespace);
            if (v1Deployment == null) {
                appsV1Api.createNamespacedDeployment(namespace, deployment, "false", K8sDryRunUtil.transferState(args.getDryRun()), null);
            } else {
                appsV1Api.replaceNamespacedDeployment(deploymentName, namespace, deployment, "false", K8sDryRunUtil.transferState(args.getDryRun()), null);
            }
            return deployment;
        } catch (ApiException e) {
            String responseBody = e.getResponseBody();
            log.error("从yml加载Deployment失败: {}", responseBody, e);
            K8sResource.ActionExceptionBody actionExceptionBody = JSON.parseObject(responseBody, K8sResource.ActionExceptionBody.class);
            if (actionExceptionBody != null) {
                throw new BadRequestException("从yml加载Deployment失败, 原因：" + actionExceptionBody.getReason());
            }
            throw new BadRequestException("从yml加载Deployment失败");
        } catch (Exception e) {
            log.error("从yml加载Deployment失败:", e);
            throw new BadRequestException("从yml加载Deployment失败");
        }
    }
}
