/*
 *  Copyright 2021-2025 Tian Jun
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package cn.odboy.infra.k8s;

import cn.odboy.constant.K8sClusterStatusEnum;
import cn.odboy.context.K8sClientAdmin;
import cn.odboy.context.K8sConfigHelper;
import cn.odboy.context.K8sProperties;
import cn.odboy.context.K8sResourceListenerAdmin;
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
    @Autowired
    private K8sProperties properties;
    @Autowired
    private K8sResourceListenerAdmin k8SResourceListenerAdmin;

    private void updateStatusById(K8sClusterConfig k8sClusterConfig, K8sClusterStatusEnum k8sClusterStatusEnum) {
        K8sClusterConfig updRecord = new K8sClusterConfig();
        updRecord.setId(k8sClusterConfig.getId());
        updRecord.setStatus(k8sClusterStatusEnum.getCode());
        k8sClusterConfigService.updateById(updRecord);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (properties.getEnable() == null || !properties.getEnable()) {
            log.info("================== 未启用K8s功能 ==================");
            return;
        }
        List<K8sClusterConfig> list = k8sClusterConfigService.list();
        for (K8sClusterConfig k8sClusterConfig : list) {
            try {
                String content = new String(k8sClusterConfig.getConfigContent(), StandardCharsets.UTF_8);
                ApiClient apiClient = k8sConfigHelper.loadFormContent(content);
                k8sClientAdmin.putClientEnv(k8sClusterConfig.getClusterCode(), k8sClusterConfig.getEnvCode(), apiClient);
                updateStatusById(k8sClusterConfig, K8sClusterStatusEnum.HEALTH);
                log.info("K8s集群 {} 服务端健康，已加入k8sClientAdmin", k8sClusterConfig.getClusterName());
                k8SResourceListenerAdmin.addListener(k8sClusterConfig.getClusterCode(), content);
                log.info("K8s集群 {} 服务端健康，已开启资源变更监听", k8sClusterConfig.getClusterName());
            } catch (Exception e) {
                log.error("K8s集群 {} 服务端不健康", k8sClusterConfig.getClusterName(), e);
                updateStatusById(k8sClusterConfig, K8sClusterStatusEnum.UN_HEALTH);
            }
        }

    }
}
