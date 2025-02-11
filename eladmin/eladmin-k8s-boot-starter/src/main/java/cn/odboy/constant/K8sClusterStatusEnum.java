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
 * k8s 集群状态
 *
 * @author odboy
 * @date 2025-01-13
 */
@Getter
@AllArgsConstructor
public enum K8sClusterStatusEnum {
    HEALTH(true, "健康"),
    UN_HEALTH(false, "不健康");
    private final Boolean code;
    private final String desc;

    public static K8sClusterStatusEnum getByCode(String code) {
        for (K8sClusterStatusEnum k8sEnvEnum : K8sClusterStatusEnum.values()) {
            if (k8sEnvEnum.getCode().equals(code)) {
                return k8sEnvEnum;
            }
        }
        return null;
    }
}
