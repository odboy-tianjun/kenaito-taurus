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
 * IngressNginx网络类型
 *
 * @author odboy
 * @date 2024-11-21
 */
@Getter
@AllArgsConstructor
public enum K8sIngressNetworkTypeEnum {
    INNER("inner", "内网"),
    OUTER("outer", "外网");
    private final String code;
    private final String desc;

    public static K8sIngressNetworkTypeEnum getByCode(String code) {
        for (K8sIngressNetworkTypeEnum item : K8sIngressNetworkTypeEnum.values()) {
            if (item.code.equals(code)) {
                return item;
            }
        }
        return null;
    }
}
