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
package cn.odboy.repository;

import cn.odboy.context.GitlabAuthAdmin;
import cn.odboy.infra.exception.BadRequestException;
import lombok.extern.slf4j.Slf4j;
import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GroupApi;
import org.gitlab4j.api.models.Group;
import org.gitlab4j.api.models.GroupParams;
import org.gitlab4j.api.models.Project;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * gitlab group
 *
 * @author odboy
 * @date 2025-01-12
 */
@Slf4j
@Component
public class GitlabGroupRepository {
    @Autowired
    private GitlabAuthAdmin gitlabAuthAdmin;

    /**
     * 创建Git分组 -> ok
     *
     * @param groupName /
     * @return /
     */
    public Group createGroup(String groupName) {
        try (GitLabApi client = gitlabAuthAdmin.auth()) {
            GroupApi groupApi = client.getGroupApi();
            GroupParams groupParams = new GroupParams();
            groupParams.withName(groupName);
            groupParams.withPath(groupName);
            // 私有分组
            groupParams.withVisibility("0");
            return groupApi.createGroup(groupParams);
        } catch (Exception e) {
            log.error("创建Git分组失败", e);
            throw new BadRequestException("创建Git分组失败");
        }
    }

    /**
     * 通过GroupName查Git分组 -> ok
     *
     * @param groupName /
     * @return /
     */
    public Group describeGroupByGroupName(String groupName) {
        try (GitLabApi client = gitlabAuthAdmin.auth()) {
            GroupApi groupApi = client.getGroupApi();
            return groupApi.getGroupsStream().filter(f -> f.getName().equals(groupName)).findFirst().orElse(null);
        } catch (Exception e) {
            log.error("通过groupName查Git分组失败", e);
            return null;
        }
    }

    /**
     * 分页获取Git分组 -> ok
     *
     * @param page 当前页
     * @return /
     */
    public List<Group> listGroups(int page) {
        int newPage = page <= 0 ? 1 : page;
        List<Group> list = new ArrayList<>();
        try (GitLabApi client = gitlabAuthAdmin.auth()) {
            GroupApi groupApi = client.getGroupApi();
            return groupApi.getGroups(newPage, 10000);
        } catch (Exception e) {
            log.error("分页获取Git分组失败", e);
            return list;
        }
    }


    /**
     * 分页获取分组项目 -> ok
     *
     * @param page 当前页
     * @return /
     */
    public List<Project> listProjects(Long groupId, int page) {
        int newPage = page <= 0 ? 1 : page;
        List<Project> list = new ArrayList<>();
        try (GitLabApi client = gitlabAuthAdmin.auth()) {
            GroupApi groupApi = client.getGroupApi();
            return groupApi.getProjects(groupId, newPage, 10000);
        } catch (Exception e) {
            log.error("分页获取分组应用失败", e);
            return list;
        }
    }
}
