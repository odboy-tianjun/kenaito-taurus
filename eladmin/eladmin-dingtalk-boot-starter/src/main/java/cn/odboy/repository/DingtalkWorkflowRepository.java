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
package cn.odboy.repository;

import cn.hutool.core.lang.Assert;
import com.aliyun.dingtalkworkflow_1_0.models.*;
import com.aliyun.tea.TeaException;
import com.aliyun.teautil.models.RuntimeOptions;
import lombok.extern.slf4j.Slf4j;
import cn.odboy.constant.DingtalkProcessInstanceResultEnum;
import cn.odboy.context.DingtalkAuthAdmin;
import cn.odboy.infra.exception.BadRequestException;
import cn.odboy.infra.exception.util.MessageFormatterUtil;
import cn.odboy.model.DingtalkWorkflow;
import cn.odboy.util.DingtalkClientConfigFactory;
import cn.odboy.util.ValidationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

/**
 * 审批流
 *
 * @author odboy
 * @date 2025-01-14
 */
@Slf4j
@Component
public class DingtalkWorkflowRepository {
    @Autowired
    private DingtalkAuthAdmin dingtalkAuthAdmin;

    /**
     * 创建审批流
     *
     * @param args /
     * @return
     */
    public String createWorkflow(DingtalkWorkflow.CreateArgs args) {
        ValidationUtil.validate(args);
        try {
            com.aliyun.dingtalkworkflow_1_0.Client client = DingtalkClientConfigFactory.createWorkflowClient();
            com.aliyun.dingtalkworkflow_1_0.models.StartProcessInstanceHeaders startProcessInstanceHeaders = new com.aliyun.dingtalkworkflow_1_0.models.StartProcessInstanceHeaders();
            startProcessInstanceHeaders.xAcsDingtalkAccessToken = dingtalkAuthAdmin.auth();
            com.aliyun.dingtalkworkflow_1_0.models.StartProcessInstanceRequest startProcessInstanceRequest = new com.aliyun.dingtalkworkflow_1_0.models.StartProcessInstanceRequest()
                    .setApprovers(args.getApprovers()
                            .stream()
                            .map(m -> new com.aliyun.dingtalkworkflow_1_0.models.StartProcessInstanceRequest.StartProcessInstanceRequestApprovers()
                                    .setActionType(m.getActionType().getCode())
                                    .setUserIds(m.getUserIds()))
                            .collect(Collectors.toList())
                    )
                    .setCcList(args.getCcList())
                    .setCcPosition("FINISH")
                    .setOriginatorUserId(args.getOriginatorUserId())
                    .setProcessCode(args.getProcessCode())
                    .setFormComponentValues(args.getFormValues()
                            .entrySet()
                            .stream()
                            .map(m -> new com.aliyun.dingtalkworkflow_1_0.models.StartProcessInstanceRequest.StartProcessInstanceRequestFormComponentValues()
                                    .setName(m.getKey())
                                    .setValue(m.getValue())
                            )
                            .collect(Collectors.toList()));
            StartProcessInstanceResponse startProcessInstanceResponse = client.startProcessInstanceWithOptions(startProcessInstanceRequest, startProcessInstanceHeaders, new RuntimeOptions());
            log.info("创建审批流成功");
            return startProcessInstanceResponse.getBody().getInstanceId();
        } catch (TeaException teaException) {
            if (!com.aliyun.teautil.Common.empty(teaException.code) && !com.aliyun.teautil.Common.empty(teaException.message)) {
                String exceptionMessage = "创建审批流失败, code={}, message={}";
                log.error(exceptionMessage, teaException.code, teaException.message, teaException);
                throw new BadRequestException(MessageFormatterUtil.format(exceptionMessage, teaException.code, teaException.message));
            }
            String exceptionMessage = "创建审批流失败";
            log.error(exceptionMessage, teaException);
            throw new BadRequestException(exceptionMessage);
        } catch (Exception exception) {
            TeaException err = new TeaException(exception.getMessage(), exception);
            if (!com.aliyun.teautil.Common.empty(err.code) && !com.aliyun.teautil.Common.empty(err.message)) {
                String exceptionMessage = "创建审批流失败, code={}, message={}";
                log.error(exceptionMessage, err.code, err.message, err);
                throw new BadRequestException(MessageFormatterUtil.format(exceptionMessage, err.code, err.message));
            }
            String exceptionMessage = "创建审批流失败";
            log.error(exceptionMessage, exception);
            throw new BadRequestException(exceptionMessage);
        }
    }

    /**
     * 获取审批流实例详情
     *
     * @param processInstanceId 审批流实例Id
     * @return /
     */
    public GetProcessInstanceResponseBody.GetProcessInstanceResponseBodyResult describeWorkflowByInstanceId(String processInstanceId) {
        Assert.notEmpty(processInstanceId, "审批流实例Id不能为空");
        try {
            com.aliyun.dingtalkworkflow_1_0.Client client = DingtalkClientConfigFactory.createWorkflowClient();
            com.aliyun.dingtalkworkflow_1_0.models.GetProcessInstanceHeaders getProcessInstanceHeaders = new com.aliyun.dingtalkworkflow_1_0.models.GetProcessInstanceHeaders();
            getProcessInstanceHeaders.xAcsDingtalkAccessToken = dingtalkAuthAdmin.auth();
            com.aliyun.dingtalkworkflow_1_0.models.GetProcessInstanceRequest getProcessInstanceRequest = new com.aliyun.dingtalkworkflow_1_0.models.GetProcessInstanceRequest()
                    .setProcessInstanceId(processInstanceId);
            GetProcessInstanceResponse getProcessInstanceResponse = client.getProcessInstanceWithOptions(getProcessInstanceRequest, getProcessInstanceHeaders, new RuntimeOptions());
            log.info("获取审批流实例详情成功");
            return getProcessInstanceResponse.getBody().getResult();
        } catch (TeaException teaException) {
            if (!com.aliyun.teautil.Common.empty(teaException.code) && !com.aliyun.teautil.Common.empty(teaException.message)) {
                String exceptionMessage = "获取审批流实例详情失败, code={}, message={}";
                log.error(exceptionMessage, teaException.code, teaException.message, teaException);
                throw new BadRequestException(MessageFormatterUtil.format(exceptionMessage, teaException.code, teaException.message));
            }
            String exceptionMessage = "获取审批流实例详情失败";
            log.error(exceptionMessage, teaException);
            throw new BadRequestException(exceptionMessage);
        } catch (Exception exception) {
            TeaException err = new TeaException(exception.getMessage(), exception);
            if (!com.aliyun.teautil.Common.empty(err.code) && !com.aliyun.teautil.Common.empty(err.message)) {
                String exceptionMessage = "获取审批流实例详情失败, code={}, message={}";
                log.error(exceptionMessage, err.code, err.message, err);
                throw new BadRequestException(MessageFormatterUtil.format(exceptionMessage, err.code, err.message));
            }
            String exceptionMessage = "获取审批流实例详情失败";
            log.error(exceptionMessage, exception);
            throw new BadRequestException(exceptionMessage);
        }
    }

    /**
     * 改变审批流节点审批状态
     *
     * @param processInstanceId     审批流实例Id
     * @param actionUserId          操作人UserId
     * @param taskId                审批流实例任务Id
     * @param processInstanceResult 审批操作
     * @return /
     */
    public Boolean modifyWorkflowTaskResult(String processInstanceId, String actionUserId, Long taskId, DingtalkProcessInstanceResultEnum processInstanceResult) {
        Assert.notEmpty(processInstanceId, "审批流实例Id不能为空");
        Assert.notEmpty(actionUserId, "操作人UserId不能为空");
        Assert.notNull(taskId, "审批流实例任务Id不能为空");
        Assert.notNull(processInstanceResult, "审批操作不能为空");
        try {
            com.aliyun.dingtalkworkflow_1_0.Client client = DingtalkClientConfigFactory.createWorkflowClient();
            com.aliyun.dingtalkworkflow_1_0.models.ExecuteProcessInstanceHeaders executeProcessInstanceHeaders = new com.aliyun.dingtalkworkflow_1_0.models.ExecuteProcessInstanceHeaders();
            executeProcessInstanceHeaders.xAcsDingtalkAccessToken = dingtalkAuthAdmin.auth();
            com.aliyun.dingtalkworkflow_1_0.models.ExecuteProcessInstanceRequest executeProcessInstanceRequest = new com.aliyun.dingtalkworkflow_1_0.models.ExecuteProcessInstanceRequest()
                    .setResult(processInstanceResult.getCode())
                    .setProcessInstanceId(processInstanceId)
                    .setActionerUserId(actionUserId)
                    .setTaskId(taskId);
            ExecuteProcessInstanceResponse executeProcessInstanceResponse = client.executeProcessInstanceWithOptions(executeProcessInstanceRequest, executeProcessInstanceHeaders, new RuntimeOptions());
            log.info("改变审批流节点审批状态成功");
            return executeProcessInstanceResponse.getBody().getResult();
        } catch (TeaException teaException) {
            if (!com.aliyun.teautil.Common.empty(teaException.code) && !com.aliyun.teautil.Common.empty(teaException.message)) {
                String exceptionMessage = "改变审批流节点审批状态失败, code={}, message={}";
                log.error(exceptionMessage, teaException.code, teaException.message, teaException);
                throw new BadRequestException(MessageFormatterUtil.format(exceptionMessage, teaException.code, teaException.message));
            }
            String exceptionMessage = "改变审批流节点审批状态失败";
            log.error(exceptionMessage, teaException);
            throw new BadRequestException(exceptionMessage);
        } catch (Exception exception) {
            TeaException err = new TeaException(exception.getMessage(), exception);
            if (!com.aliyun.teautil.Common.empty(err.code) && !com.aliyun.teautil.Common.empty(err.message)) {
                String exceptionMessage = "撤回ding消息失败, code={}, message={}";
                log.error(exceptionMessage, err.code, err.message, err);
                throw new BadRequestException(MessageFormatterUtil.format(exceptionMessage, err.code, err.message));
            }
            String exceptionMessage = "改变审批流节点审批状态失败";
            log.error(exceptionMessage, exception);
            throw new BadRequestException(exceptionMessage);
        }
    }

    /**
     * 撤销审批流实例
     *
     * @param processInstanceId 审批流实例Id
     * @param remark            终止说明
     * @return /
     */
    public Boolean revokeWorkflow(String processInstanceId, String remark) {
        Assert.notEmpty(processInstanceId, "审批流实例Id不能为空");
        try {
            com.aliyun.dingtalkworkflow_1_0.Client client = DingtalkClientConfigFactory.createWorkflowClient();
            com.aliyun.dingtalkworkflow_1_0.models.TerminateProcessInstanceHeaders terminateProcessInstanceHeaders = new com.aliyun.dingtalkworkflow_1_0.models.TerminateProcessInstanceHeaders();
            terminateProcessInstanceHeaders.xAcsDingtalkAccessToken = dingtalkAuthAdmin.auth();
            com.aliyun.dingtalkworkflow_1_0.models.TerminateProcessInstanceRequest terminateProcessInstanceRequest = new com.aliyun.dingtalkworkflow_1_0.models.TerminateProcessInstanceRequest()
                    .setIsSystem(true)
                    .setProcessInstanceId(processInstanceId)
                    .setRemark(remark);
            TerminateProcessInstanceResponse terminateProcessInstanceResponse = client.terminateProcessInstanceWithOptions(terminateProcessInstanceRequest, terminateProcessInstanceHeaders, new RuntimeOptions());
            log.info("撤销审批流实例成功");
            return terminateProcessInstanceResponse.getBody().getResult();
        } catch (TeaException teaException) {
            if (!com.aliyun.teautil.Common.empty(teaException.code) && !com.aliyun.teautil.Common.empty(teaException.message)) {
                String exceptionMessage = "撤销审批流实例失败, code={}, message={}";
                log.error(exceptionMessage, teaException.code, teaException.message, teaException);
                throw new BadRequestException(MessageFormatterUtil.format(exceptionMessage, teaException.code, teaException.message));
            }
            String exceptionMessage = "撤销审批流实例失败";
            log.error(exceptionMessage, teaException);
            throw new BadRequestException(exceptionMessage);
        } catch (Exception exception) {
            TeaException err = new TeaException(exception.getMessage(), exception);
            if (!com.aliyun.teautil.Common.empty(err.code) && !com.aliyun.teautil.Common.empty(err.message)) {
                String exceptionMessage = "撤销审批流实例失败, code={}, message={}";
                log.error(exceptionMessage, err.code, err.message, err);
                throw new BadRequestException(MessageFormatterUtil.format(exceptionMessage, err.code, err.message));
            }
            String exceptionMessage = "撤销审批流实例失败";
            log.error(exceptionMessage, exception);
            throw new BadRequestException(exceptionMessage);
        }
    }
}
