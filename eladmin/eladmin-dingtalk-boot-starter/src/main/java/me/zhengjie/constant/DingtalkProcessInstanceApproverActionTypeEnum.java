package me.zhengjie.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 审批类型
 *
 * @author odboy
 * @date 2025-01-14
 */
@Getter
@AllArgsConstructor
public enum DingtalkProcessInstanceApproverActionTypeEnum {
    AND("AND", "会签"),
    OR("OR", "或签"),
    NONE("NONE", "单人审批");

    private final String code;
    private final String desc;

    public static DingtalkProcessInstanceApproverActionTypeEnum getByCode(String code) {
        for (DingtalkProcessInstanceApproverActionTypeEnum item : DingtalkProcessInstanceApproverActionTypeEnum.values()) {
            if (item.code.equals(code)) {
                return item;
            }
        }
        return null;
    }
}
