package me.zhengjie.repository;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1NodeList;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.zhengjie.context.K8sClientAdmin;
import me.zhengjie.infra.exception.BadRequestException;
import me.zhengjie.model.K8sResource;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class K8sNodeRepository {
    private final K8sClientAdmin k8SClientAdmin;

    /**
     * 获取节点列表
     *
     * @return /
     */
    public V1NodeList listNode(String clusterCode) {
        if (StrUtil.isEmpty(clusterCode)) {
            throw new BadRequestException("集群编码不能为空");
        }
        try {
            ApiClient apiClient = k8SClientAdmin.getEnv(clusterCode);
            CoreV1Api coreV1Api = new CoreV1Api(apiClient);
            return coreV1Api.listNode("false", null, null, null, null, null, null, null, null, null);
        } catch (ApiException e) {
            String responseBody = e.getResponseBody();
            log.error("获取节点列表失败: {}", responseBody, e);
            K8sResource.ActionExceptionBody actionExceptionBody = JSON.parseObject(responseBody, K8sResource.ActionExceptionBody.class);
            if (actionExceptionBody != null) {
                throw new BadRequestException("获取节点列表失败, 原因：" + actionExceptionBody.getReason());
            }
            throw new BadRequestException("获取节点列表失败");
        } catch (Exception e) {
            log.error("获取节点列表失败:", e);
            throw new BadRequestException("获取节点列表失败");
        }
    }
}
