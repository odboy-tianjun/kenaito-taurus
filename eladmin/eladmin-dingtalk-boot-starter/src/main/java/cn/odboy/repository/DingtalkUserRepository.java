package cn.odboy.repository;

import cn.hutool.core.lang.Assert;
import cn.odboy.context.DingtalkAuthAdmin;
import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiUserCountRequest;
import com.dingtalk.api.request.OapiV2UserListRequest;
import com.dingtalk.api.response.OapiUserCountResponse;
import com.dingtalk.api.response.OapiV2UserListResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Consumer;

/**
 * 用户管理 2.0
 *
 * @author odboy
 * @date 2025-01-14
 */
@Slf4j
@Component
public class DingtalkUserRepository {
    @Autowired
    private DingtalkAuthAdmin dingtalkAuthAdmin;

    /**
     * 分页查询部门用户
     *
     * @param deptId 部门Id
     * @param cursor 游标，从0开始
     * @return /
     */
    private OapiV2UserListResponse.PageResult searchDepartmentUsers(Long deptId, Long cursor) {
        Assert.notNull(deptId, "部门Id不能为空");
        Assert.notNull(cursor, "游标不能为空");
        try {
            DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/topapi/v2/user/list");
            OapiV2UserListRequest req = new OapiV2UserListRequest();
            req.setDeptId(deptId);
            req.setCursor(cursor);
            req.setSize(100L);
            req.setLanguage("zh_CN");
            OapiV2UserListResponse rsp = client.execute(req, dingtalkAuthAdmin.auth());
            log.info("获取部门下用户列表成功");
            return rsp.getResult();
        } catch (Exception e) {
            log.info("获取部门下用户列表失败", e);
            return null;
        }
    }

    /**
     * 获取部门用户列表
     *
     * @param deptId   部门Id
     * @param consumer /
     */
    public void listDepartmentUsers(Long deptId, Consumer<List<OapiV2UserListResponse.ListUserResponse>> consumer) {
        Long cursor = 0L;
        while (true) {
            OapiV2UserListResponse.PageResult pageResult = searchDepartmentUsers(deptId, cursor);
            if (pageResult == null) {
                break;
            }
            List<OapiV2UserListResponse.ListUserResponse> list = pageResult.getList();
            if (list.isEmpty()) {
                break;
            }
            consumer.accept(list);
            cursor = pageResult.getNextCursor();
        }
    }

    /**
     * 获取部门下用户数量
     *
     * @param deptId 部门Id
     * @return /
     */
    public Long getDepartmentUserCount(Long deptId) {
        Assert.notNull(deptId, "部门Id不能为空");
        try {
            DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/topapi/user/count");
            OapiUserCountRequest req = new OapiUserCountRequest();
            req.setOnlyActive(true);
            OapiUserCountResponse rsp = client.execute(req, dingtalkAuthAdmin.auth());
            log.info("获取部门下用户数量成功");
            return rsp.getResult().getCount();
        } catch (Exception e) {
            log.info("获取部门下用户数量失败", e);
            return 0L;
        }
    }
}
