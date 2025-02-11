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
 * dingtalk 配置
 *
 * @author odboy
 * @date 2025-01-15
 */
@Data
@ConfigurationProperties(prefix = "dingtalk")
public class DingtalkProperties {
    /**
     * 是否启用
     */
    private Boolean enable;
    /**
     * 原企业内部应用AgentId
     */
    private String agentId;
    /**
     * Client ID (原 AppKey 和 SuiteKey)
     */
    private String appKey;
    /**
     * Client Secret (原 AppSecret 和 SuiteSecret)
     */
    private String appSecret;
    /**
     * 应用内机器人代码
     */
    private String robotCode;
}
