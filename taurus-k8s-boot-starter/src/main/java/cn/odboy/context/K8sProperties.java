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
package cn.odboy.context;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * k8s配置
 *
 * @author odboy
 * @date 2025-01-13
 */
@Data
@ConfigurationProperties(prefix = "k8s")
public class K8sProperties {
    /**
     * 是否启用
     */
    private Boolean enable;
    /**
     * 是否开启调试
     */
    private Boolean debug;
}
