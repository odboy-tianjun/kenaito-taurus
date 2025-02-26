package cn.odboy.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 应用迭代变更状态
 *
 * @author odboy
 * @date 2024-11-15
 */
@Getter
@AllArgsConstructor
public enum AppIterationChangeStatusEnum {
    ENABLE(1, "集成"),
    DISABLE(2, "不集成");

    private final Integer code;
    private final String desc;

    public static AppIterationChangeStatusEnum getByCode(Integer code) {
        for (AppIterationChangeStatusEnum item : AppIterationChangeStatusEnum.values()) {
            if (item.code.equals(code)) {
                return item;
            }
        }
        return null;
    }

    public static String getDescByCode(Integer code) {
        for (AppIterationChangeStatusEnum item : AppIterationChangeStatusEnum.values()) {
            if (item.code.equals(code)) {
                return item.getDesc();
            }
        }
        return "";
    }
}
