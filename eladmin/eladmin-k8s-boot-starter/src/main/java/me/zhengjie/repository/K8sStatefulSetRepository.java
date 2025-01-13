package me.zhengjie.repository;

import cn.hutool.core.collection.CollUtil;
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
import me.zhengjie.constant.K8sPodStatusEnum;
import me.zhengjie.context.K8sClientAdmin;
import me.zhengjie.infra.exception.BadRequestException;
import me.zhengjie.model.K8sPod;
import me.zhengjie.model.K8sResource;
import me.zhengjie.model.K8sStatefulSet;
import me.zhengjie.util.K8sDryRunUtil;
import me.zhengjie.util.K8sNameUtil;
import me.zhengjie.util.ValidationUtil;
import org.springframework.stereotype.Component;

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
     * @param args /
     */
    public V1StatefulSet createStatefulSet(K8sStatefulSet.CreateArgs args) {
        ValidationUtil.validate(args);
        String statefulSetName = K8sNameUtil.getStatefulSetName(args.getAppName());
        String podName = K8sNameUtil.getPodName(args.getAppName());
        Map<String, String> labels = K8sNameUtil.getLabelsMap(args.getAppName());
        V1Container v1Container = new V1Container();
        v1Container.setName(podName);
        v1Container.setImage(args.getImage());
        v1Container.setPorts(CollUtil.newArrayList(new V1ContainerPort().containerPort(args.getPort())));
        // 存活检测
        v1Container.setLivenessProbe(new V1Probe().tcpSocket(new V1TCPSocketAction().port(new IntOrString(args.getPort()))));
        // 就绪检测
        v1Container.setReadinessProbe(new V1Probe().tcpSocket(new V1TCPSocketAction().port(new IntOrString(args.getPort()))));
        v1Container.setResources(new V1ResourceRequirements()
                .putRequestsItem("cpu", new Quantity(String.valueOf(args.getRequestCpuNum())))
                .putRequestsItem("memory", new Quantity(args.getRequestMemNum() + "Gi"))
                .putLimitsItem("cpu", new Quantity(String.valueOf(args.getLimitsCpuNum())))
                .putLimitsItem("memory", new Quantity(args.getLimitsMemNum() + "Gi"))
        );
        // 构建statefulset的yaml对象
        V1StatefulSet statefulSet = new V1StatefulSetBuilder()
                .withNewMetadata()
                .withName(statefulSetName)
                .withNamespace(args.getNamespace())
                .withAnnotations(args.getAnnotations())
                .endMetadata()
                .withNewSpec()
                .withServiceName(statefulSetName)
                .withReplicas(args.getReplicas())
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
        ApiClient apiClient = k8SClientAdmin.getEnv(args.getClusterCode());
        AppsV1Api api = new AppsV1Api(apiClient);
        try {
            return api.createNamespacedStatefulSet(args.getNamespace(), statefulSet, null, K8sDryRunUtil.transferState(args.getDryRun()), null);
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
     * @param args /
     */
    public V1StatefulSet changeStatefulSetReplicas(K8sStatefulSet.ChangeReplicasArgs args) {
        try {
            ValidationUtil.validate(args);
            ApiClient apiClient = k8SClientAdmin.getEnv(args.getClusterCode());
            AppsV1Api appsV1Api = new AppsV1Api(apiClient);
            String statefulSetName = K8sNameUtil.getStatefulSetName(args.getAppName());
            V1StatefulSet statefulSet = appsV1Api.readNamespacedStatefulSet(statefulSetName, args.getNamespace(), null, null, null);
            if (statefulSet == null || statefulSet.getSpec() == null) {
                throw new BadRequestException(statefulSetName + " 不存在");
            }
            statefulSet.getSpec().setReplicas(args.getNewReplicas());
            return appsV1Api.replaceNamespacedStatefulSet(statefulSetName, args.getNamespace(), statefulSet, null, K8sDryRunUtil.transferState(args.getDryRun()), null);
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
     * @param args /
     */
    public V1StatefulSet changeStatefulSetImage(K8sStatefulSet.ChangeImageArgs args) {
        try {
            ValidationUtil.validate(args);
            ApiClient apiClient = k8SClientAdmin.getEnv(args.getClusterCode());
            AppsV1Api appsV1Api = new AppsV1Api(apiClient);
            String statefulSetName = K8sNameUtil.getStatefulSetName(args.getAppName());
            V1StatefulSet statefulSet = appsV1Api.readNamespacedStatefulSet(statefulSetName, args.getNamespace(), null, null, null);
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
                    if (container.getName().contains(args.getAppName())) {
                        container.setImage(args.getNewImage());
                        break;
                    }
                }
            }
            V1StatefulSet v1StatefulSet = appsV1Api.replaceNamespacedStatefulSet(statefulSetName, args.getNamespace(), statefulSet, null, K8sDryRunUtil.transferState(args.getDryRun()), null);
            // 手动删除Pod, 触发重新调度, 最佳实践是分批删除
            /// 这里手动删除的原因是：改变image路径并没有触发statefulset重建, 那只能出此下策
            /// 事实表明, 处于Pending状态的Pod, 就算添加了新的annotation, 或者label, 也不会生效
            /// 事实表明, 只有处于running中的Pod才会正常的重建
            List<K8sResource.Pod> podList = k8sPodRepository.listPods(args.getClusterCode(), args.getNamespace(), args.getAppName());
            for (K8sResource.Pod pod : podList) {
                if (K8sPodStatusEnum.Pending.getCode().equals(pod.getStatus())) {
                    K8sPod.RebuildArgs rebuildArgs = new K8sPod.RebuildArgs();
                    rebuildArgs.setClusterCode(args.getClusterCode());
                    rebuildArgs.setNamespace(args.getNamespace());
                    rebuildArgs.setPodName(pod.getName());
                    k8sPodRepository.rebuildPod(rebuildArgs);
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
     * @param args /
     * @return /
     */
    public V1StatefulSet changeStatefulSetSpecs(K8sStatefulSet.ChangeSpecsArgs args) {
        try {
            ValidationUtil.validate(args);
            ApiClient apiClient = k8SClientAdmin.getEnv(args.getClusterCode());
            AppsV1Api appsV1Api = new AppsV1Api(apiClient);
            String statefulSetName = K8sNameUtil.getStatefulSetName(args.getAppName());
            V1StatefulSet statefulSet = appsV1Api.readNamespacedStatefulSet(statefulSetName, args.getNamespace(), null, null, null);
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
                    if (container.getName().contains(args.getAppName())) {
                        V1ResourceRequirements resources = container.getResources();
                        if (resources == null) {
                            resources = new V1ResourceRequirements();
                        }
                        resources
                                .putRequestsItem("cpu", new Quantity(String.valueOf(args.getRequestCpuNum())))
                                .putRequestsItem("memory", new Quantity(args.getRequestMemNum() + "Gi"))
                                .putLimitsItem("cpu", new Quantity(String.valueOf(args.getLimitsCpuNum())))
                                .putLimitsItem("memory", new Quantity(args.getLimitsMemNum() + "Gi"));
                        break;
                    }
                }
            }
            V1StatefulSet v1StatefulSet = appsV1Api.replaceNamespacedStatefulSet(statefulSetName, args.getNamespace(), statefulSet, null, K8sDryRunUtil.transferState(args.getDryRun()), null);
            // 手动删除Pod, 触发重新调度, 最佳实践是分批删除
            /// 这里手动删除的原因是：改变image路径并没有触发statefulset重建, 那只能出此下策
            /// 事实表明, 处于Pending状态的Pod, 就算添加了新的annotation, 或者label, 也不会生效
            /// 事实表明, 只有处于running中的Pod才会正常的重建
            List<K8sResource.Pod> podList = k8sPodRepository.listPods(args.getClusterCode(), args.getNamespace(), args.getAppName());
            for (K8sResource.Pod pod : podList) {
                if (K8sPodStatusEnum.Pending.getCode().equals(pod.getStatus())) {
                    K8sPod.RebuildArgs rebuildArgs = new K8sPod.RebuildArgs();
                    rebuildArgs.setClusterCode(args.getClusterCode());
                    rebuildArgs.setNamespace(args.getNamespace());
                    rebuildArgs.setPodName(pod.getName());
                    k8sPodRepository.rebuildPod(rebuildArgs);
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
     * @param args /
     */
    public V1StatefulSet changeStatefulSetImageV2(K8sStatefulSet.ChangeImageArgs args) {
        try {
            ValidationUtil.validate(args);
            ApiClient apiClient = k8SClientAdmin.getEnv(args.getClusterCode());
            AppsV1Api appsV1Api = new AppsV1Api(apiClient);
            String statefulSetName = K8sNameUtil.getStatefulSetName(args.getAppName());
            // 这种方式也不能使非Running中的容器重建
            String jsonPatch = "[{\"op\": \"replace\", \"path\": \"/spec/template/spec/containers/0/image\", \"value\": \"" + args.getNewImage() + "\"}]";
            V1Patch patch = new V1Patch(jsonPatch);
            return appsV1Api.patchNamespacedStatefulSet(statefulSetName, args.getNamespace(), patch, null, K8sDryRunUtil.transferState(args.getDryRun()), null, null);
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
     * @param args /
     */
    public V1Status deleteStatefulSet(K8sStatefulSet.DeleteArgs args) {
        try {
            ValidationUtil.validate(args);
            ApiClient apiClient = k8SClientAdmin.getEnv(args.getClusterCode());
            AppsV1Api appsV1Api = new AppsV1Api(apiClient);
            String statefulSetName = K8sNameUtil.getStatefulSetName(args.getAppName());
            return appsV1Api.deleteNamespacedStatefulSet(statefulSetName, args.getNamespace(), null, K8sDryRunUtil.transferState(args.getDryRun()), null, null, null, null);
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

    /**
     * 从yaml文件内容加载StatefulSet
     *
     * @param args /
     */
    public V1StatefulSet loadStatefulSetFromYaml(K8sStatefulSet.LoadFromYamlArgs args) {
        try {
            V1StatefulSet statefulSet = Yaml.loadAs(args.getYamlContent(), V1StatefulSet.class);
            ApiClient apiClient = k8SClientAdmin.getEnv(args.getClusterCode());
            AppsV1Api appsV1Api = new AppsV1Api(apiClient);
            String statefulSetName = statefulSet.getMetadata().getName();
            appsV1Api.replaceNamespacedStatefulSet(statefulSetName, args.getNamespace(), statefulSet, "false", K8sDryRunUtil.transferState(args.getDryRun()), null);
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
