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
package cn.odboy.modules.vital.service;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.odboy.infra.mybatisplus.impl.EasyServiceImpl;
import cn.odboy.vital.domain.BirthDateTimeline;
import cn.odboy.modules.vital.mapper.BirthDateTimelineMapper;
import cn.odboy.vital.service.BirthDateTimelineService;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * 生日时间线 服务实现类
 * </p>
 *
 * @author odboy
 * @since 2021-12-15
 */
@Service
public class BirthDateTimelineServiceImpl extends EasyServiceImpl<BirthDateTimelineMapper, BirthDateTimeline> implements BirthDateTimelineService {
    @Override
    public void deleteAll() {
        baseMapper.deleteBirthDateTimelines();
    }

    @Override
    public List<BirthDateTimeline> describeBirthdayTimelines() {
        int year = DateUtil.year(new Date());
        List<BirthDateTimeline> birthdayTimelines = this.quickList(lambdaQuery().eq(BirthDateTimeline::getCurrentYear, year)
                .orderByAsc(BirthDateTimeline::getCurrentBirthday)
        );
        Date nowDate = new Date();
        int nm = DateUtil.month(nowDate);
        for (BirthDateTimeline birthdayTimeline : birthdayTimelines) {
            int om = DateUtil.month(birthdayTimeline.getCurrentBirthday());
            if (nm == om) {
                birthdayTimeline.setInProgress(true);
                break;
            }
        }
        return birthdayTimelines;
    }

    @Override
    public BirthDateTimeline findOneByDate(Integer nowYear, String gregorianDate, Long contactId) {
        return lambdaQuery()
                .eq(BirthDateTimeline::getCurrentYear, nowYear)
                .eq(BirthDateTimeline::getCurrentBirthday, gregorianDate)
                .eq(BirthDateTimeline::getContactId, contactId)
                .one();
    }

    @Override
    public List<BirthDateTimeline> findCurrentYearNewestList() {
        DateTime now = DateTime.now();
        return lambdaQuery()
                .eq(BirthDateTimeline::getCurrentYear, DateUtil.year(now))
                .gt(BirthDateTimeline::getCurrentBirthday, DateUtil.formatDate(now))
                .orderByAsc(BirthDateTimeline::getCurrentBirthday)
                .list();
    }
}
