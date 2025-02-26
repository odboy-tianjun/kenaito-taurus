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
package cn.odboy;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Dict;
import cn.odboy.constant.DingtalkProcessInstanceApproverActionTypeEnum;
import cn.odboy.model.DingtalkWorkflow;
import cn.odboy.repository.DingtalkWorkflowRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Map;

/**
 * 钉钉审批流 测试
 *
 * @author odboy
 * @date 2025-01-16
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class DingtalkWorkflowTests {
    @Autowired
    private DingtalkWorkflowRepository dingtalkWorkflowRepository;

    @Test
    public void createTest() {
        DingtalkWorkflow.CreateArgs createArgs = new DingtalkWorkflow.CreateArgs();
        createArgs.setProcessCode("PROC-86147470-4000-4A70-9A46-36D1A76A152D");
        createArgs.setOriginatorUserId("0707644155678044");
        createArgs.setFormValues(Dict.create()
                .set("名称", "测试名称")
                .set("联系方式", "18888888888")
                .toBean(Map.class)
        );
        DingtalkWorkflow.ApproverArgs approverArgs = new DingtalkWorkflow.ApproverArgs();
        approverArgs.setActionType(DingtalkProcessInstanceApproverActionTypeEnum.NONE);
        approverArgs.setUserIds(CollUtil.newArrayList("016339065647678044"));
        createArgs.setApprovers(CollUtil.newArrayList(approverArgs));
        createArgs.setCcList(null);
        String workflowInstanceId = dingtalkWorkflowRepository.createWorkflow(createArgs);
        System.err.println(workflowInstanceId);
    }
}
