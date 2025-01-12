package me.zhengjie.context;

import io.kubernetes.client.openapi.ApiClient;
import lombok.extern.slf4j.Slf4j;
import me.zhengjie.constant.EnvEnum;
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
//    @Autowired
//    private K8sConfigHelper k8sConfigHelper;
    @Autowired
    private K8sHealthChecker k8sHealthChecker;


//        List<ContainerdClusterConfig> list = containerdClusterConfigService.list();
//        for (ContainerdClusterConfig containerdClusterConfig : list) {
//            String envCode = containerdClusterConfig.getEnvCode();
//            EnvEnum envEnum = EnvEnum.getByCode(envCode);
//            if (envEnum == null) {
//                continue;
//            }
//            ApiClient apiClient = this.getEnv(envEnum);
//            try {
//                k8sHealthAdmin.checkConfigContent(apiClient);
//                containerdClusterConfigService.modifyStatus(containerdClusterConfig.getId(), ContainerdClusterConfigEnvStatusEnum.HEALTH);
//            } catch (Exception e) {
//                containerdClusterConfigService.modifyStatus(containerdClusterConfig.getId(), ContainerdClusterConfigEnvStatusEnum.UN_HEALTH);
//                log.error("{} K8s服务端不健康", envEnum.getCode(), e);
//            }
//        }

    public void remove(EnvEnum envEnum) {
        CLIENT_MAP.remove(envEnum.getCode());
    }

    public void putEnv(EnvEnum envEnum, ApiClient apiClient) {
        if (envEnum == null) {
            throw new BadRequestException("参数envEnum必填");
        }
        try {
            k8sHealthChecker.checkConfigContent(apiClient);
            CLIENT_MAP.put(envEnum.getCode(), apiClient);
            log.info("{}环境config配置检测成功，并放入应用缓存中", envEnum.getDesc());
        } catch (Exception e) {
            log.error("{}环境config配置检测失败", envEnum.getDesc());
        }
    }

    public ApiClient getEnv(EnvEnum envEnum) {
        if (envEnum == null) {
            throw new BadRequestException("参数envEnum必填");
        }
        ApiClient apiClient = CLIENT_MAP.getOrDefault(envEnum.getCode(), null);
        if (apiClient == null) {
            throw new BadRequestException("没有找到 " + envEnum.getDesc() + " 配置");
        }
        return apiClient;
    }
}
