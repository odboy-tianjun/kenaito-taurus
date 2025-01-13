package me.zhengjie.repository;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1Namespace;
import io.kubernetes.client.openapi.models.V1NamespaceList;
import io.kubernetes.client.openapi.models.V1ObjectMeta;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.zhengjie.context.K8sClientAdmin;
import me.zhengjie.infra.exception.BadRequestException;
import me.zhengjie.model.K8sNamespace;
import me.zhengjie.model.K8sResource;
import me.zhengjie.util.K8sDryRunUtil;
import me.zhengjie.util.ValidationUtil;
import org.springframework.stereotype.Component;

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
    public List<K8sResource.Namespace> listNamespaces(String clusterCode) {
        if (StrUtil.isEmpty(clusterCode)) {
            throw new BadRequestException("参数clusterCode不能为空");
        }
        try {
            ApiClient apiClient = k8SClientAdmin.getEnv(clusterCode);
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
            K8sResource.ActionExceptionBody actionExceptionBody = JSON.parseObject(responseBody, K8sResource.ActionExceptionBody.class);
            if (actionExceptionBody != null) {
                throw new BadRequestException("获取Namespace列表失败, 原因：" + actionExceptionBody.getReason());
            }
            throw new BadRequestException("获取Namespace列表失败");
        } catch (Exception e) {
            log.error("获取Namespace列表失败:", e);
            throw new BadRequestException("获取Namespace列表失败");
        }
    }

    /**
     * 根据appName获取Namespace
     *
     * @return /
     */
    public K8sResource.Namespace getNamespaceByAppName(String clusterCode, String appName) {
        if (StrUtil.isEmpty(clusterCode)) {
            throw new BadRequestException("集群编码不能为空");
        }
        if (StrUtil.isEmpty(appName)) {
            throw new BadRequestException("应用编码不能为空");
        }
        try {
            ApiClient apiClient = k8SClientAdmin.getEnv(clusterCode);
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
            log.error("根据appName获取Namespace: {}", responseBody, e);
            K8sResource.ActionExceptionBody actionExceptionBody = JSON.parseObject(responseBody, K8sResource.ActionExceptionBody.class);
            if (actionExceptionBody != null) {
                throw new BadRequestException("根据appName获取Namespace, 原因：" + actionExceptionBody.getReason());
            }
            throw new BadRequestException("根据appName获取Namespace");
        } catch (Exception e) {
            log.error("根据appName获取Namespace:", e);
            throw new BadRequestException("根据appName获取Namespace");
        }
    }

    /**
     * 创建Namespace
     *
     * @param args /
     */
    public K8sResource.Namespace createNamespace(K8sNamespace.CreateArgs args) {
        try {
            ValidationUtil.validate(args);
            ApiClient apiClient = k8SClientAdmin.getEnv(args.getClusterCode());
            CoreV1Api coreV1Api = new CoreV1Api(apiClient);
            V1Namespace newNamespace = new V1Namespace();
            newNamespace.setApiVersion("v1");
            newNamespace.setKind("Namespace");
            V1ObjectMeta metadata = new V1ObjectMeta();
            metadata.setName(args.getAppName());
            newNamespace.setMetadata(metadata);
            V1Namespace v1Namespace = coreV1Api.createNamespace(newNamespace, "false", K8sDryRunUtil.transferState(args.getDryRun()), null);
            K8sResource.Namespace simpleNamespace = new K8sResource.Namespace();
            simpleNamespace.setSpec(v1Namespace.getSpec());
            simpleNamespace.setKind(v1Namespace.getKind());
            simpleNamespace.setMetadata(v1Namespace.getMetadata());
            simpleNamespace.setStatus(v1Namespace.getStatus());
            return simpleNamespace;
        } catch (ApiException e) {
            String responseBody = e.getResponseBody();
            log.error("创建namespace失败: {}", responseBody, e);
            K8sResource.ActionExceptionBody actionExceptionBody = JSON.parseObject(responseBody, K8sResource.ActionExceptionBody.class);
            if (actionExceptionBody != null) {
                throw new BadRequestException("创建namespace失败, 原因：" + actionExceptionBody.getReason());
            }
            throw new BadRequestException("创建namespace失败");
        } catch (Exception e) {
            log.error("创建namespace失败:", e);
            throw new BadRequestException("创建namespace失败");
        }
    }
}
