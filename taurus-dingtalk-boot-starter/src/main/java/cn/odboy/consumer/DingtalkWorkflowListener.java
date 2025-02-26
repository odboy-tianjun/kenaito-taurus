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

import cn.hutool.core.thread.ThreadUtil;
import cn.odboy.context.DingtalkProperties;
import com.dingtalk.open.app.api.GenericEventListener;
import com.dingtalk.open.app.api.OpenDingTalkStreamClientBuilder;
import com.dingtalk.open.app.api.security.AuthClientCredential;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;

/**
 * 审批流 Stream监听
 * 应用开发 https://open-dev.dingtalk.com/fe/app?hash=%23%2Fcorp%2Fapp#/corp/app
 * 钉钉应用 - 事件订阅 - Stream模式推送
 * 审批事件 - 审批实例开始，结束
 *
 * @author odboy
 * @date 2025-01-16
 */
@Slf4j
public class DingtalkWorkflowListener {
    private final DingtalkProperties dingtalkProperties;

    public DingtalkWorkflowListener(DingtalkProperties dingtalkProperties, Environment environment, GenericEventListener listener) {
        this.dingtalkProperties = dingtalkProperties;
        if (listener == null) {
            log.info("listener为空，不连接DingtalkStream服务");
            return;
        }
        String propertyValue = environment.getProperty("spring.profiles.active");
        if (propertyValue == null || !"prod".equals(propertyValue)) {
            log.info("非生产环境，不连接DingtalkStream服务");
            return;
        }
        int maxRetryCnt = 30;
        do {
            try {
                connect(listener);
                maxRetryCnt = 0;
                log.info("连接DingtalkStream服务成功");
            } catch (Exception e) {
                maxRetryCnt--;
                log.error("连接DingtalkStream服务失败，等待2s后重连...", e);
                ThreadUtil.safeSleep(2000);
            }
        } while (maxRetryCnt > 0);
    }

    public void connect(GenericEventListener listener) throws Exception {
        OpenDingTalkStreamClientBuilder
                .custom()
                .credential(new AuthClientCredential(dingtalkProperties.getAppKey(), dingtalkProperties.getAppSecret()))
                .consumeThreads(2)
                .registerAllEventListener(listener).build().start();
    }
}
