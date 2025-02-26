package cn.odboy.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * IngressNginx网络类型
 * @author odboy
 * @date 2024-11-21
 */
@Getter
@AllArgsConstructor
public enum NetworkTypeEnum {
    INNER("inner", "内网"),
    OUTER("outer", "外网");

    private final String code;
    private final String desc;

    public static NetworkTypeEnum getByCode(String code) {
        for (NetworkTypeEnum item : NetworkTypeEnum.values()) {
            if (item.code.equals(code)) {
                return item;
            }
        }
        return null;
    }
}
