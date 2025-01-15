package cn.odboy.infra.k8s;

import cn.hutool.core.thread.ThreadUtil;
import cn.odboy.constant.K8sClusterStatusEnum;
import cn.odboy.context.K8sClientAdmin;
import cn.odboy.context.K8sConfigHelper;
import cn.odboy.modules.devops.domain.K8sClusterConfig;
import cn.odboy.modules.devops.service.K8sClusterConfigService;
import io.kubernetes.client.openapi.ApiClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * k8s 集群配置加载器
 *
 * @author odboy
 * @date 2025-01-13
 */
@Slf4j
@Component
public class K8sClusterConfigLoader implements InitializingBean {
    @Autowired
    private K8sClusterConfigService k8sClusterConfigService;
    @Autowired
    private K8sClientAdmin k8sClientAdmin;
    @Autowired
    private K8sConfigHelper k8sConfigHelper;

    private void updateStatusById(K8sClusterConfig k8sClusterConfig, K8sClusterStatusEnum k8sClusterStatusEnum) {
        K8sClusterConfig updRecord = new K8sClusterConfig();
        updRecord.setId(k8sClusterConfig.getId());
        updRecord.setStatus(k8sClusterStatusEnum.getCode());
        k8sClusterConfigService.updateById(updRecord);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        ThreadUtil.execAsync(() -> {
            List<K8sClusterConfig> list = k8sClusterConfigService.list();
            for (K8sClusterConfig k8sClusterConfig : list) {
                try {
                    String content = new String(k8sClusterConfig.getConfigContent(), StandardCharsets.UTF_8);
                    ApiClient apiClient = k8sConfigHelper.loadFormContent(content);
                    k8sClientAdmin.putClientEnv(k8sClusterConfig.getClusterCode(), k8sClusterConfig.getEnvCode(), apiClient);
                    updateStatusById(k8sClusterConfig, K8sClusterStatusEnum.HEALTH);
                    log.info("K8s集群 {} 服务端健康，已加入k8sClientAdmin", k8sClusterConfig.getClusterName());
                } catch (Exception e) {
                    log.error("K8s集群 {} 服务端不健康", k8sClusterConfig.getClusterName(), e);
                    updateStatusById(k8sClusterConfig, K8sClusterStatusEnum.UN_HEALTH);
                }
            }
        });
    }
}
