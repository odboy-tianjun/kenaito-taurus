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
package cn.odboy.infra.dingtalk;

import cn.hutool.core.thread.ThreadUtil;
import cn.odboy.consumer.DingtalkWorkflowConsumeRunner;
import cn.odboy.consumer.DingtalkWorkflowListener;
import cn.odboy.context.DingtalkProperties;
import com.dingtalk.open.app.stream.protocol.event.EventAckStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

/**
 * 审批流消息监听
 *
 * @author odboy
 * @date 2025-02-09
 */
@Slf4j
@Configuration
public class DingtalkWorkflowRegisterConfig {
    @Autowired
    private DingtalkProperties dingtalkProperties;
    @Autowired
    private Environment environment;

    @Bean
    public DingtalkWorkflowListener listener() {
        return new cn.odboy.consumer.DingtalkWorkflowListener(dingtalkProperties, environment, event -> {
            try {
                // 消费成功
                ThreadUtil.execAsync(new DingtalkWorkflowConsumeRunner(event));
                return EventAckStatus.SUCCESS;
            } catch (Exception e) {
                // 消费失败
                log.error("消费dingtalk消息失败", e);
                return EventAckStatus.LATER;
            }
        });
    }
}
