package cn.odboy.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum K8sEnvEnum {
    Daily("daily", "日常环境"),
    Stage("stage", "预发环境"),
    Online("online", "生产环境");

    private final String code;
    private final String desc;

    public static K8sEnvEnum getByCode(String code) {
        for (K8sEnvEnum k8sEnvEnum : K8sEnvEnum.values()) {
            if (k8sEnvEnum.getCode().equals(code)) {
                return k8sEnvEnum;
            }
        }
        return null;
    }

    public static String getByDesc(String code) {
        for (K8sEnvEnum k8sEnvEnum : K8sEnvEnum.values()) {
            if (k8sEnvEnum.getCode().equals(code)) {
                return k8sEnvEnum.getDesc();
            }
        }
        return "unknown";
    }
}
