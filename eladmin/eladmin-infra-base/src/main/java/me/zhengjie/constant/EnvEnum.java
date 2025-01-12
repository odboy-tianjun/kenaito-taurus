package me.zhengjie.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum EnvEnum {
    Daily("daily", "日常环境"),
    Stage("stage", "预发环境"),
    Online("online", "生产环境");

    private final String code;
    private final String desc;

    public static EnvEnum getByCode(String code) {
        for (EnvEnum k8sEnvEnum : EnvEnum.values()) {
            if (k8sEnvEnum.getCode().equals(code)) {
                return k8sEnvEnum;
            }
        }
        return null;
    }

    public static String getByDesc(String code) {
        for (EnvEnum k8sEnvEnum : EnvEnum.values()) {
            if (k8sEnvEnum.getCode().equals(code)) {
                return k8sEnvEnum.getDesc();
            }
        }
        return "unknown";
    }
}
