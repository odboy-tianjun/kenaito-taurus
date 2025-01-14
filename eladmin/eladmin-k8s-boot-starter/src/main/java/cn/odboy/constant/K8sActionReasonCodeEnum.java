package cn.odboy.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * k8s操作编码
 *
 * @author odboy
 * @date 2025-01-14
 */
@Getter
@AllArgsConstructor
public enum K8sActionReasonCodeEnum {
    NotFound("NotFound", "资源未找到");

    private final String code;
    private final String desc;

    public static K8sActionReasonCodeEnum getByCode(String code) {
        for (K8sActionReasonCodeEnum k8sEnvEnum : K8sActionReasonCodeEnum.values()) {
            if (k8sEnvEnum.getCode().equals(code)) {
                return k8sEnvEnum;
            }
        }
        return null;
    }
}
