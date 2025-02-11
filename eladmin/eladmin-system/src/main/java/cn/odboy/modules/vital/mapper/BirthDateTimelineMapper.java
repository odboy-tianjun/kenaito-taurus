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
package cn.odboy.modules.vital.mapper;

import cn.odboy.infra.mybatisplus.EasyMapper;
import cn.odboy.modules.vital.domain.BirthDateTimeline;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * LIFE生日: 时间线 Mapper 接口
 * </p>
 *
 * @author odboy
 * @since 2021-12-15
 */
@Mapper
public interface BirthDateTimelineMapper extends EasyMapper<BirthDateTimeline> {
    void deleteBirthDateTimelines();
}
