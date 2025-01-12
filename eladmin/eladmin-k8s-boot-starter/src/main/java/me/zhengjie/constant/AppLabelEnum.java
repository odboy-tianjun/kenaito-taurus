package me.zhengjie.constant;

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
public enum AppLabelEnum {
    AppName("appName", "应用名称"),
    AppEnv("appEnv", "环境");

    private final String code;
    private final String desc;

    public static AppLabelEnum getByCode(String code) {
        for (AppLabelEnum k8sEnvEnum : AppLabelEnum.values()) {
            if (k8sEnvEnum.getCode().equals(code)) {
                return k8sEnvEnum;
            }
        }
        return null;
    }
}
