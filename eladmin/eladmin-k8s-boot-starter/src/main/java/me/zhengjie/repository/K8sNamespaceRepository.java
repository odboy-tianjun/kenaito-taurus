package me.zhengjie.repository;

import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1Namespace;
import io.kubernetes.client.openapi.models.V1NamespaceList;
import io.kubernetes.client.openapi.models.V1ObjectMeta;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.zhengjie.constant.EnvEnum;
import me.zhengjie.context.K8sClientAdmin;
import me.zhengjie.model.K8sResource;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
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
    public List<K8sResource.Namespace> queryList(ApiClient apiClient) {
        try {
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
            return new ArrayList<>();
        } catch (Exception e) {
            log.error("获取Namespace列表失败:", e);
            return new ArrayList<>();
        }
    }

    /**
     * 获取Namespace列表
     *
     * @return /
     */
    public K8sResource.Namespace getByAppName(EnvEnum envEnum, String appName) {
        try {
            ApiClient apiClient = k8SClientAdmin.getEnv(envEnum);
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
            log.error("根据appName获取Namespace失败: {}", responseBody, e);
            return null;
        } catch (Exception e) {
            log.error("根据appName获取Namespace失败:", e);
            return null;
        }
    }

    public K8sResource.Namespace create(EnvEnum envEnum, String appName) {
        ApiClient apiClient = k8SClientAdmin.getEnv(envEnum);
        CoreV1Api coreV1Api = new CoreV1Api(apiClient);
        V1Namespace newNamespace = new V1Namespace();
        newNamespace.setApiVersion("v1");
        newNamespace.setKind("Namespace");
        V1ObjectMeta metadata = new V1ObjectMeta();
        metadata.setName(appName);
        newNamespace.setMetadata(metadata);
        try {
            V1Namespace v1Namespace = coreV1Api.createNamespace(newNamespace, "false", null, null);
            K8sResource.Namespace simpleNamespace = new K8sResource.Namespace();
            simpleNamespace.setSpec(v1Namespace.getSpec());
            simpleNamespace.setKind(v1Namespace.getKind());
            simpleNamespace.setMetadata(v1Namespace.getMetadata());
            simpleNamespace.setStatus(v1Namespace.getStatus());
            return simpleNamespace;
        } catch (ApiException e) {
            String responseBody = e.getResponseBody();
            if (responseBody.contains("AlreadyExists")) {
                log.warn("Namespace=" + appName + "已存在, 返回已有的Namespace");
                return this.getByAppName(envEnum, appName);
            }
            log.error("创建Namespace失败: {}", responseBody, e);
            return null;
        } catch (Exception e) {
            log.error("创建Namespace失败:", e);
            return null;
        }
    }
}
