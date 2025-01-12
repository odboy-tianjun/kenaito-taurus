package me.zhengjie.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 应用等级
 *
 * @author odboy
 * @date 2024-09-13
 */
@Getter
@AllArgsConstructor
public enum AppLevelEnum {
    A("a", "核心应用"),
    B("b", "非核心应用"),
    C("c", "边缘应用");

    private final String code;
    private final String desc;

    public static AppLevelEnum getByCode(String code) {
        for (AppLevelEnum item : AppLevelEnum.values()) {
            if (item.code.equals(code)) {
                return item;
            }
        }
        return null;
    }
    public static String getDescByCode(String code) {
        for (AppLevelEnum item : AppLevelEnum.values()) {
            if (item.code.equals(code)) {
                return item.getDesc();
            }
        }
        return "";
    }
}
