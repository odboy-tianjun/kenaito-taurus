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
package cn.odboy.vital.service;

import cn.odboy.vital.domain.BirthDateTimeline;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 生日时间线 服务类
 * </p>
 *
 * @author odboy
 * @since 2021-12-15
 */
public interface BirthDateTimelineService extends IService<BirthDateTimeline> {
    void deleteAll();

    /**
     * 查询当年时间线列表
     *
     * @return List<BirthdayTimeline>
     */
    List<BirthDateTimeline> describeBirthdayTimelines();

    BirthDateTimeline findOneByDate(Integer nowYear, String gregorianDate, Long contactId);

    List<BirthDateTimeline> findCurrentYearNewestList();
}
