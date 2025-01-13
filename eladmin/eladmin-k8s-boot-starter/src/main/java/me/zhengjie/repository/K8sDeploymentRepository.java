package me.zhengjie.repository;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson.JSON;
import io.kubernetes.client.custom.Quantity;
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
import me.zhengjie.model.K8sDeployment;
import me.zhengjie.model.K8sPod;
import me.zhengjie.model.K8sResource;
import me.zhengjie.util.K8sDryRunUtil;
import me.zhengjie.util.K8sNameUtil;
import me.zhengjie.util.ValidationUtil;
import org.springframework.stereotype.Component;

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
     * @param args /
     */
    public V1Deployment createDeployment(K8sDeployment.CreateArgs args) {
        try {
            ValidationUtil.validate(args);
            Map<String, String> labels = K8sNameUtil.getLabelsMap(args.getAppName());
            String deploymentName = K8sNameUtil.getDeploymentName(args.getAppName());
            // 构建deployment的yaml对象
            V1Deployment deployment = new V1DeploymentBuilder()
                    .withNewMetadata()
                    .withName(deploymentName)
                    .withNamespace(args.getNamespace())
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
                            .name(K8sNameUtil.getPodName(args.getAppName()))
                            .image(args.getImage())
                            .ports(CollUtil.newArrayList(new V1ContainerPort().containerPort(args.getPort())))
                    )
                    .endSpec()
                    .endTemplate()
                    .endSpec()
                    .build();
            ApiClient apiClient = k8SClientAdmin.getClientEnv(args.getClusterCode());
            AppsV1Api appsV1Api = new AppsV1Api(apiClient);
            return appsV1Api.createNamespacedDeployment(args.getNamespace(), deployment, "false", K8sDryRunUtil.transferState(args.getDryRun()), null);
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
        try {
            ValidationUtil.validate(args);
            ApiClient apiClient = k8SClientAdmin.getClientEnv(args.getClusterCode());
            AppsV1Api appsV1Api = new AppsV1Api(apiClient);
            String deploymentName = K8sNameUtil.getDeploymentName(args.getAppName());
            V1Deployment deployment = appsV1Api.readNamespacedDeployment(deploymentName, args.getNamespace(), "false", null, null);
            if (deployment == null || deployment.getSpec() == null) {
                throw new BadRequestException(deploymentName + " 不存在");
            }
            deployment.getSpec().setReplicas(args.getNewReplicas());
            return appsV1Api.replaceNamespacedDeployment(deploymentName, args.getNamespace(), deployment, "false", K8sDryRunUtil.transferState(args.getDryRun()), null);
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
        try {
            ValidationUtil.validate(args);
            ApiClient apiClient = k8SClientAdmin.getClientEnv(args.getClusterCode());
            AppsV1Api appsV1Api = new AppsV1Api(apiClient);
            String deploymentName = K8sNameUtil.getDeploymentName(args.getAppName());
            V1Deployment deployment = appsV1Api.readNamespacedDeployment(deploymentName, args.getNamespace(), "false", null, null);
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
            V1Deployment v1Deployment = appsV1Api.replaceNamespacedDeployment(deploymentName, args.getNamespace(), deployment, "false", K8sDryRunUtil.transferState(args.getDryRun()), null);
            // 手动删除Pod, 触发重新调度, 最佳实践是分批删除
            /// 这里手动删除的原因是：改变image路径并没有触发statefulset重建, 那只能出此下策
            /// 事实表明, 处于Pending状态的Pod, 就算添加了新的annotation, 或者label, 也不会直接生效, 从而触发pod重建
            List<K8sResource.Pod> pods = k8sPodRepository.listPods(args.getClusterCode(), args.getNamespace(), args.getAppName());
            for (K8sResource.Pod pod : pods) {
                if (K8sPodStatusEnum.Pending.getCode().equals(pod.getStatus())) {
                    K8sPod.RebuildArgs rebuildArgs = new K8sPod.RebuildArgs();
                    rebuildArgs.setClusterCode(args.getClusterCode());
                    rebuildArgs.setNamespace(args.getNamespace());
                    rebuildArgs.setPodName(pod.getName());
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
        try {
            ValidationUtil.validate(args);
            ApiClient apiClient = k8SClientAdmin.getClientEnv(args.getClusterCode());
            AppsV1Api appsV1Api = new AppsV1Api(apiClient);
            String deploymentName = K8sNameUtil.getDeploymentName(args.getAppName());
            V1Deployment deployment = appsV1Api.readNamespacedDeployment(deploymentName, args.getNamespace(), "false", null, null);
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
            V1Deployment v1Deployment = appsV1Api.replaceNamespacedDeployment(deploymentName, args.getNamespace(), deployment, "false", K8sDryRunUtil.transferState(args.getDryRun()), null);
            List<K8sResource.Pod> pods = k8sPodRepository.listPods(args.getClusterCode(), args.getNamespace(), args.getAppName());
            for (K8sResource.Pod pod : pods) {
                if (K8sPodStatusEnum.Pending.getCode().equals(pod.getStatus())) {
                    K8sPod.RebuildArgs rebuildArgs = new K8sPod.RebuildArgs();
                    rebuildArgs.setClusterCode(args.getClusterCode());
                    rebuildArgs.setNamespace(args.getNamespace());
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
        try {
            ValidationUtil.validate(args);
            ApiClient apiClient = k8SClientAdmin.getClientEnv(args.getClusterCode());
            AppsV1Api appsV1Api = new AppsV1Api(apiClient);
            return appsV1Api.deleteNamespacedDeployment(args.getDeploymentName(), args.getNamespace(), "false", K8sDryRunUtil.transferState(args.getDryRun()), null, null, null, null);
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

    public V1Deployment getDeploymentByName(@NotNull String clusterCode, @NotNull String namespace, @NotNull String deploymentName) {
        ApiClient apiClient = k8SClientAdmin.getClientEnv(clusterCode);
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

    public V1Deployment loadDeploymentFromYaml(K8sDeployment.LoadFromYamlArgs args) {
        try {
            ValidationUtil.validate(args);
            V1Deployment deployment = Yaml.loadAs(args.getYamlContent(), V1Deployment.class);
            ApiClient apiClient = k8SClientAdmin.getClientEnv(args.getClusterCode());
            AppsV1Api appsV1Api = new AppsV1Api(apiClient);
            String deploymentName = deployment.getMetadata().getName();
            appsV1Api.replaceNamespacedDeployment(deploymentName, args.getNamespace(), deployment, "false", K8sDryRunUtil.transferState(args.getDryRun()), null);
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
