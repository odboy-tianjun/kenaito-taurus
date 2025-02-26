/*
 *  Copyright 2021-2025 Tian Jun
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package cn.odboy.constant;

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
