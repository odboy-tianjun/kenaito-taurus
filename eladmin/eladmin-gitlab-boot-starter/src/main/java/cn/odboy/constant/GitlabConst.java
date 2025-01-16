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
package cn.odboy.constant;

/**
 * 公共常量
 *
 * @author odboy
 * @date 2024-09-09
 */
public interface GitlabConst {
    /**
     * 正则表达式
     */
    String REGEX_APP_NAME = "^[a-z][a-z0-9]*(-[a-z0-9]+)*$";
    /**
     * ROOT用户命名空间id
     */
    Long ROOT_NAMESPACE_ID = 1L;
    /**
     * 项目默认分支master
     */
    String PROJECT_DEFAULT_BRANCH = "master";
}
