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
    @Autowired
    private K8sHealthChecker k8sHealthChecker;

    public void remove(String clusterCode) {
        CLIENT_MAP.remove(clusterCode);
    }

    public void putEnv(String clusterCode, ApiClient apiClient) {
        if (StrUtil.isBlank(clusterCode)) {
            throw new BadRequestException("参数clusterCode必填");
        }
        try {
            k8sHealthChecker.checkConfigContent(apiClient);
            CLIENT_MAP.put(clusterCode, apiClient);
            log.info("{}集群config配置检测成功，并放入应用缓存中", clusterCode);
        } catch (Exception e) {
            log.error("{}集群config配置检测失败", clusterCode);
        }
    }

    public ApiClient getEnv(String clusterCode) {
        if (StrUtil.isBlank(clusterCode)) {
            throw new BadRequestException("参数clusterCode必填");
        }
        ApiClient apiClient = CLIENT_MAP.getOrDefault(clusterCode, null);
        if (apiClient == null) {
            throw new BadRequestException(String.format("没有找到集群 %s 配置", clusterCode));
        }
        return apiClient;
    }
}
