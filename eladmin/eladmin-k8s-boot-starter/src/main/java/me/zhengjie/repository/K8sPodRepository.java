package me.zhengjie.repository;

import com.alibaba.fastjson.JSON;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1ObjectMeta;
import io.kubernetes.client.openapi.models.V1PodList;
import io.kubernetes.client.openapi.models.V1PodSpec;
import io.kubernetes.client.openapi.models.V1PodStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.zhengjie.context.K8sClientAdmin;
import me.zhengjie.infra.exception.BadRequestException;
import me.zhengjie.model.request.K8sPod;
import me.zhengjie.model.response.K8sResource;
import me.zhengjie.util.K8sDryRunUtil;
import me.zhengjie.util.K8sResourceNameUtil;
import me.zhengjie.util.ValidationUtil;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Kubernetes Pod Repository
 *
 * @author odboy
 * @date 2024-09-26
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class K8sPodRepository {
    private final K8sClientAdmin k8SClientAdmin;

    public List<K8sResource.Pod> listPods(@NotNull String clusterCode, Map<String, String> fieldSelector, Map<String, String> labelSelector) {
        try {
            ApiClient apiClient = k8SClientAdmin.getClient(clusterCode);
            CoreV1Api coreV1Api = new CoreV1Api(apiClient);
            String fieldSelectorStr = K8sResourceNameUtil.genLabelSelectorExpression(fieldSelector);
            String labelSelectorStr = K8sResourceNameUtil.genLabelSelectorExpression(labelSelector);
            V1PodList podList = coreV1Api.listPodForAllNamespaces(null, null, fieldSelectorStr, labelSelectorStr, null, "false", null, null, null, null);
            return podList.getItems().stream().map(pod -> {
                V1ObjectMeta metadata = pod.getMetadata();
                if (metadata != null && metadata.getName() != null) {
                    K8sResource.Pod server = new K8sResource.Pod();
                    if (metadata.getCreationTimestamp() != null) {
                        server.setCreateTime(Date.from(metadata.getCreationTimestamp().toInstant()));
                    }
                    if (metadata.getDeletionTimestamp() != null) {
                        // 当Pod被删除时, 这个属性有值
                        server.setDeleteTime(Date.from(metadata.getDeletionTimestamp().toInstant()));
                    }
                    server.setName(metadata.getName());
                    server.setLabels(metadata.getLabels());
                    server.setNamespace(metadata.getNamespace());
                    server.setResourceVersion(metadata.getResourceVersion());
                    V1PodSpec spec = pod.getSpec();
                    if (spec != null) {
                        server.setRestartPolicy(spec.getRestartPolicy());
                    }
                    V1PodStatus podStatus = pod.getStatus();
                    if (podStatus != null) {
                        server.setIp(podStatus.getPodIP());
                        server.setStatus(server.getDeleteTime() != null ? "Terminated" : podStatus.getPhase());
                        server.setQosClass(podStatus.getQosClass());
                        if (podStatus.getStartTime() != null) {
                            server.setStartTime(Date.from(podStatus.getStartTime().toInstant()));
                        }
                        server.setConditions(podStatus.getConditions());
                    }
                    return server;
                }
                return null;
            }).filter(Objects::nonNull).collect(Collectors.toList());
        } catch (ApiException e) {
            String responseBody = e.getResponseBody();
            log.error("获取Pod列表失败: {}", responseBody, e);
            return new ArrayList<>();
        } catch (Exception e) {
            log.error("获取Pod列表失败:", e);
            return new ArrayList<>();
        }
    }

    /**
     * 查询pod列表
     *
     * @param namespace     命名空间
     * @param labelSelector pod标签键值对
     * @return /
     */
    public List<K8sResource.Pod> listPods(@NotNull String clusterCode, @NotNull String namespace, Map<String, String> labelSelector) {
        try {
            ApiClient apiClient = k8SClientAdmin.getClient(clusterCode);
            CoreV1Api coreV1Api = new CoreV1Api(apiClient);
            String labelSelectorStr = K8sResourceNameUtil.genLabelSelectorExpression(labelSelector);
            V1PodList podList = coreV1Api.listNamespacedPod(namespace, "false", null, null, null, labelSelectorStr, null, null, null, null, null);
            return podList.getItems().stream().map(pod -> {
                K8sResource.Pod server = new K8sResource.Pod();
                V1ObjectMeta metadata = pod.getMetadata();
                if (metadata != null) {
                    if (metadata.getCreationTimestamp() != null) {
                        server.setCreateTime(Date.from(metadata.getCreationTimestamp().toInstant()));
                    }
                    if (metadata.getDeletionTimestamp() != null) {
                        // 当Pod被删除时, 这个属性有值
                        server.setDeleteTime(Date.from(metadata.getDeletionTimestamp().toInstant()));
                    }
                    server.setName(metadata.getName());
                    server.setLabels(metadata.getLabels());
                    server.setNamespace(metadata.getNamespace());
                    server.setResourceVersion(metadata.getResourceVersion());
                }
                V1PodSpec spec = pod.getSpec();
                if (spec != null) {
                    server.setRestartPolicy(spec.getRestartPolicy());
                }
                V1PodStatus podStatus = pod.getStatus();
                if (podStatus != null) {
                    server.setIp(podStatus.getPodIP());
                    server.setStatus(server.getDeleteTime() != null ? "Terminated" : podStatus.getPhase());
                    server.setQosClass(podStatus.getQosClass());
                    if (podStatus.getStartTime() != null) {
                        server.setStartTime(Date.from(podStatus.getStartTime().toInstant()));
                    }
                    server.setConditions(podStatus.getConditions());
                }
                return server;
            }).collect(Collectors.toList());
        } catch (ApiException e) {
            String responseBody = e.getResponseBody();
            log.error("获取Pod列表失败: {}", responseBody, e);
            return new ArrayList<>();
        } catch (Exception e) {
            log.error("获取Pod列表失败:", e);
            return new ArrayList<>();
        }
    }

    public List<K8sResource.Pod> listPods(@NotNull String clusterCode, @NotNull String namespace, @NotNull String podName) {
        try {
            ApiClient apiClient = k8SClientAdmin.getClient(clusterCode);
            CoreV1Api coreV1Api = new CoreV1Api(apiClient);
            Map<String, String> labelSelector = K8sResourceNameUtil.getLabelsMap(namespace);
            String labelSelectorStr = K8sResourceNameUtil.genLabelSelectorExpression(labelSelector);
            V1PodList podList = coreV1Api.listNamespacedPod(namespace, "false", null, null, null, labelSelectorStr, null, null, null, null, null);
            return podList.getItems().stream().map(pod -> {
                V1ObjectMeta metadata = pod.getMetadata();
                if (metadata != null && metadata.getName() != null) {
                    if (metadata.getName().contains(podName)) {
                        K8sResource.Pod server = new K8sResource.Pod();
                        if (metadata.getCreationTimestamp() != null) {
                            server.setCreateTime(Date.from(metadata.getCreationTimestamp().toInstant()));
                        }
                        if (metadata.getDeletionTimestamp() != null) {
                            // 当Pod被删除时, 这个属性有值
                            server.setDeleteTime(Date.from(metadata.getDeletionTimestamp().toInstant()));
                        }
                        server.setName(metadata.getName());
                        server.setLabels(metadata.getLabels());
                        server.setNamespace(metadata.getNamespace());
                        server.setResourceVersion(metadata.getResourceVersion());
                        V1PodSpec spec = pod.getSpec();
                        if (spec != null) {
                            server.setRestartPolicy(spec.getRestartPolicy());
                        }
                        V1PodStatus podStatus = pod.getStatus();
                        if (podStatus != null) {
                            server.setIp(podStatus.getPodIP());
                            server.setStatus(server.getDeleteTime() != null ? "Terminated" : podStatus.getPhase());
                            server.setQosClass(podStatus.getQosClass());
                            if (podStatus.getStartTime() != null) {
                                server.setStartTime(Date.from(podStatus.getStartTime().toInstant()));
                            }
                            server.setConditions(podStatus.getConditions());
                        }
                        return server;
                    }
                }
                return null;
            }).filter(Objects::nonNull).collect(Collectors.toList());
        } catch (ApiException e) {
            String responseBody = e.getResponseBody();
            log.error("获取Pod列表失败: {}", responseBody, e);
            return new ArrayList<>();
        } catch (Exception e) {
            log.error("获取Pod列表失败:", e);
            return new ArrayList<>();
        }
    }

    /**
     * 重建/重启Pod, 通过删除Pod重建
     *
     * @param args /
     */
    public void rebuildPod(K8sPod.RebuildArgs args) {
        ValidationUtil.validate(args);
        try {
            ApiClient apiClient = k8SClientAdmin.getClient(args.getClusterCode());
            CoreV1Api coreV1Api = new CoreV1Api(apiClient);
            coreV1Api.deleteNamespacedPod(args.getPodName(), args.getNamespace(), "false", K8sDryRunUtil.transferState(args.getDryRun()), null, null, null, null);
        } catch (ApiException e) {
            String responseBody = e.getResponseBody();
            log.error("重建Pod失败: {}", responseBody, e);
            K8sResource.ActionExceptionBody actionExceptionBody = JSON.parseObject(responseBody, K8sResource.ActionExceptionBody.class);
            if (actionExceptionBody != null) {
                throw new BadRequestException("重建Pod失败, 原因：" + actionExceptionBody.getReason());
            }
            throw new BadRequestException("重建Pod失败");
        } catch (Exception e) {
            log.error("重建Pod失败:", e);
            throw new BadRequestException("重建Pod失败");
        }
    }
}
