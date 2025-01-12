package me.zhengjie.repository;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson.JSON;
import io.kubernetes.client.custom.IntOrString;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1Service;
import io.kubernetes.client.openapi.models.V1ServiceBuilder;
import io.kubernetes.client.openapi.models.V1ServicePort;
import io.kubernetes.client.util.Yaml;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.zhengjie.constant.AppLabelEnum;
import me.zhengjie.constant.EnvEnum;
import me.zhengjie.context.K8sClientAdmin;
import me.zhengjie.infra.exception.BadRequestException;
import me.zhengjie.model.K8sResource;
import me.zhengjie.util.K8sResourceTool;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Kubernetes Service Repository
 *
 * @author odboy
 * @date 2024-09-26
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class K8sSvcRepository {
    private final K8sClientAdmin k8SClientAdmin;

    /**
     * 创建Service
     *
     * @param namespace     命名空间
     * @param appName       应用名称
     * @param annotations   service注解
     * @param port          外部访问的端口号
     * @param targetPort    应用服务端口号
     * @param labelSelector pod标签选择器
     */
    public V1Service create(@NotNull EnvEnum envEnum, @NotNull String namespace, @NotNull String appName, Map<String, String> annotations,
                            @NotNull Integer port, @NotNull Integer targetPort, Map<String, String> labelSelector) {
        String serviceName = K8sResourceTool.getServiceName(appName);

        if (CollUtil.isEmpty(labelSelector)) {
            labelSelector = new HashMap<>(1);
        }
        labelSelector.put(AppLabelEnum.AppName.getCode(), appName);

        // 构建service的yaml对象
        V1Service service = new V1ServiceBuilder()
                .withNewMetadata()
                .withName(serviceName)
                .withNamespace(namespace)
                .withAnnotations(annotations)
                .endMetadata()
                .withNewSpec()
                .withPorts(new V1ServicePort().protocol("TCP").port(port).targetPort(new IntOrString(targetPort)))
                .withSelector(labelSelector)
                .endSpec()
                .build();
        ApiClient apiClient = k8SClientAdmin.getEnv(envEnum);
        // Deployment and StatefulSet is defined in apps/v1, so you should use AppsV1Api instead of CoreV1API
        CoreV1Api api = new CoreV1Api(apiClient);
        try {
            return api.createNamespacedService(namespace, service, null, null, null);
        } catch (ApiException e) {
            String responseBody = e.getResponseBody();
            log.error("创建Service失败: {}", responseBody, e);
            K8sResource.ActionExceptionBody actionExceptionBody = JSON.parseObject(responseBody, K8sResource.ActionExceptionBody.class);
            if (actionExceptionBody != null) {
                throw new BadRequestException("创建Service失败, 原因：" + actionExceptionBody.getReason());
            }
            throw new BadRequestException("创建Service失败");
        } catch (Exception e) {
            log.error("创建Service失败:", e);
            throw new BadRequestException("创建Service失败");
        }
    }

    /**
     * 删除Service
     *
     * @param namespace 命名空间
     * @param appName   应用名称
     */
    public void remove(@NotNull EnvEnum envEnum, @NotNull String namespace, @NotNull String appName) {
        ApiClient apiClient = k8SClientAdmin.getEnv(envEnum);
        CoreV1Api api = new CoreV1Api(apiClient);
        try {
            api.deleteNamespacedService(K8sResourceTool.getServiceName(appName), namespace, null, null, null, null, null, null);
        } catch (ApiException e) {
            String responseBody = e.getResponseBody();
            log.error("删除Service失败: {}", responseBody, e);
            K8sResource.ActionExceptionBody actionExceptionBody = JSON.parseObject(responseBody, K8sResource.ActionExceptionBody.class);
            if (actionExceptionBody != null) {
                throw new BadRequestException("删除Service失败, 原因：" + actionExceptionBody.getReason());
            }
            throw new BadRequestException("删除Service失败");
        } catch (Exception e) {
            log.error("删除Service失败:", e);
            throw new BadRequestException("删除Service失败");
        }
    }

    public V1Service loadFromYml(@NotNull EnvEnum envEnum, @NotNull String namespace, @NotBlank String ymlContent) {
        try {
            V1Service service = Yaml.loadAs(ymlContent, V1Service.class);
            ApiClient apiClient = k8SClientAdmin.getEnv(envEnum);
            CoreV1Api coreV1Api = new CoreV1Api(apiClient);
            String serviceName = Objects.requireNonNull(service.getMetadata()).getName();
            // 执行验收测试, dryRun="All"
            coreV1Api.replaceNamespacedService(serviceName, namespace, service, "false", null, null);
            return service;
        } catch (ApiException e) {
            String responseBody = e.getResponseBody();
            log.error("从yml加载Service失败: {}", responseBody, e);
            K8sResource.ActionExceptionBody actionExceptionBody = JSON.parseObject(responseBody, K8sResource.ActionExceptionBody.class);
            if (actionExceptionBody != null) {
                throw new BadRequestException("从yml加载Service失败, 原因：" + actionExceptionBody.getReason());
            }
            throw new BadRequestException("从yml加载Service失败");
        } catch (Exception e) {
            log.error("从yml加载Service失败:", e);
            throw new BadRequestException("从yml加载Service失败");
        }
    }
}
