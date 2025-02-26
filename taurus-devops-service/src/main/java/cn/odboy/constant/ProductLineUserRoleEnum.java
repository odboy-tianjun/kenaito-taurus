package cn.odboy.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 产品线人员角色
 *
 * @author odboy
 * @date 2024-09-13
 */
@Getter
@AllArgsConstructor
public enum ProductLineUserRoleEnum {
    ADMIN("admin", "管理员"),
    PE("pe", "PE");

    private final String code;
    private final String desc;

    public static ProductLineUserRoleEnum getByCode(String code) {
        for (ProductLineUserRoleEnum item : ProductLineUserRoleEnum.values()) {
            if (item.code.equals(code)) {
                return item;
            }
        }
        return null;
    }
}
