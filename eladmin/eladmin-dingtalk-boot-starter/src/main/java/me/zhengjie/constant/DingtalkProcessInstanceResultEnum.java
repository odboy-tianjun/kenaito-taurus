package me.zhengjie.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 审批操作
 *
 * @author odboy
 * @date 2025-01-14
 */
@Getter
@AllArgsConstructor
public enum DingtalkProcessInstanceResultEnum {
    AGREE("agree", "同意"),
    REFUSE("refuse", "拒绝");

    private final String code;
    private final String desc;

    public static DingtalkProcessInstanceResultEnum getByCode(String code) {
        for (DingtalkProcessInstanceResultEnum item : DingtalkProcessInstanceResultEnum.values()) {
            if (item.code.equals(code)) {
                return item;
            }
        }
        return null;
    }
}
