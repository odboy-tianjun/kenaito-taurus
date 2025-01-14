package cn.odboy.repository;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Assert;
import cn.odboy.model.request.K8sPod;
import cn.odboy.model.request.K8sStatefulSet;
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
import cn.odboy.constant.K8sActionReasonCodeEnum;
import cn.odboy.constant.K8sPodStatusEnum;
import cn.odboy.context.K8sClientAdmin;
import cn.odboy.infra.exception.BadRequestException;
import cn.odboy.model.response.K8sResource;
import cn.odboy.util.K8sDryRunUtil;
import cn.odboy.util.K8sResourceNameUtil;
import cn.odboy.util.ValidationUtil;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Objects;

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
        try {
            String statefulSetName = K8sResourceNameUtil.getStatefulSetName(args.getAppName(), k8SClientAdmin.getEnvCode(args.getClusterCode()));
            String podName = K8sResourceNameUtil.getPodName(args.getAppName(), k8SClientAdmin.getEnvCode(args.getClusterCode()));
            Map<String, String> labels = K8sResourceNameUtil.getLabelsMap(args.getAppName());
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
                    .withNamespace(args.getAppName())
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
            ApiClient apiClient = k8SClientAdmin.getClient(args.getClusterCode());
            AppsV1Api api = new AppsV1Api(apiClient);
            return api.createNamespacedStatefulSet(args.getAppName(), statefulSet, null, K8sDryRunUtil.transferState(args.getDryRun()), null);
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
        ValidationUtil.validate(args);
        try {
            ApiClient apiClient = k8SClientAdmin.getClient(args.getClusterCode());
            AppsV1Api appsV1Api = new AppsV1Api(apiClient);
            String statefulSetName = K8sResourceNameUtil.getStatefulSetName(args.getAppName(), k8SClientAdmin.getEnvCode(args.getClusterCode()));
            V1StatefulSet statefulSet = appsV1Api.readNamespacedStatefulSet(statefulSetName, args.getAppName(), null, null, null);
            if (statefulSet == null || statefulSet.getSpec() == null) {
                throw new BadRequestException(statefulSetName + " 不存在");
            }
            statefulSet.getSpec().setReplicas(args.getNewReplicas());
            return appsV1Api.replaceNamespacedStatefulSet(statefulSetName, args.getAppName(), statefulSet, null, K8sDryRunUtil.transferState(args.getDryRun()), null);
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
        ValidationUtil.validate(args);
        try {
            ApiClient apiClient = k8SClientAdmin.getClient(args.getClusterCode());
            AppsV1Api appsV1Api = new AppsV1Api(apiClient);
            String statefulSetName = K8sResourceNameUtil.getStatefulSetName(args.getAppName(), k8SClientAdmin.getEnvCode(args.getClusterCode()));
            V1StatefulSet statefulSet = appsV1Api.readNamespacedStatefulSet(statefulSetName, args.getAppName(), null, null, null);
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
            // 手动删除Pod, 触发重新调度, 最佳实践是分批删除
            /// 这里手动删除的原因是：改变image路径并没有触发statefulset重建, 那只能出此下策
            /// 事实表明, 处于Pending状态的Pod, 就算添加了新的annotation, 或者label, 也不会生效
            /// 事实表明, 只有处于running中的Pod才会正常的重建
            List<K8sResource.Pod> podList = k8sPodRepository.listPods(args.getClusterCode(), args.getAppName(), statefulSetName);
            for (K8sResource.Pod pod : podList) {
                if (K8sPodStatusEnum.Pending.getCode().equals(pod.getStatus())) {
                    K8sPod.RebuildArgs rebuildArgs = new K8sPod.RebuildArgs();
                    rebuildArgs.setClusterCode(args.getClusterCode());
                    rebuildArgs.setPodName(pod.getName());
                    rebuildArgs.setNamespace(pod.getNamespace());
                    k8sPodRepository.rebuildPod(rebuildArgs);
                }
            }
            return appsV1Api.replaceNamespacedStatefulSet(statefulSetName, args.getAppName(), statefulSet, null, K8sDryRunUtil.transferState(args.getDryRun()), null);
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
        ValidationUtil.validate(args);
        try {
            ApiClient apiClient = k8SClientAdmin.getClient(args.getClusterCode());
            AppsV1Api appsV1Api = new AppsV1Api(apiClient);
            String statefulSetName = K8sResourceNameUtil.getStatefulSetName(args.getAppName(), k8SClientAdmin.getEnvCode(args.getClusterCode()));
            V1StatefulSet statefulSet = appsV1Api.readNamespacedStatefulSet(statefulSetName, args.getAppName(), null, null, null);
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
            // 手动删除Pod, 触发重新调度, 最佳实践是分批删除
            /// 这里手动删除的原因是：改变image路径并没有触发statefulset重建, 那只能出此下策
            /// 事实表明, 处于Pending状态的Pod, 就算添加了新的annotation, 或者label, 也不会生效
            /// 事实表明, 只有处于running中的Pod才会正常的重建
            List<K8sResource.Pod> podList = k8sPodRepository.listPods(args.getClusterCode(), args.getAppName(), statefulSetName);
            for (K8sResource.Pod pod : podList) {
                if (K8sPodStatusEnum.Pending.getCode().equals(pod.getStatus())) {
                    K8sPod.RebuildArgs rebuildArgs = new K8sPod.RebuildArgs();
                    rebuildArgs.setClusterCode(args.getClusterCode());
                    rebuildArgs.setPodName(pod.getName());
                    rebuildArgs.setNamespace(pod.getNamespace());
                    k8sPodRepository.rebuildPod(rebuildArgs);
                }
            }
            return appsV1Api.replaceNamespacedStatefulSet(statefulSetName, args.getAppName(), statefulSet, null, K8sDryRunUtil.transferState(args.getDryRun()), null);
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
        ValidationUtil.validate(args);
        try {
            ApiClient apiClient = k8SClientAdmin.getClient(args.getClusterCode());
            AppsV1Api appsV1Api = new AppsV1Api(apiClient);
            String statefulSetName = K8sResourceNameUtil.getStatefulSetName(args.getAppName(), k8SClientAdmin.getEnvCode(args.getClusterCode()));
            // 这种方式也不能使非Running中的容器重建
            String jsonPatch = "[{\"op\": \"replace\", \"path\": \"/spec/template/spec/containers/0/image\", \"value\": \"" + args.getNewImage() + "\"}]";
            V1Patch patch = new V1Patch(jsonPatch);
            return appsV1Api.patchNamespacedStatefulSet(statefulSetName, args.getAppName(), patch, null, K8sDryRunUtil.transferState(args.getDryRun()), null, null);
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
        ValidationUtil.validate(args);
        try {
            ApiClient apiClient = k8SClientAdmin.getClient(args.getClusterCode());
            AppsV1Api appsV1Api = new AppsV1Api(apiClient);
            String statefulSetName = K8sResourceNameUtil.getStatefulSetName(args.getAppName(), k8SClientAdmin.getEnvCode(args.getClusterCode()));
            return appsV1Api.deleteNamespacedStatefulSet(statefulSetName, args.getAppName(), null, K8sDryRunUtil.transferState(args.getDryRun()), null, null, null, null);
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
     * 根据appName获取StatefulSet
     *
     * @return /
     */
    public V1StatefulSet describeStatefulSetByAppName(String clusterCode, String appName) {
        Assert.notEmpty(clusterCode, "集群编码不能为空");
        Assert.notEmpty(appName, "应用名称不能为空");
        try {
            ApiClient apiClient = k8SClientAdmin.getClient(clusterCode);
            AppsV1Api appsV1Api = new AppsV1Api(apiClient);
            String statefulSetName = K8sResourceNameUtil.getStatefulSetName(appName, clusterCode);
            return appsV1Api.readNamespacedStatefulSet(statefulSetName, appName, "false", null, null);
        } catch (ApiException e) {
            String responseBody = e.getResponseBody();
            K8sResource.ActionExceptionBody actionExceptionBody = JSON.parseObject(responseBody, K8sResource.ActionExceptionBody.class);
            if (actionExceptionBody != null) {
                if (actionExceptionBody.getReason().contains(K8sActionReasonCodeEnum.NotFound.getCode())) {
                    return null;
                }
                log.error("根据appName获取StatefulSet失败: {}", responseBody, e);
                throw new BadRequestException("根据appName获取StatefulSet失败, 原因：" + actionExceptionBody.getReason());
            }
            throw new BadRequestException("根据appName获取StatefulSet失败");
        } catch (Exception e) {
            log.error("根据appName获取StatefulSet失败:", e);
            throw new BadRequestException("根据appName获取StatefulSet失败");
        }
    }

    /**
     * 根据name获取StatefulSet
     *
     * @return /
     */
    public V1StatefulSet describeStatefulSetByName(String clusterCode, String name, String namespace) {
        Assert.notEmpty(clusterCode, "集群编码不能为空");
        Assert.notEmpty(name, "名称不能为空");
        Assert.notEmpty(namespace, "命名空间不能为空");
        try {
            ApiClient apiClient = k8SClientAdmin.getClient(clusterCode);
            AppsV1Api appsV1Api = new AppsV1Api(apiClient);
            return appsV1Api.readNamespacedStatefulSet(name, namespace, "false", null, null);
        } catch (ApiException e) {
            String responseBody = e.getResponseBody();
            K8sResource.ActionExceptionBody actionExceptionBody = JSON.parseObject(responseBody, K8sResource.ActionExceptionBody.class);
            if (actionExceptionBody != null) {
                if (actionExceptionBody.getReason().contains(K8sActionReasonCodeEnum.NotFound.getCode())) {
                    return null;
                }
                log.error("根据name获取StatefulSet失败: {}", responseBody, e);
                throw new BadRequestException("根据name获取StatefulSet失败, 原因：" + actionExceptionBody.getReason());
            }
            throw new BadRequestException("根据name获取StatefulSet失败");
        } catch (Exception e) {
            log.error("根据name获取StatefulSet失败:", e);
            throw new BadRequestException("根据name获取StatefulSet失败");
        }
    }

    /**
     * 从yaml文件内容加载StatefulSet
     *
     * @param args /
     */
    public V1StatefulSet loadStatefulSetFromYaml(K8sStatefulSet.LoadFromYamlArgs args) {
        ValidationUtil.validate(args);
        try {
            V1StatefulSet statefulSet = Yaml.loadAs(args.getYamlContent(), V1StatefulSet.class);
            ApiClient apiClient = k8SClientAdmin.getClient(args.getClusterCode());
            AppsV1Api appsV1Api = new AppsV1Api(apiClient);
            String statefulSetName = Objects.requireNonNull(statefulSet.getMetadata()).getName();
            String namespace = Objects.requireNonNull(statefulSet.getMetadata()).getNamespace();
            V1StatefulSet statefulSetByName = describeStatefulSetByName(args.getClusterCode(), statefulSetName, namespace);
            if (statefulSetByName == null) {
                appsV1Api.createNamespacedStatefulSet(namespace, statefulSet, "false", K8sDryRunUtil.transferState(args.getDryRun()), null);
            } else {
                appsV1Api.replaceNamespacedStatefulSet(statefulSetName, namespace, statefulSet, "false", K8sDryRunUtil.transferState(args.getDryRun()), null);
            }
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
