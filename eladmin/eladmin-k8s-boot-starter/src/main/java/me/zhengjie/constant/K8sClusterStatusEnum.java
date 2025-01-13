package me.zhengjie.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * k8s 集群状态
 *
 * @author odboy
 * @date 2025-01-13
 */
@Getter
@AllArgsConstructor
public enum K8sClusterStatusEnum {
    HEALTH(true, "健康"),
    UN_HEALTH(false, "不健康");

    private final Boolean code;
    private final String desc;

    public static K8sClusterStatusEnum getByCode(String code) {
        for (K8sClusterStatusEnum k8sEnvEnum : K8sClusterStatusEnum.values()) {
            if (k8sEnvEnum.getCode().equals(code)) {
                return k8sEnvEnum;
            }
        }
        return null;
    }
}
