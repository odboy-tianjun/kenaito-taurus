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
package cn.odboy.context;

import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.Configuration;
import io.kubernetes.client.util.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * k8s 配置加载
 *
 * @author odboy
 * @date 2025-01-12
 */
public class K8sConfigHelper {
    private static final Logger log = LoggerFactory.getLogger(K8sConfigHelper.class);
    @Value("${k8s.debug:false}")
    private Boolean debug;
    @Autowired
    private ResourceLoader resourceLoader;

    /**
     * 从系统配置 $HOME/.kube/config 读取配置文件连接 Kubernetes 集群
     *
     * @return /
     */
    public ApiClient loadFormDefault() {
        try {
            ApiClient apiClient = Config.defaultClient();
            Configuration.setDefaultApiClient(apiClient);
            apiClient.setDebugging(debug);
            return apiClient;
        } catch (IOException e) {
            log.error("从系统配置 $HOME/.kube/config 读取配置文件连接 Kubernetes 集群失败", e);
            throw new RuntimeException("从系统配置 $HOME/.kube/config 读取配置文件连接 Kubernetes 集群失败");
        }
    }

    /**
     * 从指定文件读取配置文件连接 Kubernetes 集群
     *
     * @param filePath 配置文件所在绝对路径
     * @return /
     */
    public ApiClient loadFormLocalPath(String filePath) {
        try {
            ApiClient apiClient = Config.fromConfig(filePath);
            Configuration.setDefaultApiClient(apiClient);
            apiClient.setDebugging(debug);
            return apiClient;
        } catch (IOException e) {
            log.error("从指定文件读取配置文件连接 Kubernetes 集群失败, 参数filePath={}", filePath, e);
            throw new RuntimeException("从指定文件读取配置文件连接 Kubernetes 集群失败");
        }
    }

    /**
     * 从指定内容连接 Kubernetes 集群
     *
     * @param content 连接集群配置信息
     * @return /
     */
    public ApiClient loadFormContent(String content) {
        try {
            ApiClient apiClient = Config.fromConfig(new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8)));
            apiClient.setDebugging(debug);
            return apiClient;
        } catch (IOException e) {
            log.error("从指定内容连接 Kubernetes 集群失败, 参数content={}", content, e);
            throw new RuntimeException("从指定内容连接 Kubernetes 集群失败");
        }
    }

    /**
     * 从Resources文件夹读取配置文件连接 Kubernetes 集群
     *
     * @param relativeFileName 相对文件路径名, 比如：resources/config/ingress_demo.txt
     * @return /
     */
    public ApiClient loadFormResourceFile(String relativeFileName) {
        try {
            Resource resource = resourceLoader.getResource("classpath:" + relativeFileName);
            ApiClient apiClient = Config.fromConfig(resource.getInputStream());
            apiClient.setDebugging(true);
            return apiClient;
        } catch (IOException e) {
            log.error("从Resources文件夹读取配置文件连接 Kubernetes 集群失败", e);
            throw new RuntimeException("从Resources文件夹读取配置文件连接 Kubernetes 集群失败");
        }
    }
}
