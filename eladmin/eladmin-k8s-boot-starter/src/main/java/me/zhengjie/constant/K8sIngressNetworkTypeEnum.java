package me.zhengjie.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * IngressNginx网络类型
 *
 * @author odboy
 * @date 2024-11-21
 */
@Getter
@AllArgsConstructor
public enum K8sIngressNetworkTypeEnum {
    INNER("inner", "内网"),
    OUTER("outer", "外网");

    private final String code;
    private final String desc;

    public static K8sIngressNetworkTypeEnum getByCode(String code) {
        for (K8sIngressNetworkTypeEnum item : K8sIngressNetworkTypeEnum.values()) {
            if (item.code.equals(code)) {
                return item;
            }
        }
        return null;
    }
}
