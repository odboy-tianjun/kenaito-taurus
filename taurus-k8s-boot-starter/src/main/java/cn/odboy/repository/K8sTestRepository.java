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
package cn.odboy.repository;

import cn.odboy.model.response.K8sResource;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1NamespaceList;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Kubernetes Namespace Test Repository
 *
 * @author odboy
 * @date 2024-11-20
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class K8sTestRepository {
    /**
     * 获取Namespace列表
     *
     * @return /
     */
    public List<K8sResource.Namespace> listNamespaces(ApiClient apiClient) {
        try {
            CoreV1Api coreV1Api = new CoreV1Api(apiClient);
            V1NamespaceList namespaceList = coreV1Api.listNamespace("false", true, null, null, null, 1, null, null, null, false);
            return namespaceList.getItems().stream().map(m -> {
                K8sResource.Namespace namespace = new K8sResource.Namespace();
                namespace.setSpec(m.getSpec());
                namespace.setKind(m.getKind());
                namespace.setMetadata(m.getMetadata());
                namespace.setStatus(m.getStatus());
                return namespace;
            }).collect(Collectors.toList());
        } catch (ApiException e) {
            String responseBody = e.getResponseBody();
            log.error("获取Namespace列表失败: {}", responseBody, e);
            return new ArrayList<>();
        } catch (Exception e) {
            log.error("获取Namespace列表失败:", e);
            return new ArrayList<>();
        }
    }
}
