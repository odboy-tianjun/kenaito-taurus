package me.zhengjie.repository;

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
import me.zhengjie.constant.K8sAppLabelEnum;
import me.zhengjie.context.K8sClientAdmin;
import me.zhengjie.infra.exception.BadRequestException;
import me.zhengjie.model.K8sResource;
import me.zhengjie.model.K8sService;
import me.zhengjie.util.K8sDryRunUtil;
import me.zhengjie.util.K8sNameUtil;
import me.zhengjie.util.ValidationUtil;
import org.springframework.stereotype.Component;

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
public class K8sServiceRepository {
    private final K8sClientAdmin k8SClientAdmin;

    /**
     * 创建Service
     *
     * @param args /
     */
    public V1Service createService(K8sService.CreateArgs args) {
        try {
            ValidationUtil.validate(args);
            String serviceName = K8sNameUtil.getServiceName(args.getAppName());
            args.getLabelSelector().put(K8sAppLabelEnum.AppName.getCode(), args.getAppName());
            // 构建service的yaml对象
            V1Service service = new V1ServiceBuilder()
                    .withNewMetadata()
                    .withName(serviceName)
                    .withNamespace(args.getNamespace())
                    .withAnnotations(args.getAnnotations())
                    .endMetadata()
                    .withNewSpec()
                    .withPorts(new V1ServicePort().protocol("TCP").port(args.getPort()).targetPort(new IntOrString(args.getTargetPort())))
                    .withSelector(args.getLabelSelector())
                    .endSpec()
                    .build();
            ApiClient apiClient = k8SClientAdmin.getClientEnv(args.getClusterCode());
            // Deployment and StatefulSet is defined in apps/v1, so you should use AppsV1Api instead of CoreV1API
            CoreV1Api api = new CoreV1Api(apiClient);
            return api.createNamespacedService(args.getNamespace(), service, null, K8sDryRunUtil.transferState(args.getDryRun()), null);
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
     * @param args /
     */
    public void deleteService(K8sService.DeleteArgs args) {
        try {
            ValidationUtil.validate(args);
            ApiClient apiClient = k8SClientAdmin.getClientEnv(args.getClusterCode());
            CoreV1Api api = new CoreV1Api(apiClient);
            api.deleteNamespacedService(K8sNameUtil.getServiceName(args.getAppName()), args.getNamespace(), null, K8sDryRunUtil.transferState(args.getDryRun()), null, null, null, null);
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

    public V1Service loadServiceFromYaml(K8sService.LoadFromYamlArgs args) {
        try {
            V1Service service = Yaml.loadAs(args.getYamlContent(), V1Service.class);
            ApiClient apiClient = k8SClientAdmin.getClientEnv(args.getClusterCode());
            CoreV1Api coreV1Api = new CoreV1Api(apiClient);
            String serviceName = Objects.requireNonNull(service.getMetadata()).getName();
            // 执行验收测试, dryRun="All"
            coreV1Api.replaceNamespacedService(serviceName, args.getNamespace(), service, null, K8sDryRunUtil.transferState(args.getDryRun()), null);
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
