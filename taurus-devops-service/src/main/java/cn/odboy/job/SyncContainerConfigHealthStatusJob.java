package cn.odboy.job;

import cn.odboy.constant.ContainerdClusterConfigEnvStatusEnum;
import cn.odboy.context.K8sConfigHelper;
import cn.odboy.context.K8sHealthChecker;
import cn.odboy.domain.ContainerdClusterConfig;
import cn.odboy.service.ContainerdClusterConfigService;
import cn.odboy.common.util.CollUtil;
import io.kubernetes.client.openapi.ApiClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 同步K8s集群配置健康状态
 *
 * @author odboy
 * @date 2024-11-18
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SyncContainerConfigHealthStatusJob {
    private final K8sHealthChecker k8sHealthChecker;
    private final K8sConfigHelper k8sConfigHelper;
    private final ContainerdClusterConfigService containerdClusterConfigService;

    public void run() {
        List<ContainerdClusterConfig> list = containerdClusterConfigService.list();
        if (CollUtil.isEmpty(list)) {
            log.info("没有找到配置信息");
            return;
        }
        for (ContainerdClusterConfig containerdClusterConfig : list) {
            ContainerdClusterConfig updRecord = new ContainerdClusterConfig();
            updRecord.setId(containerdClusterConfig.getId());
            try {
                ApiClient apiClient = k8sConfigHelper.loadFormContent(containerdClusterConfig.getConfigContent());
                k8sHealthChecker.checkConfigContent(apiClient);
                updRecord.setEnvStatus(ContainerdClusterConfigEnvStatusEnum.HEALTH.getCode());
                log.info("{} K8s服务端健康", containerdClusterConfig.getEnvCode());
            } catch (Exception e) {
                updRecord.setEnvStatus(ContainerdClusterConfigEnvStatusEnum.UN_HEALTH.getCode());
                log.error("{} K8s服务端不健康", containerdClusterConfig.getEnvCode(), e);
            }
            containerdClusterConfigService.updateById(updRecord);
        }
    }
}
