package me.zhengjie.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 应用语言
 *
 * @author odboy
 * @date 2024-09-13
 */
@Getter
@AllArgsConstructor
public enum AppLanguageEnum {
    JAVA("java", "Java"),
    PYTHON("python", "Python"),
    GO("golang", "Go"),
    DOTNET("dotnet", "DotNet"),
    REACT("react", "React"),
    VUE("vue", "Vue");

    private final String code;
    private final String desc;

    public static AppLanguageEnum getByCode(String code) {
        for (AppLanguageEnum item : AppLanguageEnum.values()) {
            if (item.code.equals(code)) {
                return item;
            }
        }
        return null;
    }

    public static String getDescByCode(String code) {
        for (AppLanguageEnum item : AppLanguageEnum.values()) {
            if (item.code.equals(code)) {
                return item.getDesc();
            }
        }
        return "unknown";
    }
}
