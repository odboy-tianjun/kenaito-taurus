package cn.odboy.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 应用迭代状态
 *
 * @author odboy
 * @date 2024-11-15
 */
@Getter
@AllArgsConstructor
public enum AppIterationStatusEnum {
    READY(1, "未开始"),
    LOADING(2, "进行中"),
    FINISH(3, "已完成");

    private final Integer code;
    private final String desc;

    public static AppIterationStatusEnum getByCode(Integer code) {
        for (AppIterationStatusEnum item : AppIterationStatusEnum.values()) {
            if (item.code.equals(code)) {
                return item;
            }
        }
        return null;
    }

    public static String getDescByCode(Integer code) {
        for (AppIterationStatusEnum item : AppIterationStatusEnum.values()) {
            if (item.code.equals(code)) {
                return item.getDesc();
            }
        }
        return "";
    }
}
