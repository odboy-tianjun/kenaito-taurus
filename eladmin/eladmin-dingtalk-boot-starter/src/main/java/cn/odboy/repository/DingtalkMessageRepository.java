package cn.odboy.repository;

import cn.hutool.core.lang.Assert;
import cn.odboy.context.DingtalkAuthAdmin;
import cn.odboy.util.DingtalkClientHelper;
import com.aliyun.dingtalkrobot_1_0.models.RobotRecallDingResponse;
import com.aliyun.dingtalkrobot_1_0.models.RobotSendDingResponse;
import com.aliyun.dingtalkrobot_1_0.models.RobotSendDingResponseBody;
import com.aliyun.tea.TeaException;
import com.aliyun.teautil.models.RuntimeOptions;
import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiMessageCorpconversationAsyncsendV2Request;
import com.dingtalk.api.request.OapiMessageCorpconversationGetsendresultRequest;
import com.dingtalk.api.request.OapiMessageCorpconversationRecallRequest;
import com.dingtalk.api.response.OapiMessageCorpconversationAsyncsendV2Response;
import com.dingtalk.api.response.OapiMessageCorpconversationGetsendresultResponse;
import com.dingtalk.api.response.OapiMessageCorpconversationRecallResponse;
import lombok.extern.slf4j.Slf4j;
import cn.odboy.context.DingtalkProperties;
import cn.odboy.infra.exception.BadRequestException;
import cn.odboy.infra.exception.util.MessageFormatterUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 消息通知
 *
 * @author odboy
 * @date 2025-01-14
 */
@Slf4j
@Component
public class DingtalkMessageRepository {
    @Autowired
    private DingtalkProperties dingtalkProperties;
    @Autowired
    private DingtalkAuthAdmin dingtalkAuthAdmin;

    /**
     * 异步发送文本工作通知
     *
     * @param dingtalkUserIds 接收人Dingtalk用户Id列表
     * @param content         文本内容
     * @param toAllUser       是否发送给所有人
     * @return /
     */
    public Long asyncSendTextWorkNotice(List<String> dingtalkUserIds, String content, boolean toAllUser) {
        Assert.notEmpty(dingtalkUserIds, "接收人Dingtalk用户Id列表不能为空");
        Assert.checkBetween(dingtalkUserIds.size(), 1, 100, "接收人Dingtalk用户Id列表长度范围1~100");
        try {
            DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/topapi/message/corpconversation/asyncsend_v2");
            OapiMessageCorpconversationAsyncsendV2Request req = new OapiMessageCorpconversationAsyncsendV2Request();
            req.setAgentId(Long.valueOf(dingtalkProperties.getAgentId()));
            req.setUseridList(String.join(",", dingtalkUserIds));
            req.setToAllUser(toAllUser);
            OapiMessageCorpconversationAsyncsendV2Request.Msg msg = new OapiMessageCorpconversationAsyncsendV2Request.Msg();
            msg.setMsgtype("text");
            OapiMessageCorpconversationAsyncsendV2Request.Text text = new OapiMessageCorpconversationAsyncsendV2Request.Text();
            text.setContent(content);
            msg.setText(text);
            req.setMsg(msg);
            OapiMessageCorpconversationAsyncsendV2Response rsp = client.execute(req, dingtalkAuthAdmin.auth());
            log.info("异步发送文本工作通知成功");
            return rsp.getTaskId();
        } catch (Exception e) {
            log.info("异步发送文本工作通知失败", e);
            throw new BadRequestException("异步发送文本工作通知失败");
        }
    }

    /**
     * 异步发送Markdown工作通知
     *
     * @param dingtalkUserIds 接收人Dingtalk用户Id列表
     * @param title           标题
     * @param text            内容
     * @param toAllUser       是否发送给所有人
     * @return /
     */
    public Long asyncSendMarkdownWorkNotice(List<String> dingtalkUserIds, String title, String text, boolean toAllUser) {
        Assert.notEmpty(dingtalkUserIds, "接收人Dingtalk用户Id列表不能为空");
        Assert.checkBetween(dingtalkUserIds.size(), 1, 100, "接收人Dingtalk用户Id列表长度范围1~100");
        try {
            DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/topapi/message/corpconversation/asyncsend_v2");
            OapiMessageCorpconversationAsyncsendV2Request req = new OapiMessageCorpconversationAsyncsendV2Request();
            req.setAgentId(Long.valueOf(dingtalkProperties.getAgentId()));
            req.setUseridList(String.join(",", dingtalkUserIds));
            req.setToAllUser(toAllUser);
            OapiMessageCorpconversationAsyncsendV2Request.Msg msg = new OapiMessageCorpconversationAsyncsendV2Request.Msg();
            msg.setMsgtype("markdown");
            OapiMessageCorpconversationAsyncsendV2Request.Markdown markdown = new OapiMessageCorpconversationAsyncsendV2Request.Markdown();
            markdown.setText(text);
            markdown.setTitle(title);
            msg.setMarkdown(markdown);
            req.setMsg(msg);
            OapiMessageCorpconversationAsyncsendV2Response rsp = client.execute(req, dingtalkAuthAdmin.auth());
            log.info("异步发送Markdown工作通知成功");
            return rsp.getTaskId();
        } catch (Exception e) {
            log.info("异步发送Markdown工作通知失败", e);
            throw new BadRequestException("异步发送Markdown工作通知失败");
        }
    }

    /**
     * 获取工作通知的发送结果
     *
     * @param taskId 发送工作通知后返回的taskId
     * @return /
     */
    public OapiMessageCorpconversationGetsendresultResponse.AsyncSendResult getWorkNoticeSendResult(Long taskId) {
        Assert.notNull(taskId, "taskId不能为空");
        try {
            DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/topapi/message/corpconversation/getsendresult");
            OapiMessageCorpconversationGetsendresultRequest req = new OapiMessageCorpconversationGetsendresultRequest();
            req.setAgentId(Long.valueOf(dingtalkProperties.getAgentId()));
            req.setTaskId(taskId);
            OapiMessageCorpconversationGetsendresultResponse rsp = client.execute(req, dingtalkAuthAdmin.auth());
            log.info("获取工作通知的发送结果成功");
            return rsp.getSendResult();
        } catch (Exception e) {
            log.error("获取工作通知的发送结果失败", e);
            throw new BadRequestException("获取工作通知的发送结果失败");
        }
    }

    /**
     * 撤销工作通知
     *
     * @param taskId 发送工作通知后返回的taskId
     * @return /
     */
    public boolean revokeWorkNotice(Long taskId) {
        Assert.notNull(taskId, "taskId不能为空");
        try {
            DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/topapi/message/corpconversation/recall");
            OapiMessageCorpconversationRecallRequest req = new OapiMessageCorpconversationRecallRequest();
            req.setAgentId(Long.valueOf(dingtalkProperties.getAgentId()));
            req.setMsgTaskId(taskId);
            OapiMessageCorpconversationRecallResponse rsp = client.execute(req, dingtalkAuthAdmin.auth());
            log.info("撤销工作通知成功");
            return rsp.getErrcode() == 0;
        } catch (Exception e) {
            log.info("撤销工作通知失败", e);
            return false;
        }
    }

    /**
     * 发送ding消息
     *
     * @param dingtalkUserIds 接收人Dingtalk用户Id列表
     * @param content         消息内容
     * @return /
     */
    public RobotSendDingResponseBody sendDingNotice(List<String> dingtalkUserIds, String content) {
        Assert.notEmpty(dingtalkUserIds, "接收人Dingtalk用户Id列表不能为空");
        Assert.checkBetween(dingtalkUserIds.size(), 1, 200, "接收人Dingtalk用户Id列表长度范围1~100");
        try {
            com.aliyun.dingtalkrobot_1_0.Client client = DingtalkClientHelper.createRobotClient();
            com.aliyun.dingtalkrobot_1_0.models.RobotSendDingHeaders robotSendDingHeaders = new com.aliyun.dingtalkrobot_1_0.models.RobotSendDingHeaders();
            robotSendDingHeaders.xAcsDingtalkAccessToken = dingtalkAuthAdmin.auth();
            com.aliyun.dingtalkrobot_1_0.models.RobotSendDingRequest robotSendDingRequest = new com.aliyun.dingtalkrobot_1_0.models.RobotSendDingRequest()
                    .setRobotCode(dingtalkProperties.getRobotCode())
                    .setContent(content)
                    .setRemindType(1)
                    .setReceiverUserIdList(dingtalkUserIds);
            RobotSendDingResponse robotSendDingResponse = client.robotSendDingWithOptions(robotSendDingRequest, robotSendDingHeaders, new RuntimeOptions());
            return robotSendDingResponse.getBody();
        } catch (TeaException teaException) {
            if (!com.aliyun.teautil.Common.empty(teaException.code) && !com.aliyun.teautil.Common.empty(teaException.message)) {
                String exceptionMessage = "发送ding消息失败, code={}, message={}";
                log.error(exceptionMessage, teaException.code, teaException.message, teaException);
                throw new BadRequestException(MessageFormatterUtil.format(exceptionMessage, teaException.code, teaException.message));
            }
            String exceptionMessage = "发送ding消息失败";
            log.error(exceptionMessage, teaException);
            throw new BadRequestException(exceptionMessage);
        } catch (Exception exception) {
            TeaException err = new TeaException(exception.getMessage(), exception);
            if (!com.aliyun.teautil.Common.empty(err.code) && !com.aliyun.teautil.Common.empty(err.message)) {
                String exceptionMessage = "发送ding消息失败, code={}, message={}";
                log.error(exceptionMessage, err.code, err.message, err);
                throw new BadRequestException(MessageFormatterUtil.format(exceptionMessage, err.code, err.message));
            }
            String exceptionMessage = "发送ding消息失败";
            log.error(exceptionMessage, exception);
            throw new BadRequestException(exceptionMessage);
        }
    }

    /**
     * 撤回ding消息
     *
     * @param openDingId 发送ding消息后返回的openDingId
     * @return /
     */
    public String revokeDingNotice(String openDingId) {
        Assert.notEmpty(openDingId, "openDingId不能为空");
        try {
            com.aliyun.dingtalkrobot_1_0.Client client = DingtalkClientHelper.createRobotClient();
            com.aliyun.dingtalkrobot_1_0.models.RobotRecallDingHeaders robotRecallDingHeaders = new com.aliyun.dingtalkrobot_1_0.models.RobotRecallDingHeaders();
            robotRecallDingHeaders.xAcsDingtalkAccessToken = dingtalkAuthAdmin.auth();
            com.aliyun.dingtalkrobot_1_0.models.RobotRecallDingRequest robotRecallDingRequest = new com.aliyun.dingtalkrobot_1_0.models.RobotRecallDingRequest()
                    .setRobotCode(dingtalkProperties.getRobotCode())
                    .setOpenDingId(openDingId);
            RobotRecallDingResponse robotRecallDingResponse = client.robotRecallDingWithOptions(robotRecallDingRequest, robotRecallDingHeaders, new RuntimeOptions());
            return robotRecallDingResponse.getBody().getOpenDingId();
        } catch (TeaException teaException) {
            if (!com.aliyun.teautil.Common.empty(teaException.code) && !com.aliyun.teautil.Common.empty(teaException.message)) {
                String exceptionMessage = "撤回ding消息失败, code={}, message={}";
                log.error(exceptionMessage, teaException.code, teaException.message, teaException);
                throw new BadRequestException(MessageFormatterUtil.format(exceptionMessage, teaException.code, teaException.message));
            }
            String exceptionMessage = "撤回ding消息失败";
            log.error(exceptionMessage, teaException);
            throw new BadRequestException(exceptionMessage);
        } catch (Exception exception) {
            TeaException err = new TeaException(exception.getMessage(), exception);
            if (!com.aliyun.teautil.Common.empty(err.code) && !com.aliyun.teautil.Common.empty(err.message)) {
                String exceptionMessage = "撤回ding消息失败, code={}, message={}";
                log.error(exceptionMessage, err.code, err.message, err);
                throw new BadRequestException(MessageFormatterUtil.format(exceptionMessage, err.code, err.message));
            }
            String exceptionMessage = "撤回ding消息失败";
            log.error(exceptionMessage, exception);
            throw new BadRequestException(exceptionMessage);
        }
    }
}
