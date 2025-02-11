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
 * Gitlab 配置
 *
 * @author odboy
 * @date 2024-10-01
 */
@Data
@ConfigurationProperties(prefix = "gitlab")
public class GitlabProperties {
    /**
     * 是否启用
     */
    private Boolean enable;
    /**
     * gitlab地址
     * 所需权限：api、read_api、read_user、create_runner、manage_runner、read_repository、write_repository
     * v14.8.2 http://192.168.235.100:20080/-/profile/personal_access_tokens
     * v17.x http://192.168.235.100:20080/-/user_settings/personal_access_tokens
     * 默认分支
     * v17.x http://192.168.235.100:20080/admin/application_settings/repository#js-default-branch-name
     */
    private String url;
    /**
     * root用户的令牌
     */
    private String accessToken;
}
