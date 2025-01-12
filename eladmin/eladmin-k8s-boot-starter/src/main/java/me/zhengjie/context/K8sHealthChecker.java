package me.zhengjie.context;

import io.kubernetes.client.openapi.ApiClient;
import lombok.extern.slf4j.Slf4j;
import me.zhengjie.repository.K8sTestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class K8sHealthChecker {
    @Autowired
    private K8sTestRepository k8sTestRepository;
//    @Autowired
//    private K8sConfigHelper k8sConfigHelper;

    public void checkConfigContent(ApiClient apiClient) {
        k8sTestRepository.queryList(apiClient);
    }
//
//    public void checkConfigContent(String configContent) {
//        ApiClient apiClient = k8sConfigHelper.loadFormContent(configContent);
//        k8sTestRepository.queryList(apiClient);
//    }
}
