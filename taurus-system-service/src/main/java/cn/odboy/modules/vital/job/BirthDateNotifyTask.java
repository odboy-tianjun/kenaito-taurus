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
//package cn.odboy.modules.vital.job;
//
//import cn.hutool.core.collection.CollUtil;
//import cn.hutool.core.date.DateUtil;
//import cn.odboy.modules.vital.domain.BirthDateTimeline;
//import cn.odboy.modules.vital.service.BirthDateTimelineService;
//import cn.odboy.repository.DingtalkMessageRepository;
//import cn.odboy.util.DingTalkTextColorUtil;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Service;
//
//import java.util.Collections;
//import java.util.Date;
//import java.util.List;
//
///**
// * 生日提醒任务
// *
// * @author odboy
// * @date 2022-10-14
// */
//@Slf4j
//@Service
//@RequiredArgsConstructor
//public class BirthDateNotifyTask {
//    private final static int MIN_NOTIFY_DAY = 7;
//    private final BirthDateTimelineService birthdayTimelineService;
//    private final DingtalkMessageRepository dingtalkMessageRepository;
//
//    public void run() {
//        Date nowDate = DateUtil.beginOfDay(new Date());
//        List<BirthDateTimeline> timelines = birthdayTimelineService.findCurrentYearNewestList();
//        for (BirthDateTimeline timeline : timelines) {
//            long betweenDay = DateUtil.betweenDay(timeline.getCurrentBirthday(), nowDate, true);
//            long nowTime = nowDate.getTime();
//            long timelineTime = timeline.getCurrentBirthday().getTime();
//            if (nowTime < timelineTime && betweenDay < MIN_NOTIFY_DAY) {
//                // 超前提醒
//                String content = DingTalkTextColorUtil.getColorMainFontText(timeline.getContactName()) +
//                        ", " +
//                        "距离生日还差" +
//                        DingTalkTextColorUtil.getColorErrorText(betweenDay + "") +
//                        "天(" + DateUtil.formatDate(timeline.getCurrentBirthday()) + "), " +
//                        "注意哦~";
//                dingtalkMessageRepository.asyncSendMarkdownWorkNotice("生日提醒", content, Collections.singletonList(dingTalkProperties.getManageUserId()));
////                EmailBO emailBO = new EmailBO();
////                emailBO.setTos(CollUtil.newArrayList(birthdayProperties.getNotifyEmail()));
////                emailBO.setSubject("生日超前提醒");
////                emailBO.setContent(content);
////                emailRepository.sendSimpleMail(emailBO);
//            } else if (nowTime == timelineTime) {
//                // 当天提醒
//                String content = "今天(" + DateUtil.formatDate(timeline.getCurrentBirthday()) + ")是" +
//                        DingTalkTextColorUtil.getColorMainFontText(timeline.getContactName()) +
//                        "的生日哦, 别忘了送上美好的祝福和红包哦~";
//                dingtalkMessageRepository.asyncSendMarkdownWorkNotice("生日提醒", content, Collections.singletonList(dingTalkProperties.getManageUserId()));
////                EmailBO emailBO = new EmailBO();
////                emailBO.setTos(CollUtil.newArrayList(birthdayProperties.getNotifyEmail()));
////                emailBO.setSubject("生日当天提醒");
////                emailBO.setContent(content);
//            }
//        }
//    }
//
//    public static void main(String[] args) {
//        System.err.println(DateUtil.betweenDay(new Date(), new Date(), true));
//        System.err.println(DateUtil.beginOfDay(new Date()));
//    }
//}
