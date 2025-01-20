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
package cn.odboy.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * Gitlab 仓库
 *
 * @author odboy
 * @date 2024-09-09
 */
public class GitlabProject {
    @Data
    public static class CreateArgs {
        /**
         * 项目中文名称
         */
        private String name;
        /**
         * 项目英文名称
         */
        @NotBlank(message = "应用名称不能为空")
        private String appName;
        @NotNull(message = "GitGroup不能为空")
        private Long groupOrUserId;
        /**
         * 项目描述
         */
        private String description;
    }

    @Data
    @EqualsAndHashCode(callSuper = true)
    public static class CreateResp extends CreateArgs {
        /**
         * OwnerId
         */
        private Long creatorId;
        /**
         * 创建时间
         */
        private Date createdAt;
        /**
         * 默认分支
         */
        private String defaultBranch;
        /**
         * git clone 地址
         */
        private String httpUrlToRepo;
        /**
         * 项目id
         */
        private Long projectId;
        /**
         * 项目名称
         */
        private String projectName;
        /**
         * 可见级别
         */
        private String visibility;
        /**
         * 项目地址
         */
        private String homeUrl;
    }
}
