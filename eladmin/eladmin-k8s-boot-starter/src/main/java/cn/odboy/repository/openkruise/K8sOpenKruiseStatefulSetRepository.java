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
package cn.odboy.repository.openkruise;

import cn.hutool.core.lang.Assert;
import cn.odboy.constant.K8sActionReasonCodeEnum;
import cn.odboy.context.K8sClientAdmin;
import cn.odboy.infra.exception.BadRequestException;
import cn.odboy.model.openkruise.KruiseAppsV1alpha1StatefulSet;
import cn.odboy.model.request.K8sStatefulSet;
import cn.odboy.model.response.K8sResource;
import cn.odboy.repository.K8sPodRepository;
import cn.odboy.util.K8sDryRunUtil;
import cn.odboy.util.ValidationUtil;
import com.alibaba.fastjson.JSON;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.CustomObjectsApi;
import io.kubernetes.client.util.Yaml;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * Kubernetes StatefulSet Repository For OpenKruise
 *
 * @author odboy
 * @date 2024-09-26
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class K8sOpenKruiseStatefulSetRepository {
    private final K8sClientAdmin k8SClientAdmin;
    private final K8sPodRepository k8sPodRepository;

    /**
     * 根据name获取KruiseStatefulSet
     *
     * @return /
     */
    public KruiseAppsV1alpha1StatefulSet describeStatefulSetByName(String clusterCode, String name, String namespace) {
        Assert.notEmpty(clusterCode, "集群编码不能为空");
        Assert.notEmpty(name, "名称不能为空");
        Assert.notEmpty(namespace, "命名空间不能为空");
        try {
            CustomObjectsApi customObjectsApi = new CustomObjectsApi(k8SClientAdmin.getClient(clusterCode));
            Object obj = customObjectsApi.getNamespacedCustomObject(
                    "apps.kruise.io",
                    "v1beta1",
                    namespace,
                    "statefulsets",
                    name
            );
            return JSON.parseObject(JSON.toJSONString(obj), KruiseAppsV1alpha1StatefulSet.class);
        } catch (ApiException e) {
            String responseBody = e.getResponseBody();
            K8sResource.ActionExceptionBody actionExceptionBody = JSON.parseObject(responseBody, K8sResource.ActionExceptionBody.class);
            if (actionExceptionBody != null) {
                if (actionExceptionBody.getReason().contains(K8sActionReasonCodeEnum.NotFound.getCode())) {
                    return null;
                }
                log.error("根据name获取KruiseStatefulSet失败: {}", responseBody, e);
                throw new BadRequestException("根据name获取KruiseStatefulSet失败, 原因：" + actionExceptionBody.getReason());
            }
            throw new BadRequestException("根据name获取KruiseStatefulSet失败");
        } catch (Exception e) {
            log.error("根据name获取KruiseStatefulSet失败:", e);
            throw new BadRequestException("根据name获取KruiseStatefulSet失败");
        }
    }

    /**
     * 从yaml文件内容加载KruiseStatefulSet
     *
     * @param args /
     */
    public KruiseAppsV1alpha1StatefulSet loadStatefulSetFromYaml(K8sStatefulSet.LoadFromYamlArgs args) {
        ValidationUtil.validate(args);
        try {
            ApiClient apiClient = k8SClientAdmin.getClient(args.getClusterCode());
            CustomObjectsApi customObjectsApi = new CustomObjectsApi(apiClient);
            KruiseAppsV1alpha1StatefulSet statefulSet = Yaml.loadAs(args.getYamlContent(), KruiseAppsV1alpha1StatefulSet.class);
            String statefulSetName = Objects.requireNonNull(statefulSet.getMetadata()).getName();
            String namespace = Objects.requireNonNull(statefulSet.getMetadata()).getNamespace();
            KruiseAppsV1alpha1StatefulSet statefulSetByName = describeStatefulSetByName(args.getClusterCode(), statefulSetName, namespace);
            if (statefulSetByName == null) {
                customObjectsApi.createNamespacedCustomObject(
                        "apps.kruise.io",
                        "v1beta1",
                        namespace,
                        "statefulsets",
                        statefulSet,
                        "false",
                        K8sDryRunUtil.transferState(args.getDryRun()),
                        null
                );
            } else {
                customObjectsApi.replaceClusterCustomObject(
                        "apps.kruise.io",
                        "v1beta1",
                        "statefulsets",
                        statefulSetName,
                        statefulSet,
                        K8sDryRunUtil.transferState(args.getDryRun()),
                        null
                );
            }
            return statefulSet;
        } catch (ApiException e) {
            String responseBody = e.getResponseBody();
            log.error("从yml加载KruiseStatefulSet失败: {}", responseBody, e);
            K8sResource.ActionExceptionBody actionExceptionBody = JSON.parseObject(responseBody, K8sResource.ActionExceptionBody.class);
            if (actionExceptionBody != null) {
                throw new BadRequestException("从yml加载KruiseStatefulSet失败, 原因：" + actionExceptionBody.getReason());
            }
            throw new BadRequestException("从yml加载KruiseStatefulSet失败");
        } catch (Exception e) {
            log.error("从yml加载KruiseStatefulSet失败:", e);
            throw new BadRequestException("从yml加载KruiseStatefulSet失败");
        }
    }
}
