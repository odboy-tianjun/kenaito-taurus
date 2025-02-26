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
package cn.odboy.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.odboy.constant.K8sAppLabelEnum;

import java.util.HashMap;
import java.util.Map;

/**
 * k8s资源命名 工具
 *
 * @author odboy
 * @date 2025-01-14
 */
public class K8sResourceNameUtil {
    public static String getStatefulSetName(String appName, String envCode) {
        return String.format("devops-sts-%s-%s", appName, envCode);
    }

    public static String getDeploymentName(String appName, String envCode) {
        return String.format("devops-dep-%s-%s", appName, envCode);
    }

    public static String getServiceName(String appName, String envCode) {
        return String.format("devops-svc-%s-%s", appName, envCode);
    }

    /**
     * 用于创建一个Headless服务，这个服务将提供每个Pod的稳定网络标识符
     */
    public static String getServiceHeadlessName(String appName, String envCode){
        return String.format("devops-hls-%s-%s", appName, envCode);
    }

    public static String getVolumeName(String appName, String envCode){
        return String.format("devops-vol-%s-%s", appName, envCode);
    }

    public static String getIngressName(String appName, String envCode) {
        return String.format("devops-ing-%s-%s", appName, envCode);
    }

    public static String getPodName(String appName, String envCode) {
        return String.format("devops-pod-%s-%s", appName, envCode);
    }

    public static String genLabelSelectorExpression(Map<String, String> labelSelector) {
        String labelSelectorStr = null;
        if (CollUtil.isNotEmpty(labelSelector)) {
            // labelSelectorStr = "key1=value1,key2=value2";
            StringBuilder tempBuilder = new StringBuilder();
            for (Map.Entry<String, String> kvEntry : labelSelector.entrySet()) {
                tempBuilder.append(kvEntry.getKey()).append("=").append(kvEntry.getValue()).append(",");
            }
            if (tempBuilder.length() > 0) {
                tempBuilder.deleteCharAt(tempBuilder.length() - 1);
            }
            if (StrUtil.isNotBlank(tempBuilder)) {
                labelSelectorStr = tempBuilder.toString();
            }
        }
        return labelSelectorStr;
    }

    public static Map<String, String> getLabelsMap(String appName) {
        return new HashMap<>(1) {{
            put(K8sAppLabelEnum.AppName.getCode(), appName);
        }};
    }

    public static String getLabelsStr(String appName) {
        return K8sAppLabelEnum.AppName.getCode() + "=" + appName;
    }
}
