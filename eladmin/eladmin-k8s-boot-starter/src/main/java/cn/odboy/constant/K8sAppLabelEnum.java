package cn.odboy.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 应用标签 枚举
 *
 * @author odboy
 * @date 2025-01-12
 */
@Getter
@AllArgsConstructor
public enum K8sAppLabelEnum {
    ResGroup("resGroup", "资源组"),
    AppName("appName", "应用名称"),
    AppEnv("appEnv", "环境编码");

    private final String code;
    private final String desc;

    public static K8sAppLabelEnum getByCode(String code) {
        for (K8sAppLabelEnum k8sEnvEnum : K8sAppLabelEnum.values()) {
            if (k8sEnvEnum.getCode().equals(code)) {
                return k8sEnvEnum;
            }
        }
        return null;
    }
}
