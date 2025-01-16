/*
 *  Copyright 2022-2025 Tian Jun
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

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * dingtalk支持
 *
 * @author odboy
 * @date 2025-01-13
 */
@Configuration
@EnableConfigurationProperties(DingtalkProperties.class)
@ConditionalOnClass(DingtalkAuthAdmin.class)
public class DingtalkAutoConfiguration {
    @Bean
    public DingtalkAuthAdmin dingtalkAuthAdmin() {
        return new DingtalkAuthAdmin();
    }
}
