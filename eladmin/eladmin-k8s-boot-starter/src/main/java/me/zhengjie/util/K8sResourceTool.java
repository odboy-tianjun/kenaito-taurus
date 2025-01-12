package me.zhengjie.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import me.zhengjie.constant.AppLabelEnum;

import java.util.HashMap;
import java.util.Map;

public class K8sResourceTool {

    public static String getStatefulSetName(String appName) {
        return "sts-" + appName;
    }

    public static String getDeploymentName(String appName) {
        return "dep-" + appName;
    }

    public static String getServiceName(String appName) {
        return "svc-" + appName;
    }

    public static String getIngressName(String appName) {
        return "ing-" + appName;
    }

    public static String getPodName(String appName) {
        return "pod-" + appName;
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
            put(AppLabelEnum.AppName.getCode(), appName);
        }};
    }

    public static String getLabelsStr(String appName) {
        return AppLabelEnum.AppName.getCode() + "=" + appName;
    }
}
