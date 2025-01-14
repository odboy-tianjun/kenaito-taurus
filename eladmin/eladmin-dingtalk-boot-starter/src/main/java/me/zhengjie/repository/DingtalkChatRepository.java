package me.zhengjie.repository;

import cn.hutool.core.lang.Assert;
import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiChatCreateRequest;
import com.dingtalk.api.request.OapiChatGetRequest;
import com.dingtalk.api.request.OapiChatSubadminUpdateRequest;
import com.dingtalk.api.request.OapiChatUpdateRequest;
import com.dingtalk.api.response.OapiChatCreateResponse;
import com.dingtalk.api.response.OapiChatGetResponse;
import com.dingtalk.api.response.OapiChatSubadminUpdateResponse;
import com.dingtalk.api.response.OapiChatUpdateResponse;
import lombok.extern.slf4j.Slf4j;
import me.zhengjie.context.DingtalkAuthAdmin;
import me.zhengjie.infra.exception.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 群管理
 *
 * @author odboy
 * @date 2025-01-14
 */
@Slf4j
@Component
public class DingtalkChatRepository {
    @Autowired
    private DingtalkAuthAdmin dingtalkAuthAdmin;

    /**
     * 创建群
     *
     * @param name        群名称
     * @param ownerUserId 群主UserId
     * @param joinUserIds 群成员UserId列表
     * @return /
     */
    public OapiChatCreateResponse createChat(String name, String ownerUserId, List<String> joinUserIds) {
        Assert.notEmpty(name, "群名称不能为空");
        Assert.notEmpty(ownerUserId, "群主UserId不能为空");
        Assert.notEmpty(joinUserIds, "群成员UserId列表不能为空");
        Assert.checkBetween(joinUserIds.size(), 2, 40, "群成员UserId列表长度范围2~40");
        try {
            DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/chat/create");
            OapiChatCreateRequest req = new OapiChatCreateRequest();
            req.setName(name);
            req.setOwner(ownerUserId);
            req.setUseridlist(joinUserIds);
            req.setShowHistoryType(1L);
            req.setSearchable(1L);
            req.setValidationType(1L);
            req.setMentionAllAuthority(1L);
            req.setManagementType(1L);
            req.setChatBannedType(0L);
            OapiChatCreateResponse rsp = client.execute(req, dingtalkAuthAdmin.auth());
            log.info("创建群成功");
            return rsp;
        } catch (Exception e) {
            log.info("创建群失败", e);
            throw new BadRequestException("创建群失败");
        }
    }

    /**
     * 群加人
     *
     * @param chatId      群Id
     * @param joinUserIds 群成员UserId列表
     * @return /
     */
    public boolean joinUsersToChat(String chatId, List<String> joinUserIds) {
        Assert.notEmpty(chatId, "群Id不能为空");
        Assert.notEmpty(joinUserIds, "群成员UserId列表不能为空");
        Assert.checkBetween(joinUserIds.size(), 1, 40, "群成员UserId列表长度范围1~40");
        try {
            DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/chat/update");
            OapiChatUpdateRequest req = new OapiChatUpdateRequest();
            req.setChatid(chatId);
            req.setAddUseridlist(joinUserIds);
            OapiChatUpdateResponse rsp = client.execute(req, dingtalkAuthAdmin.auth());
            log.info("群加人成功");
            return rsp.isSuccess();
        } catch (Exception e) {
            log.info("群加人失败", e);
            throw new BadRequestException("群加人失败");
        }
    }

    /**
     * 群踢人
     *
     * @param chatId         群Id
     * @param kickOutUserIds 群成员UserId列表
     * @return /
     */
    public boolean kickOutUsersFromChat(String chatId, List<String> kickOutUserIds) {
        Assert.notEmpty(chatId, "群Id不能为空");
        Assert.notEmpty(kickOutUserIds, "群成员UserId列表不能为空");
        Assert.checkBetween(kickOutUserIds.size(), 1, 40, "群成员UserId列表长度范围1~40");
        try {
            DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/chat/update");
            OapiChatUpdateRequest req = new OapiChatUpdateRequest();
            req.setChatid(chatId);
            req.setDelUseridlist(kickOutUserIds);
            OapiChatUpdateResponse rsp = client.execute(req, dingtalkAuthAdmin.auth());
            log.info("群踢人成功");
            return rsp.isSuccess();
        } catch (Exception e) {
            log.info("群踢人失败", e);
            throw new BadRequestException("群踢人失败");
        }
    }

    /**
     * 修改群名称
     *
     * @param chatId 群Id
     * @param name   群名称
     * @return /
     */
    public boolean modifyChatName(String chatId, String name) {
        Assert.notEmpty(chatId, "群Id不能为空");
        Assert.notEmpty(name, "群名称不能为空");
        try {
            DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/chat/update");
            OapiChatUpdateRequest req = new OapiChatUpdateRequest();
            req.setChatid(chatId);
            req.setName(name);
            OapiChatUpdateResponse rsp = client.execute(req, dingtalkAuthAdmin.auth());
            log.info("修改群名称成功");
            return rsp.isSuccess();
        } catch (Exception e) {
            log.info("修改群名称失败", e);
            throw new BadRequestException("修改群名称失败");
        }
    }

    /**
     * 转让群主
     *
     * @param chatId  群Id
     * @param ownerId 群主Id
     * @return /
     */
    public boolean transferChatOwner(String chatId, String ownerId) {
        Assert.notEmpty(chatId, "群Id不能为空");
        Assert.notEmpty(ownerId, "群主Id不能为空");
        try {
            DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/chat/update");
            OapiChatUpdateRequest req = new OapiChatUpdateRequest();
            req.setChatid(chatId);
            req.setOwner(ownerId);
            OapiChatUpdateResponse rsp = client.execute(req, dingtalkAuthAdmin.auth());
            log.info("转让群主成功");
            return rsp.isSuccess();
        } catch (Exception e) {
            log.info("转让群主失败", e);
            throw new BadRequestException("转让群主失败");
        }
    }

    /**
     * 添加群管理员
     *
     * @param chatId      群Id
     * @param joinUserIds 群成员UserId列表
     * @return /
     */
    public boolean joinAdminUsersToChat(String chatId, List<String> joinUserIds) {
        Assert.notEmpty(chatId, "群Id不能为空");
        Assert.notEmpty(joinUserIds, "群成员UserId列表不能为空");
        Assert.checkBetween(joinUserIds.size(), 1, 40, "群成员UserId列表长度范围1~40");
        try {
            DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/topapi/chat/subadmin/update");
            OapiChatSubadminUpdateRequest req = new OapiChatSubadminUpdateRequest();
            req.setChatid(chatId);
            req.setUserids(String.join(",", joinUserIds));
            req.setRole(2L);
            OapiChatSubadminUpdateResponse rsp = client.execute(req, dingtalkAuthAdmin.auth());
            log.info("添加群管理员成功");
            return rsp.isSuccess();
        } catch (Exception e) {
            log.info("添加群管理员失败", e);
            throw new BadRequestException("添加群管理员失败");
        }
    }

    /**
     * 踢出群管理员
     *
     * @param chatId      群Id
     * @param joinUserIds 群成员UserId列表
     * @return /
     */
    public boolean kickOutAdminUsersToChat(String chatId, List<String> joinUserIds) {
        Assert.notEmpty(chatId, "群Id不能为空");
        Assert.notEmpty(joinUserIds, "群成员UserId列表不能为空");
        Assert.checkBetween(joinUserIds.size(), 1, 40, "群成员UserId列表长度范围1~40");
        try {
            DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/topapi/chat/subadmin/update");
            OapiChatSubadminUpdateRequest req = new OapiChatSubadminUpdateRequest();
            req.setChatid(chatId);
            req.setUserids(String.join(",", joinUserIds));
            req.setRole(3L);
            OapiChatSubadminUpdateResponse rsp = client.execute(req, dingtalkAuthAdmin.auth());
            log.info("踢出群管理员成功");
            return rsp.isSuccess();
        } catch (Exception e) {
            log.info("踢出群管理员失败", e);
            throw new BadRequestException("踢出群管理员失败");
        }
    }

    /**
     * 获取群信息
     *
     * @param chatId 群Id
     * @return /
     */
    public OapiChatGetResponse.ChatInfo describeChatById(String chatId) {
        Assert.notEmpty(chatId, "群Id不能为空");
        try {
            DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/chat/get");
            OapiChatGetRequest req = new OapiChatGetRequest();
            req.setChatid(chatId);
            req.setHttpMethod("GET");
            OapiChatGetResponse rsp = client.execute(req, dingtalkAuthAdmin.auth());
            log.info("获取群信息成功");
            return rsp.getChatInfo();
        } catch (Exception e) {
            log.info("获取群信息失败", e);
            return null;
        }
    }
}
