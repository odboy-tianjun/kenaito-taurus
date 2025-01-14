package me.zhengjie.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import me.zhengjie.constant.K8sAppLabelEnum;

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
        return String.format("%s-%s-sts", appName, envCode);
    }

    public static String getDeploymentName(String appName, String envCode) {
        return String.format("%s-%s-dep", appName, envCode);
    }

    public static String getServiceName(String appName, String envCode) {
        return String.format("%s-%s-svc", appName, envCode);
    }

    public static String getIngressName(String appName, String envCode) {
        return String.format("%s-%s-ing", appName, envCode);
    }

    public static String getPodName(String appName, String envCode) {
        return String.format("%s-%s-pod", appName, envCode);
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
        return new HashMap<String, String>(1) {{
            put(K8sAppLabelEnum.AppName.getCode(), appName);
        }};
    }

    public static String getLabelsStr(String appName) {
        return K8sAppLabelEnum.AppName.getCode() + "=" + appName;
    }
}
