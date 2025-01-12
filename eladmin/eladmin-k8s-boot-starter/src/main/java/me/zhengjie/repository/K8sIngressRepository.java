package me.zhengjie.repository;

import com.alibaba.fastjson.JSON;
import io.kubernetes.client.custom.IntOrString;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.ExtensionsV1beta1Api;
import io.kubernetes.client.openapi.apis.NetworkingV1Api;
import io.kubernetes.client.openapi.models.*;
import io.kubernetes.client.util.Yaml;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.zhengjie.constant.EnvEnum;
import me.zhengjie.context.K8sClientAdmin;
import me.zhengjie.infra.exception.BadRequestException;
import me.zhengjie.model.K8sResource;
import me.zhengjie.util.K8sResourceTool;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Map;

/**
 * Kubernetes Ingress Repository
 *
 * @author odboy
 * @date 2024-09-26
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class K8sIngressRepository {
    private final K8sClientAdmin k8SClientAdmin;

    /**
     * 创建k8s V1Ingress
     *
     * @param namespace   命名空间
     * @param appName     应用名称
     * @param annotations ingress注解
     * @param path        匹配的路径
     * @param hostname    绑定的域名
     * @param serviceName 路由到的服务名称
     * @param servicePort 路由到的服务端口
     * @return /
     */
    public V1Ingress create(@NotNull EnvEnum envEnum, @NotNull String namespace, @NotNull String appName, Map<String, String> annotations,
                            @NotNull String path, String hostname, @NotNull String serviceName,
                            @NotNull Integer servicePort) {
        String ingressName = K8sResourceTool.getIngressName(appName);
        // 构建ingress的yaml对象
        V1Ingress ingress = new V1IngressBuilder()
                .withNewMetadata()
                .withName(ingressName)
                .withNewNamespace(namespace)
                .withAnnotations(annotations)
                .endMetadata()
                .withNewSpec()
                .addNewRule()
                .withHost(hostname)
                .withHttp(new V1HTTPIngressRuleValueBuilder().addToPaths(new V1HTTPIngressPathBuilder()
                        .withPath(path)
                        .withPathType("ImplementationSpecific")
                        .withBackend(new V1IngressBackendBuilder()
                                .withService(new V1IngressServiceBackendBuilder()
                                        .withName(serviceName)
                                        .withPort(new V1ServiceBackendPortBuilder()
                                                .withNumber(servicePort).build()).build()).build()).build()).build())
                .endRule()
                .endSpec()
                .build();
        ApiClient apiClient = k8SClientAdmin.getEnv(envEnum);
        NetworkingV1Api networkingV1Api = new NetworkingV1Api(apiClient);
        try {
            return networkingV1Api.createNamespacedIngress(namespace, ingress, null, null, null);
        } catch (ApiException e) {
            String responseBody = e.getResponseBody();
            log.error("创建Ingress失败: {}", responseBody, e);
            K8sResource.ActionExceptionBody actionExceptionBody = JSON.parseObject(responseBody, K8sResource.ActionExceptionBody.class);
            if (actionExceptionBody != null) {
                throw new BadRequestException("创建Ingress失败, 原因：" + actionExceptionBody.getReason());
            }
            throw new BadRequestException("创建Ingress失败");
        } catch (Exception e) {
            log.error("创建Ingress失败:", e);
            throw new BadRequestException("创建Ingress失败");
        }
    }

    /**
     * 创建k8s ExtensionIngress
     *
     * @param namespace   命名空间
     * @param appName     应用名称
     * @param annotations ingress注解
     * @param path        匹配的路径
     * @param serviceName 路由到的服务名称
     * @param servicePort 路由到的服务端口
     * @return /
     */
    public ExtensionsV1beta1Ingress createExtension(@NotNull EnvEnum envEnum, @NotNull String namespace, @NotNull String appName,
                                                    Map<String, String> annotations, @NotNull String path,
                                                    @NotNull String serviceName, @NotNull Integer servicePort) {
        String ingressName = "inge-" + appName;
        // 构建ingress的yaml对象
        ExtensionsV1beta1Ingress ingress = new ExtensionsV1beta1IngressBuilder()
                .withNewMetadata()
                .withName(ingressName)
                .withNamespace(namespace)
                .withAnnotations(annotations)
                .endMetadata()
                .withNewSpec()
                .addNewRule()
                .withHttp(new ExtensionsV1beta1HTTPIngressRuleValueBuilder().addToPaths(new ExtensionsV1beta1HTTPIngressPathBuilder()
                        .withPath(path)
                        .withBackend(new ExtensionsV1beta1IngressBackendBuilder()
                                .withServiceName(serviceName)
                                .withServicePort(new IntOrString(servicePort)).build()).build()).build())
                .endRule()
                .endSpec()
                .build();
        ApiClient apiClient = k8SClientAdmin.getEnv(envEnum);
        ExtensionsV1beta1Api networkingV1Api = new ExtensionsV1beta1Api(apiClient);
        try {
            return networkingV1Api.createNamespacedIngress(namespace, ingress, null, null, null);
        } catch (ApiException e) {
            String responseBody = e.getResponseBody();
            log.error("创建Ingress失败: {}", responseBody, e);
            K8sResource.ActionExceptionBody actionExceptionBody = JSON.parseObject(responseBody, K8sResource.ActionExceptionBody.class);
            if (actionExceptionBody != null) {
                throw new BadRequestException("创建Ingress失败, 原因：" + actionExceptionBody.getReason());
            }
            throw new BadRequestException("创建Ingress失败");
        } catch (Exception e) {
            log.error("创建Ingress失败:", e);
            throw new BadRequestException("创建Ingress失败");
        }
    }

    /**
     * 删除Ingress
     *
     * @param namespace 命名空间
     * @param appName   应用名称
     */
    public void remove(@NotNull EnvEnum envEnum, @NotNull String namespace, @NotNull String appName) {
        ApiClient apiClient = k8SClientAdmin.getEnv(envEnum);
        NetworkingV1Api networkingV1Api = new NetworkingV1Api(apiClient);
        try {
            networkingV1Api.deleteNamespacedIngress(K8sResourceTool.getIngressName(appName), namespace, null, null, null, null, null, null);
        } catch (ApiException e) {
            String responseBody = e.getResponseBody();
            log.error("删除Ingress失败: {}", responseBody, e);
            K8sResource.ActionExceptionBody actionExceptionBody = JSON.parseObject(responseBody, K8sResource.ActionExceptionBody.class);
            if (actionExceptionBody != null) {
                throw new BadRequestException("删除Ingress失败, 原因：" + actionExceptionBody.getReason());
            }
            throw new BadRequestException("删除Ingress失败");
        } catch (Exception e) {
            log.error("删除Ingress失败:", e);
            throw new BadRequestException("删除Ingress失败");
        }
    }

    public V1Ingress loadFromYml(@NotNull EnvEnum envEnum, @NotNull String namespace, @NotBlank String ymlContent) {
        try {
            V1Ingress ingress = Yaml.loadAs(ymlContent, V1Ingress.class);
            ApiClient apiClient = k8SClientAdmin.getEnv(envEnum);
            NetworkingV1Api networkingV1Api = new NetworkingV1Api(apiClient);
            String ingressName = ingress.getMetadata().getName();
            // 执行验收测试
            networkingV1Api.replaceNamespacedIngress(ingressName, namespace, ingress, "false", "All", null);
            return ingress;
        } catch (ApiException e) {
            String responseBody = e.getResponseBody();
            log.error("从yml加载Ingress失败: {}", responseBody, e);
            K8sResource.ActionExceptionBody actionExceptionBody = JSON.parseObject(responseBody, K8sResource.ActionExceptionBody.class);
            if (actionExceptionBody != null) {
                throw new BadRequestException("从yml加载Ingress失败, 原因：" + actionExceptionBody.getReason());
            }
            throw new BadRequestException("从yml加载Ingress失败");
        } catch (Exception e) {
            log.error("从yml加载Ingress失败:", e);
            throw new BadRequestException("从yml加载Ingress失败");
        }
    }
}
