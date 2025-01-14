package me.zhengjie.context;

import cn.hutool.core.util.StrUtil;
import io.kubernetes.client.openapi.ApiClient;
import lombok.extern.slf4j.Slf4j;
import me.zhengjie.infra.exception.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * TODO 待实现
 *
 * @author odboy
 * @date 2025-01-12
 */
@Slf4j
@Component
public class K8sClientAdmin {
    private static final Map<String, ApiClient> CLIENT_MAP = new HashMap<>(1);
    private static final Map<String, String> CLIENT_ENV = new HashMap<>(1);
    @Autowired
    private K8sHealthChecker k8sHealthChecker;

    public void deleteClient(String clusterCode) {
        CLIENT_MAP.remove(clusterCode);
        CLIENT_ENV.remove(clusterCode);
    }

    public void putClientEnv(String clusterCode, String envCode, ApiClient apiClient) {
        if (StrUtil.isBlank(clusterCode)) {
            throw new BadRequestException("参数clusterCode必填");
        }
        try {
            k8sHealthChecker.checkConfigContent(apiClient);
            CLIENT_MAP.put(clusterCode, apiClient);
            CLIENT_ENV.put(clusterCode, envCode);
            log.info("{}集群config配置检测成功，并放入应用缓存中", clusterCode);
        } catch (Exception e) {
            log.error("{}集群config配置检测失败", clusterCode);
        }
    }

    public ApiClient getClient(String clusterCode) {
        if (StrUtil.isBlank(clusterCode)) {
            throw new BadRequestException("参数clusterCode必填");
        }
        ApiClient apiClient = CLIENT_MAP.getOrDefault(clusterCode, null);
        if (apiClient == null) {
            throw new BadRequestException(String.format("没有找到集群 %s 配置", clusterCode));
        }
        return apiClient;
    }

    public String getEnvCode(String clusterCode) {
        if (StrUtil.isBlank(clusterCode)) {
            throw new BadRequestException("参数clusterCode必填");
        }
        return CLIENT_ENV.getOrDefault(clusterCode, null);
    }
}
