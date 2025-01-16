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
 * 分支命名规则
 *
 * @author odboy
 * @date 2024-11-15
 */
public interface BranchNameConst {
    /**
     * release_{迭代名称拼音}_{版本号，格式: yyyyMMddHHmmss}
     */
    String RELEASE = "release_%s_%s";
    /**
     * release_{迭代名称拼音}_{版本号，格式: yyyyMMddHHmmss}
     */
    String FEATURE = "feature_%s_%s";
}
