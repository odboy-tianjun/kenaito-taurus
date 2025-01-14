package cn.odboy.context;

import cn.odboy.repository.K8sTestRepository;
import io.kubernetes.client.openapi.ApiClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * k8s健康检查，检查配置文件内容
 *
 * @author odboy
 * @date 2025-01-13
 */
@Slf4j
@Component
public class K8sHealthChecker {
    @Autowired
    private K8sTestRepository k8sTestRepository;

    public void checkConfigContent(ApiClient apiClient) {
        k8sTestRepository.listNamespaces(apiClient);
    }
}
