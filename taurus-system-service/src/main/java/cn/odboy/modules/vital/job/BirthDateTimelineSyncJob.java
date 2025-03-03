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
package cn.odboy.modules.vital.job;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.chinese.LunarInfo;
import cn.odboy.mybatisplus.model.PageResult;
import cn.odboy.vital.constant.BirthDateComputeModeEnum;
import cn.odboy.vital.domain.BirthDate;
import cn.odboy.vital.domain.BirthDateTimeline;
import cn.odboy.vital.service.BirthDateService;
import cn.odboy.vital.service.BirthDateTimelineService;
import cn.odboy.vital.service.CalendarDictService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * 生日时间线 同步任务
 *
 * @author odboy
 * @date 2022-10-13
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BirthDateTimelineSyncJob {
    private final BirthDateService birthdayService;
    private final BirthDateTimelineService birthdayTimelineService;
    private final CalendarDictService birthdayDateDictService;

    @Transactional(rollbackFor = Exception.class)
    public void run() {
        Date nowTime = DateTime.now();
        int nowYear = DateUtil.year(nowTime);
        birthdayTimelineService.deleteAll();
        PageResult<BirthDate.SearchBirthDates> birthdays = birthdayService.searchBirthDates(null, new Page<>(1, 1000));
        for (BirthDate.SearchBirthDates birthday : birthdays.getContent()) {
            if (BirthDateComputeModeEnum.GREGORIAN.getCode().equals(birthday.getComputeMode())) {
                // 公历年
                String gregorianDate = birthday.getGregorianDate();
                Date currentBirthday = DateUtil.parseDate(nowYear + gregorianDate.substring(gregorianDate.indexOf("-"), gregorianDate.length() - 1));
                BirthDateTimeline timeline = new BirthDateTimeline();
                timeline.setBirthdayId(birthday.getId());
                timeline.setCurrentYear(nowYear);
                timeline.setCurrentBirthday(currentBirthday);
                timeline.setContactId(birthday.getContactId());
                timeline.setContactName(birthday.getContactRealName());
                timeline.setOneYearOld(birthday.getOneYearOld());
                timeline.setVirtualYearOld(birthday.getVirtualYearOld());
                timeline.setZodiac(birthday.getZodiac());
                timeline.setChineseZodiac(birthday.getChineseZodiac());
                // 当月有哪些人过生日
                timeline.setInProgress(DateUtil.month(nowTime) == DateUtil.month(timeline.getCurrentBirthday()));
                birthdayTimelineService.save(timeline);
            } else if ("lunar".equals(birthday.getComputeMode())) {
                // 农历年
                BirthDate.DescribeLunarDateByLunarYmd args = new BirthDate.DescribeLunarDateByLunarYmd();
                args.setLunarYear(String.valueOf(nowYear));
                args.setLunarMonth(birthday.getLunarMonth());
                args.setLunarDay(birthday.getLunarDay());
                Date currentBirthday;
                try {
                    currentBirthday = birthdayDateDictService.describeLunarDateByLunarYmd(args);
                } catch (Exception e) {
                    log.warn("这位仁兄可能不是闰年月", e);
                    args.setLunarMonth(args.getLunarMonth().replace("闰", ""));
                    currentBirthday = birthdayDateDictService.describeLunarDateByLunarYmd(args);
                }
                BirthDateTimeline timeline = new BirthDateTimeline();
                timeline.setBirthdayId(birthday.getId());
                timeline.setCurrentYear(nowYear);
                timeline.setCurrentBirthday(currentBirthday);
                timeline.setContactId(birthday.getContactId());
                timeline.setContactName(birthday.getContactRealName());
                timeline.setOneYearOld(birthday.getOneYearOld());
                timeline.setVirtualYearOld(birthday.getVirtualYearOld());
                timeline.setZodiac(birthday.getZodiac());
                timeline.setChineseZodiac(birthday.getChineseZodiac());
                // 当月有哪些人过生日
                timeline.setInProgress(DateUtil.month(nowTime) == DateUtil.month(timeline.getCurrentBirthday()));
                birthdayTimelineService.save(timeline);
            }
        }
    }

    public static void main(String[] args) {
        Date nowTime = DateTime.now();
        // 一年之初
        System.err.println(DateUtil.beginOfYear(nowTime));
        // 一年之末
        System.err.println(DateUtil.endOfYear(nowTime));
        System.err.println(DateUtil.month(nowTime));
        System.err.println(DateUtil.dayOfMonth(nowTime));
        // 计算年龄, 周岁=今年-出生年, 虚岁=今年-出生年+1
        System.err.println(LunarInfo.leapMonth(1995));
        String dateStr = "2023-11-27";
        System.err.println(dateStr.substring(dateStr.indexOf("-"), dateStr.length() - 1));
    }
}
