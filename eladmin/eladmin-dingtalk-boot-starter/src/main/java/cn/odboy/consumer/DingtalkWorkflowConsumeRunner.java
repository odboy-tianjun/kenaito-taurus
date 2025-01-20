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
package cn.odboy.consumer;

import com.alibaba.fastjson2.JSON;
import com.dingtalk.open.app.api.message.GenericOpenDingTalkEvent;
import shade.com.alibaba.fastjson2.JSONObject;

/**
 * dingtalk 审批流信息消费线程
 *
 * @author odboy
 * @date 2025-01-16
 */
public class DingtalkWorkflowConsumeRunner implements Runnable {
    private final GenericOpenDingTalkEvent event;

    public DingtalkWorkflowConsumeRunner(GenericOpenDingTalkEvent event) {
        this.event = event;
    }

    @Override
    public void run() {
        // 事件唯一Id
        String eventId = event.getEventId();
        // 事件类型
        String eventType = event.getEventType();
        // 事件产生时间
        Long bornTime = event.getEventBornTime();
        System.err.println("eventId=" + eventId);
        System.err.println("eventType=" + eventType);
        System.err.println("bornTime=" + bornTime);
        // 获取事件体
        JSONObject bizData = event.getData();
        // 处理事件
        process(bizData);
    }

    private void process(JSONObject bizData) {
        System.err.println("bizData=" + JSON.toJSONString(bizData));
    }
}
