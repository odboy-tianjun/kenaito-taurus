package cn.odboy.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum K8sCoreEnum {
    AppName("appName", "应用名称"),
    AppEnv("appEnv", "环境");

    private final String code;
    private final String desc;

    public static K8sCoreEnum getByCode(String code) {
        for (K8sCoreEnum k8sEnvEnum : K8sCoreEnum.values()) {
            if (k8sEnvEnum.getCode().equals(code)) {
                return k8sEnvEnum;
            }
        }
        return null;
    }
}
