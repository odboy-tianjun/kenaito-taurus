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
import me.zhengjie.context.K8sClientAdmin;
import me.zhengjie.infra.exception.BadRequestException;
import me.zhengjie.model.K8sIngress;
import me.zhengjie.model.K8sResource;
import me.zhengjie.util.K8sDryRunUtil;
import me.zhengjie.util.K8sNameUtil;
import me.zhengjie.util.ValidationUtil;
import org.springframework.stereotype.Component;

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
     * @param args /
     */
    public V1Ingress createIngress(K8sIngress.CreateArgs args) {
        try {
            ValidationUtil.validate(args);
            String ingressName = K8sNameUtil.getIngressName(args.getAppName());
            // 构建ingress的yaml对象
            V1Ingress ingress = new V1IngressBuilder()
                    .withNewMetadata()
                    .withName(ingressName)
                    .withNewNamespace(args.getNamespace())
                    .withAnnotations(args.getAnnotations())
                    .endMetadata()
                    .withNewSpec()
                    .addNewRule()
                    .withHost(args.getHostname())
                    .withHttp(new V1HTTPIngressRuleValueBuilder().addToPaths(new V1HTTPIngressPathBuilder()
                            .withPath(args.getPath())
                            .withPathType("ImplementationSpecific")
                            .withBackend(new V1IngressBackendBuilder()
                                    .withService(new V1IngressServiceBackendBuilder()
                                            .withName(args.getServiceName())
                                            .withPort(new V1ServiceBackendPortBuilder()
                                                    .withNumber(args.getServicePort()).build()).build()).build()).build()).build())
                    .endRule()
                    .endSpec()
                    .build();
            ApiClient apiClient = k8SClientAdmin.getClientEnv(args.getClusterCode());
            NetworkingV1Api networkingV1Api = new NetworkingV1Api(apiClient);
            return networkingV1Api.createNamespacedIngress(args.getNamespace(), ingress, "false", K8sDryRunUtil.transferState(args.getDryRun()), null);
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
     * @param args /
     */
    public ExtensionsV1beta1Ingress createIngressExtension(K8sIngress.CreateArgs args) {
        try {
            ValidationUtil.validate(args);
            String ingressName = "inge-" + args.getAppName();
            // 构建ingress的yaml对象
            ExtensionsV1beta1Ingress ingress = new ExtensionsV1beta1IngressBuilder()
                    .withNewMetadata()
                    .withName(ingressName)
                    .withNamespace(args.getNamespace())
                    .withAnnotations(args.getAnnotations())
                    .endMetadata()
                    .withNewSpec()
                    .addNewRule()
                    .withHost(args.getHostname())
                    .withHttp(new ExtensionsV1beta1HTTPIngressRuleValueBuilder().addToPaths(new ExtensionsV1beta1HTTPIngressPathBuilder()
                            .withPath(args.getPath())
                            .withBackend(new ExtensionsV1beta1IngressBackendBuilder()
                                    .withServiceName(args.getServiceName())
                                    .withServicePort(new IntOrString(args.getServicePort())).build()).build()).build())
                    .endRule()
                    .endSpec()
                    .build();
            ApiClient apiClient = k8SClientAdmin.getClientEnv(args.getClusterCode());
            ExtensionsV1beta1Api networkingV1Api = new ExtensionsV1beta1Api(apiClient);
            return networkingV1Api.createNamespacedIngress(args.getNamespace(), ingress, "false", K8sDryRunUtil.transferState(args.getDryRun()), null);
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
     * @param args /
     */
    public void deleteIngress(K8sIngress.DeleteArgs args) {
        try {
            ValidationUtil.validate(args);
            ApiClient apiClient = k8SClientAdmin.getClientEnv(args.getClusterCode());
            NetworkingV1Api networkingV1Api = new NetworkingV1Api(apiClient);
            networkingV1Api.deleteNamespacedIngress(K8sNameUtil.getIngressName(args.getAppName()), args.getNamespace(), "false", K8sDryRunUtil.transferState(args.getDryRun()), null, null, null, null);
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

    /**
     * 从yaml文件内容加载Ingress
     *
     * @param args /
     */
    public V1Ingress loadIngressFromYaml(K8sIngress.LoadFromYamlArgs args) {
        try {
            ValidationUtil.validate(args);
            V1Ingress ingress = Yaml.loadAs(args.getYamlContent(), V1Ingress.class);
            ApiClient apiClient = k8SClientAdmin.getClientEnv(args.getClusterCode());
            NetworkingV1Api networkingV1Api = new NetworkingV1Api(apiClient);
            String ingressName = ingress.getMetadata().getName();
            networkingV1Api.replaceNamespacedIngress(ingressName, args.getNamespace(), ingress, "false", K8sDryRunUtil.transferState(args.getDryRun()), null);
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
