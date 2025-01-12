package me.zhengjie.repository;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import io.kubernetes.client.custom.IntOrString;
import io.kubernetes.client.custom.Quantity;
import io.kubernetes.client.custom.V1Patch;
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

/**
 * Kubernetes StatefulSet Repository
 *
 * @author odboy
 * @date 2024-09-26
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class K8sStatefulSetRepository {
    private final K8sClientAdmin k8SClientAdmin;
    private final K8sPodRepository k8sPodRepository;

    /**
     * 创建statefulset控制器
     *
     * @param envEnum     环境编码
     * @param namespace   命名空间
     * @param appName     应用名称
     * @param annotations statefulset注解
     * @param replicas    副本数量
     * @param image       镜像地址
     * @param port        容器服务端口号
     * @return /
     */
    public V1StatefulSet create(@NotNull EnvEnum envEnum, @NotNull String namespace, @NotNull String appName, Map<String, String> annotations,
                                @NotNull Integer replicas, @NotNull String image, @NotNull Integer port) {
        if (replicas == null || replicas < 0) {
            replicas = 1;
        }

        String statefulSetName = K8sResourceTool.getStatefulSetName(appName);
        String podName = K8sResourceTool.getPodName(appName);
        Map<String, String> labels = K8sResourceTool.getLabelsMap(appName);

        V1Container v1Container = new V1Container();
        v1Container.setName(podName);
        v1Container.setImage(image);
        v1Container.setPorts(CollUtil.newArrayList(new V1ContainerPort().containerPort(port)));
        v1Container.setLivenessProbe(new V1Probe().tcpSocket(new V1TCPSocketAction().port(new IntOrString(port))));
        v1Container.setReadinessProbe(new V1Probe().tcpSocket(new V1TCPSocketAction().port(new IntOrString(port))));

        // 构建statefulset的yaml对象
        V1StatefulSet statefulSet = new V1StatefulSetBuilder()
                .withNewMetadata()
                .withName(statefulSetName)
                .withNamespace(namespace)
                .withAnnotations(annotations)
                .endMetadata()
                .withNewSpec()
                .withServiceName(statefulSetName)
                .withReplicas(replicas)
                .withNewSelector()
                .withMatchLabels(labels)
                .endSelector()
                .withNewTemplate()
                .withNewMetadata()
                .withLabels(labels)
                .endMetadata()
                .withNewSpec()
                .withContainers(v1Container)
                .endSpec()
                .endTemplate()
                .endSpec()
                .build();
        ApiClient apiClient = k8SClientAdmin.getEnv(envEnum);
        AppsV1Api api = new AppsV1Api(apiClient);
        try {
            return api.createNamespacedStatefulSet(namespace, statefulSet, null, null, null);
        } catch (ApiException e) {
            String responseBody = e.getResponseBody();
            log.error("创建StatefulSet失败: {}", responseBody, e);
            K8sResource.ActionExceptionBody actionExceptionBody = JSON.parseObject(responseBody, K8sResource.ActionExceptionBody.class);
            if (actionExceptionBody != null) {
                throw new BadRequestException("创建StatefulSet失败, 原因：" + actionExceptionBody.getReason());
            }
            throw new BadRequestException("创建StatefulSet失败");
        } catch (Exception e) {
            log.error("创建StatefulSet失败:", e);
            throw new BadRequestException("创建StatefulSet失败");
        }
    }

    /**
     * 创建statefulset控制器
     *
     * @param namespace   命名空间
     * @param appName     应用名称
     * @param annotations statefulset注解
     * @param replicas    副本数量
     * @param image       镜像地址
     * @param port        容器服务端口号
     * @param cpuNum      Cpu数值
     * @param memoryNum   Memory数值
     * @return /
     */
    public V1StatefulSet create(@NotNull EnvEnum envEnum, @NotNull String namespace, @NotNull String appName, Map<String, String> annotations,
                                @NotNull Integer replicas, @NotNull String image, @NotNull Integer port, @NotNull Integer cpuNum,
                                @NotNull Integer memoryNum) {
        if (replicas == null || replicas < 0) {
            replicas = 1;
        }
        if (cpuNum == null || cpuNum < 0) {
            cpuNum = 1;
        }
        if (memoryNum == null || memoryNum < 0) {
            memoryNum = 1;
        }

        String statefulSetName = K8sResourceTool.getStatefulSetName(appName);
        String podName = K8sResourceTool.getPodName(appName);
        Map<String, String> labels = K8sResourceTool.getLabelsMap(appName);

        V1Container v1Container = new V1Container();
        v1Container.setName(podName);
        v1Container.setImage(image);
        v1Container.setPorts(CollUtil.newArrayList(new V1ContainerPort().containerPort(port)));
        v1Container.setLivenessProbe(new V1Probe().tcpSocket(new V1TCPSocketAction().port(new IntOrString(port))));
        v1Container.setReadinessProbe(new V1Probe().tcpSocket(new V1TCPSocketAction().port(new IntOrString(port))));
        v1Container.setResources(new V1ResourceRequirements()
                .putRequestsItem("cpu", new Quantity("10m"))
                .putRequestsItem("memory", new Quantity("1G"))
                .putLimitsItem("cpu", new Quantity(String.valueOf(cpuNum)))
                .putLimitsItem("memory", new Quantity(memoryNum + "G"))
        );

        // 构建statefulset的yaml对象
        V1StatefulSet statefulSet = new V1StatefulSetBuilder()
                .withNewMetadata()
                .withName(statefulSetName)
                .withNamespace(namespace)
                .withAnnotations(annotations)
                .endMetadata()
                .withNewSpec()
                .withServiceName(statefulSetName)
                .withReplicas(replicas)
                .withNewSelector()
                .withMatchLabels(labels)
                .endSelector()
                .withNewTemplate()
                .withNewMetadata()
                .withLabels(labels)
                .endMetadata()
                .withNewSpec()
                .withContainers(v1Container)
                .endSpec()
                .endTemplate()
                .endSpec()
                .build();
        ApiClient apiClient = k8SClientAdmin.getEnv(envEnum);
        AppsV1Api api = new AppsV1Api(apiClient);
        try {
            return api.createNamespacedStatefulSet(namespace, statefulSet, null, null, null);
        } catch (ApiException e) {
            String responseBody = e.getResponseBody();
            log.error("创建StatefulSet失败: {}", responseBody, e);
            K8sResource.ActionExceptionBody actionExceptionBody = JSON.parseObject(responseBody, K8sResource.ActionExceptionBody.class);
            if (actionExceptionBody != null) {
                throw new BadRequestException("创建StatefulSet失败, 原因：" + actionExceptionBody.getReason());
            }
            throw new BadRequestException("创建StatefulSet失败");
        } catch (Exception e) {
            log.error("创建StatefulSet失败:", e);
            throw new BadRequestException("创建StatefulSet失败");
        }
    }

    /**
     * 变更StatefulSet副本数量
     *
     * @param namespace   命名空间
     * @param appName     应用名称
     * @param newReplicas 新副本数量
     * @return /
     */
    public V1StatefulSet changeReplicas(@NotNull EnvEnum envEnum, @NotNull String namespace, @NotNull String appName, @NotNull Integer newReplicas) {
        try {
            if (newReplicas == null || newReplicas < 0) {
                newReplicas = 0;
            }
            ApiClient apiClient = k8SClientAdmin.getEnv(envEnum);
            AppsV1Api appsV1Api = new AppsV1Api(apiClient);
            String statefulSetName = K8sResourceTool.getStatefulSetName(appName);
            V1StatefulSet statefulSet = appsV1Api.readNamespacedStatefulSet(statefulSetName, namespace, null, null, null);
            if (statefulSet == null || statefulSet.getSpec() == null) {
                throw new BadRequestException(statefulSetName + " 不存在");
            }
            statefulSet.getSpec().setReplicas(newReplicas);
            return appsV1Api.replaceNamespacedStatefulSet(statefulSetName, namespace, statefulSet, null, null, null);
        } catch (ApiException e) {
            String responseBody = e.getResponseBody();
            log.error("变更StatefulSet副本数量失败: {}", responseBody, e);
            K8sResource.ActionExceptionBody actionExceptionBody = JSON.parseObject(responseBody, K8sResource.ActionExceptionBody.class);
            if (actionExceptionBody != null) {
                throw new BadRequestException("变更StatefulSet副本数量失败, 原因：" + actionExceptionBody.getReason());
            }
            throw new BadRequestException("变更StatefulSet副本数量失败");
        } catch (Exception e) {
            log.error("变更StatefulSet副本数量失败:", e);
            throw new BadRequestException("变更StatefulSet副本数量失败");
        }
    }

    /**
     * 变更statefulset镜像地址
     *
     * @param namespace 命名空间
     * @param appName   应用名称
     * @param newImage  新镜像地址
     * @return /
     */
    public V1StatefulSet changeImage(@NotNull EnvEnum envEnum, @NotNull String namespace, @NotNull String appName, @NotNull String newImage) {
        if (StrUtil.isBlank(newImage)) {
            throw new BadRequestException("镜像地址必填");
        }
        try {
            ApiClient apiClient = k8SClientAdmin.getEnv(envEnum);
            AppsV1Api appsV1Api = new AppsV1Api(apiClient);
            String statefulSetName = K8sResourceTool.getStatefulSetName(appName);
            V1StatefulSet statefulSet = appsV1Api.readNamespacedStatefulSet(statefulSetName, namespace, null, null, null);
            if (statefulSet == null || statefulSet.getSpec() == null) {
                throw new BadRequestException(statefulSetName + " 不存在");
            }
            V1PodSpec podSpec = statefulSet.getSpec().getTemplate().getSpec();
            if (podSpec != null) {
                List<V1Container> containers = podSpec.getContainers();
                if (containers.isEmpty()) {
                    throw new BadRequestException("Pod中不包含任何容器");
                }
                for (V1Container container : containers) {
                    if (container.getName().contains(appName)) {
                        container.setImage(newImage);
                        break;
                    }
                }
            }
            V1StatefulSet v1StatefulSet = appsV1Api.replaceNamespacedStatefulSet(statefulSetName, namespace, statefulSet, null, null, null);
            // 手动删除Pod, 触发重新调度, 最佳实践是分批删除
            /// 这里手动删除的原因是：改变image路径并没有触发statefulset重建, 那只能出此下策
            /// 事实表明, 处于Pending状态的Pod, 就算添加了新的annotation, 或者label, 也不会生效
            /// 事实表明, 只有处于running中的Pod才会正常的重建
            List<K8sResource.Pod> podList = k8sPodRepository.queryByAppName(envEnum, namespace, appName);
            for (K8sResource.Pod pod : podList) {
                if (PodStatusEnum.Pending.getCode().equals(pod.getStatus())) {
                    k8sPodRepository.rebuildPod(envEnum, namespace, pod.getName());
                }
            }
            return v1StatefulSet;
        } catch (ApiException e) {
            String responseBody = e.getResponseBody();
            log.error("变更StatefulSet镜像地址失败: {}", responseBody, e);
            K8sResource.ActionExceptionBody actionExceptionBody = JSON.parseObject(responseBody, K8sResource.ActionExceptionBody.class);
            if (actionExceptionBody != null) {
                throw new BadRequestException("变更StatefulSet镜像地址失败, 原因：" + actionExceptionBody.getReason());
            }
            throw new BadRequestException("变更StatefulSet镜像地址失败");
        } catch (Exception e) {
            log.error("变更StatefulSet镜像地址失败:", e);
            throw new BadRequestException("变更StatefulSet镜像地址失败");
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
    public V1StatefulSet changePodSpecs(@NotNull EnvEnum envEnum, @NotNull String namespace, @NotNull String appName, @NotNull Integer cpuNum,
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
            String statefulSetName = K8sResourceTool.getStatefulSetName(appName);
            V1StatefulSet statefulSet = appsV1Api.readNamespacedStatefulSet(statefulSetName, namespace, null, null, null);
            if (statefulSet == null || statefulSet.getSpec() == null) {
                throw new BadRequestException(statefulSetName + " 不存在");
            }
            V1PodSpec podSpec = statefulSet.getSpec().getTemplate().getSpec();
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
            V1StatefulSet v1StatefulSet = appsV1Api.replaceNamespacedStatefulSet(statefulSetName, namespace, statefulSet, null, null, null);
            // 手动删除Pod, 触发重新调度, 最佳实践是分批删除
            /// 这里手动删除的原因是：改变image路径并没有触发statefulset重建, 那只能出此下策
            /// 事实表明, 处于Pending状态的Pod, 就算添加了新的annotation, 或者label, 也不会生效
            /// 事实表明, 只有处于running中的Pod才会正常的重建
            List<K8sResource.Pod> podList = k8sPodRepository.queryByAppName(envEnum, namespace, appName);
            for (K8sResource.Pod pod : podList) {
                if (PodStatusEnum.Pending.getCode().equals(pod.getStatus())) {
                    k8sPodRepository.rebuildPod(envEnum, namespace, pod.getName());
                }
            }
            return v1StatefulSet;
        } catch (ApiException e) {
            String responseBody = e.getResponseBody();
            log.error("变更StatefulSet镜像地址失败: {}", responseBody, e);
            K8sResource.ActionExceptionBody actionExceptionBody = JSON.parseObject(responseBody, K8sResource.ActionExceptionBody.class);
            if (actionExceptionBody != null) {
                throw new BadRequestException("变更StatefulSet镜像地址失败, 原因：" + actionExceptionBody.getReason());
            }
            throw new BadRequestException("变更StatefulSet镜像地址失败");
        } catch (Exception e) {
            log.error("变更StatefulSet镜像地址失败:", e);
            throw new BadRequestException("变更StatefulSet镜像地址失败");
        }
    }

    /**
     * 变更statefulset镜像地址
     *
     * @param namespace 命名空间
     * @param appName   应用名称
     * @param newImage  新镜像地址
     * @return /
     */
    public V1StatefulSet changeImageV2(@NotNull EnvEnum envEnum, @NotNull String namespace, @NotNull String appName, @NotNull String newImage) {
        if (StrUtil.isBlank(newImage)) {
            throw new BadRequestException("镜像地址必填");
        }
        ApiClient apiClient = k8SClientAdmin.getEnv(envEnum);
        AppsV1Api appsV1Api = new AppsV1Api(apiClient);
        String statefulSetName = K8sResourceTool.getStatefulSetName(appName);
        try {
            // 这种方式也不能使非Running中的容器重建
            String jsonPatch = "[{\"op\": \"replace\", \"path\": \"/spec/template/spec/containers/0/image\", \"value\": \"" + newImage + "\"}]";
            V1Patch patch = new V1Patch(jsonPatch);
            return appsV1Api.patchNamespacedStatefulSet(statefulSetName, namespace, patch, null, null, null, null);
        } catch (ApiException e) {
            String responseBody = e.getResponseBody();
            log.error("变更StatefulSet镜像规格失败: {}", responseBody, e);
            K8sResource.ActionExceptionBody actionExceptionBody = JSON.parseObject(responseBody, K8sResource.ActionExceptionBody.class);
            if (actionExceptionBody != null) {
                throw new BadRequestException("变更StatefulSet镜像规格失败, 原因：" + actionExceptionBody.getReason());
            }
            throw new BadRequestException("变更StatefulSet镜像规格失败");
        } catch (Exception e) {
            log.error("变更StatefulSet镜像规格失败:", e);
            throw new BadRequestException("变更StatefulSet镜像规格失败");
        }
    }

    /**
     * 删除StatefulSet
     *
     * @param namespace 命名空间
     * @param appName   应用名称
     * @return /
     */
    public V1Status remove(@NotNull EnvEnum envEnum, @NotNull String namespace, @NotNull String appName) {
        try {
            ApiClient apiClient = k8SClientAdmin.getEnv(envEnum);
            AppsV1Api appsV1Api = new AppsV1Api(apiClient);
            String statefulSetName = K8sResourceTool.getStatefulSetName(appName);
            return appsV1Api.deleteNamespacedStatefulSet(statefulSetName, namespace, null, null, null, null, null, null);
        } catch (ApiException e) {
            String responseBody = e.getResponseBody();
            log.error("删除StatefulSet失败: {}", responseBody, e);
            K8sResource.ActionExceptionBody actionExceptionBody = JSON.parseObject(responseBody, K8sResource.ActionExceptionBody.class);
            if (actionExceptionBody != null) {
                throw new BadRequestException("删除StatefulSet失败, 原因：" + actionExceptionBody.getReason());
            }
            throw new BadRequestException("删除StatefulSet失败");
        } catch (Exception e) {
            log.error("删除StatefulSet失败:", e);
            throw new BadRequestException("删除StatefulSet失败");
        }
    }

    public V1StatefulSet loadFromYml(@NotNull EnvEnum envEnum, @NotNull String namespace, @NotBlank String ymlContent) {
        try {
            V1StatefulSet statefulSet = Yaml.loadAs(ymlContent, V1StatefulSet.class);
            ApiClient apiClient = k8SClientAdmin.getEnv(envEnum);
            AppsV1Api appsV1Api = new AppsV1Api(apiClient);
            String statefulSetName = statefulSet.getMetadata().getName();
            // 执行验收测试
            appsV1Api.replaceNamespacedStatefulSet(statefulSetName, namespace, statefulSet, "false", "All", null);
            return statefulSet;
        } catch (ApiException e) {
            String responseBody = e.getResponseBody();
            log.error("从yml加载StatefulSet失败: {}", responseBody, e);
            K8sResource.ActionExceptionBody actionExceptionBody = JSON.parseObject(responseBody, K8sResource.ActionExceptionBody.class);
            if (actionExceptionBody != null) {
                throw new BadRequestException("从yml加载StatefulSet失败, 原因：" + actionExceptionBody.getReason());
            }
            throw new BadRequestException("从yml加载StatefulSet失败");
        } catch (Exception e) {
            log.error("从yml加载StatefulSet失败:", e);
            throw new BadRequestException("从yml加载StatefulSet失败");
        }
    }
}
