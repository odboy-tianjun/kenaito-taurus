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
 * 应用标签 枚举
 *
 * @author odboy
 * @date 2025-01-12
 */
@Getter
@AllArgsConstructor
public enum K8sAppLabelEnum {
    ResGroup("resGroup", "资源组"),
    AppName("appName", "应用名称"),
    EnvCode("envCode", "环境编码");
    private final String code;
    private final String desc;

    public static K8sAppLabelEnum getByCode(String code) {
        for (K8sAppLabelEnum k8sEnvEnum : K8sAppLabelEnum.values()) {
            if (k8sEnvEnum.getCode().equals(code)) {
                return k8sEnvEnum;
            }
        }
        return null;
    }
}
