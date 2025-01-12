package me.zhengjie.repository;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import io.kubernetes.client.custom.Quantity;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.AppsV1Api;
import io.kubernetes.client.openapi.models.*;
import io.kubernetes.client.util.Yaml;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.zhengjie.constant.EnvEnum;
import me.zhengjie.constant.PodStatusEnum;
import me.zhengjie.context.K8sClientAdmin;
import me.zhengjie.infra.exception.BadRequestException;
import me.zhengjie.model.K8sResource;
import me.zhengjie.util.K8sResourceTool;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class K8sDeploymentRepository {
    private final K8sClientAdmin k8SClientAdmin;
    private final K8sPodRepository k8sPodRepository;

    /**
     * 创建Deployment
     *
     * @param envEnum     环境编码
     * @param namespace   命名空间
     * @param appName     应用名称
     * @param annotations deployment注解
     * @param image       镜像地址
     * @param replicas    副本数量
     * @param port        容器服务端口号
     * @return /
     */
    public V1Deployment createDeployment(@NotNull EnvEnum envEnum, @NotNull String namespace, @NotNull String appName, Map<String, String> annotations,
                                         @NotNull String image, @NotNull Integer replicas, @NotNull Integer port) {
        try {
            if (replicas == null || replicas < 0) {
                replicas = 1;
            }
            Map<String, String> labels = K8sResourceTool.getLabelsMap(appName);
            String deploymentName = K8sResourceTool.getDeploymentName(appName);
            // 构建deployment的yaml对象
            V1Deployment deployment = new V1DeploymentBuilder()
                    .withNewMetadata()
                    .withName(deploymentName)
                    .withNamespace(namespace)
                    .withAnnotations(annotations)
                    .endMetadata()
                    .withNewSpec()
                    .withReplicas(replicas)
                    .withSelector(new V1LabelSelector().matchLabels(labels))
                    .withNewTemplate()
                    .withNewMetadata()
                    .withLabels(labels)
                    .endMetadata()
                    .withNewSpec()
                    .withContainers(new V1Container()
                            .name(K8sResourceTool.getPodName(appName))
                            .image(image)
                            .ports(CollUtil.newArrayList(new V1ContainerPort().containerPort(port)))
                    )
                    .endSpec()
                    .endTemplate()
                    .endSpec()
                    .build();
            ApiClient apiClient = k8SClientAdmin.getEnv(envEnum);
            AppsV1Api appsV1Api = new AppsV1Api(apiClient);
            return appsV1Api.createNamespacedDeployment(namespace, deployment, null, null, null);
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
     * @param namespace   命名空间
     * @param appName     应用名称
     * @param newReplicas 新副本数量
     * @return /
     */
    public V1Deployment changeReplicas(@NotNull EnvEnum envEnum, @NotNull String namespace, @NotNull String appName, @NotNull Integer newReplicas) {
        try {
            if (newReplicas == null || newReplicas < 0) {
                newReplicas = 0;
            }
            ApiClient apiClient = k8SClientAdmin.getEnv(envEnum);
            AppsV1Api appsV1Api = new AppsV1Api(apiClient);
            String deploymentName = K8sResourceTool.getDeploymentName(appName);
            V1Deployment deployment = appsV1Api.readNamespacedDeployment(deploymentName, namespace, null, null, null);
            if (deployment == null || deployment.getSpec() == null) {
                throw new BadRequestException(deploymentName + " 不存在");
            }
            deployment.getSpec().setReplicas(newReplicas);
            return appsV1Api.replaceNamespacedDeployment(deploymentName, namespace, deployment, null, null, null);
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
     * @param namespace 命名空间
     * @param appName   应用名称
     * @param newImage  新镜像地址
     * @return /
     */
    public V1Deployment changeImage(@NotNull EnvEnum envEnum, @NotNull String namespace, @NotNull String appName, @NotNull String newImage) {
        try {
            if (StrUtil.isBlank(newImage)) {
                throw new BadRequestException("镜像地址必填");
            }
            ApiClient apiClient = k8SClientAdmin.getEnv(envEnum);
            AppsV1Api appsV1Api = new AppsV1Api(apiClient);
            String deploymentName = K8sResourceTool.getDeploymentName(appName);
            V1Deployment deployment = appsV1Api.readNamespacedDeployment(deploymentName, namespace, null, null, null);
            if (deployment == null || deployment.getSpec() == null) {
                throw new BadRequestException(deploymentName + " 不存在");
            }
            V1PodSpec podSpec = deployment.getSpec().getTemplate().getSpec();
            if (podSpec != null) {
                List<V1Container> containers = deployment.getSpec().getTemplate().getSpec().getContainers();
                if (containers.isEmpty()) {
                    throw new BadRequestException("Pod中不包含任何容器");
                }
                containers.get(0).setImage(newImage);
            }
            V1Deployment v1Deployment = appsV1Api.replaceNamespacedDeployment(deploymentName, namespace, deployment, null, null, null);
            // 手动删除Pod, 触发重新调度, 最佳实践是分批删除
            /// 这里手动删除的原因是：改变image路径并没有触发statefulset重建, 那只能出此下策
            /// 事实表明, 处于Pending状态的Pod, 就算添加了新的annotation, 或者label, 也不会直接生效, 从而触发pod重建
            List<K8sResource.Pod> pods = k8sPodRepository.queryByAppName(envEnum, namespace, appName);
            for (K8sResource.Pod pod : pods) {
                if (PodStatusEnum.Pending.getCode().equals(pod.getStatus())) {
                    k8sPodRepository.rebuildPod(envEnum, namespace, pod.getName());
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
     * 变更statefulset规格
     *
     * @param namespace 命名空间
     * @param appName   应用名称
     * @param cpuNum    Cpu数值
     * @param memoryNum Memory数值
     * @return /
     */
    public V1Deployment changePodSpecs(@NotNull EnvEnum envEnum, @NotNull String namespace, @NotNull String appName, @NotNull Integer cpuNum,
                                       @NotNull Integer memoryNum) {
        if (cpuNum == null || cpuNum < 0) {
            cpuNum = 1;
        }
        if (memoryNum == null || memoryNum < 0) {
            memoryNum = 1;
        }
        try {
            ApiClient apiClient = k8SClientAdmin.getEnv(envEnum);
            AppsV1Api appsV1Api = new AppsV1Api(apiClient);
            String deploymentName = K8sResourceTool.getDeploymentName(appName);
            V1Deployment deployment = appsV1Api.readNamespacedDeployment(deploymentName, namespace, null, null, null);
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
                    if (container.getName().contains(appName)) {
                        V1ResourceRequirements resources = container.getResources();
                        if (resources == null) {
                            resources = new V1ResourceRequirements();
                        }
                        resources
                                .putRequestsItem("cpu", new Quantity("10m"))
                                .putRequestsItem("memory", new Quantity("1G"))
                                .putLimitsItem("cpu", new Quantity(String.valueOf(cpuNum)))
                                .putLimitsItem("memory", new Quantity(memoryNum + "G"));
                        break;
                    }
                }
            }
            V1Deployment v1Deployment = appsV1Api.replaceNamespacedDeployment(deploymentName, namespace, deployment, null, null, null);
            List<K8sResource.Pod> pods = k8sPodRepository.queryByAppName(envEnum, namespace, appName);
            for (K8sResource.Pod pod : pods) {
                if (PodStatusEnum.Pending.getCode().equals(pod.getStatus())) {
                    k8sPodRepository.rebuildPod(envEnum, namespace, pod.getName());
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
     * @param namespace      命名空间
     * @param deploymentName deployment名称
     * @return /
     */
    public V1Status remove(@NotNull EnvEnum envEnum, @NotNull String namespace, @NotNull String deploymentName) {
        try {
            ApiClient apiClient = k8SClientAdmin.getEnv(envEnum);
            AppsV1Api appsV1Api = new AppsV1Api(apiClient);
            return appsV1Api.deleteNamespacedDeployment(deploymentName, namespace, null, null, null, null, null, null);
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

    public V1Deployment getDeploymentByName(@NotNull EnvEnum envEnum, @NotNull String namespace, @NotNull String deploymentName) {
        ApiClient apiClient = k8SClientAdmin.getEnv(envEnum);
        AppsV1Api appsV1Api = new AppsV1Api(apiClient);
        try {
            return appsV1Api.readNamespacedDeployment(deploymentName, namespace, "false", null, null);
        } catch (ApiException e) {
            String responseBody = e.getResponseBody();
            log.error("通过deployment名称查询deployment失败: {}", responseBody, e);
            K8sResource.ActionExceptionBody actionExceptionBody = JSON.parseObject(responseBody, K8sResource.ActionExceptionBody.class);
            if (actionExceptionBody != null) {
                throw new BadRequestException("通过deployment名称查询deployment失败, 原因：" + actionExceptionBody.getReason());
            }
            throw new BadRequestException("通过deployment名称查询deployment失败");
        } catch (Exception e) {
            log.error("通过deployment名称查询deployment失败:", e);
            throw new BadRequestException("通过deployment名称查询deployment失败");
        }
    }

    public V1Deployment loadFromYml(@NotNull EnvEnum envEnum, @NotNull String namespace, @NotBlank String ymlContent) {
        try {
            V1Deployment deployment = Yaml.loadAs(ymlContent, V1Deployment.class);
            ApiClient apiClient = k8SClientAdmin.getEnv(envEnum);
            AppsV1Api appsV1Api = new AppsV1Api(apiClient);
            String deploymentName = deployment.getMetadata().getName();
            // 执行验收测试
            appsV1Api.replaceNamespacedDeployment(deploymentName, namespace, deployment, "false", "All", null);
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
