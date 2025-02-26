package cn.odboy.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * IngressNginx网络类型后缀
 * @author odboy
 * @date 2024-11-21
 */
@Getter
@AllArgsConstructor
public enum NetworkTypeSuffixEnum {
    INNER(".com", "内网"),
    OUTER(".cn", "外网");

    private final String code;
    private final String desc;

    public static NetworkTypeSuffixEnum getByCode(String code) {
        for (NetworkTypeSuffixEnum item : NetworkTypeSuffixEnum.values()) {
            if (item.code.equals(code)) {
                return item;
            }
        }
        return null;
    }
}
