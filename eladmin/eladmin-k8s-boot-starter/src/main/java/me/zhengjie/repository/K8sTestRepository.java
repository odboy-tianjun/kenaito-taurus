package me.zhengjie.repository;

import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1NamespaceList;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.zhengjie.model.response.K8sResource;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Kubernetes Namespace Test Repository
 *
 * @author odboy
 * @date 2024-11-20
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class K8sTestRepository {
    /**
     * 获取Namespace列表
     *
     * @return /
     */
    public List<K8sResource.Namespace> listNamespaces(ApiClient apiClient) {
        try {
            CoreV1Api coreV1Api = new CoreV1Api(apiClient);
            V1NamespaceList namespaceList = coreV1Api.listNamespace("false", true, null, null, null, 1, null, null, null, false);
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
}
