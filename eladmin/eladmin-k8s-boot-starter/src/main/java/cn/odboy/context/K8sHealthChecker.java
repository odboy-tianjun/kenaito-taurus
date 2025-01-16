/*
 *  Copyright 2022-2025 Tian Jun
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
