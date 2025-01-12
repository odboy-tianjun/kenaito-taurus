package me.zhengjie.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 应用配置
 *
 * @author odboy
 * @date 2024-11-15
 */
@Getter
@AllArgsConstructor
public enum AppConfigEnum {
    DEFAULT_BRANCH("master", "仓库默认分支");

    private final String code;
    private final String desc;

    public static AppConfigEnum getByCode(String code) {
        for (AppConfigEnum item : AppConfigEnum.values()) {
            if (item.code.equals(code)) {
                return item;
            }
        }
        return null;
    }

    public static String getDescByCode(String code) {
        for (AppConfigEnum item : AppConfigEnum.values()) {
            if (item.code.equals(code)) {
                return item.getDesc();
            }
        }
        return "";
    }
}
