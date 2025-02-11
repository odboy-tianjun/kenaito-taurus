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

import cn.hutool.core.util.StrUtil;
import cn.odboy.context.GitlabAuthAdmin;
import cn.odboy.infra.exception.BadRequestException;
import lombok.extern.slf4j.Slf4j;
import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.Pager;
import org.gitlab4j.api.RepositoryApi;
import org.gitlab4j.api.models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Gitlab仓库
 *
 * @author odboy
 * @date 2024-09-09
 */
@Slf4j
@Component
public class GitlabProjectBranchRepository {
    @Autowired
    private GitlabAuthAdmin gitlabAuthAdmin;
    @Autowired
    private GitlabProjectRepository gitlabProjectRepository;

    /**
     * 创建分支
     *
     * @param projectId  项目id
     * @param branchName 分支名称
     * @param ref        从什么分支创建, 比如: master
     * @return /
     */
    public Branch createBranchByProjectId(Long projectId, String branchName, String ref) {
        Project project = gitlabProjectRepository.describeProjectById(projectId);
        try (GitLabApi client = gitlabAuthAdmin.auth()) {
            RepositoryApi gitlabAuthAdminApi = client.getRepositoryApi();
            if (StrUtil.isBlank(ref)) {
                ref = project.getDefaultBranch();
            }
            return gitlabAuthAdminApi.createBranch(projectId, branchName, ref);
        } catch (Exception e) {
            log.error("创建分支失败", e);
            throw new BadRequestException("创建分支失败");
        }
    }

    /**
     * 创建分支
     *
     * @param projectName 项目名称
     * @param branchName  分支名称
     * @param ref         从什么分支创建, 比如: master
     * @return /
     */
    public Branch createBranchByProjectName(String projectName, String branchName, String ref) {
        Project project = gitlabProjectRepository.describeProjectByProjectName(projectName);
        if (project == null) {
            throw new BadRequestException(String.format("没有找到项目%s，请先去创建项目", projectName));
        }
        return createBranchByProjectId(project.getId(), branchName, ref);
    }

    /**
     * 项目id获取分支
     *
     * @param projectId  项目id
     * @param branchName 分支名称
     * @return /
     */
    public Branch describeBranchByProjectId(Long projectId, String branchName) {
        try (GitLabApi client = gitlabAuthAdmin.auth()) {
            return client.getRepositoryApi().getBranch(projectId, branchName);
        } catch (Exception e) {
            log.error("根据projectId获取分支失败", e);
            throw new BadRequestException("根据projectId获取分支失败");
        }
    }

    /**
     * 项目名称获取分支
     *
     * @param projectName 项目名称
     * @param branchName  分支名称
     * @return /
     */
    public Branch describeBranchByProjectName(String projectName, String branchName) {
        Project project = gitlabProjectRepository.describeProjectByProjectName(projectName);
        if (project == null) {
            throw new BadRequestException(String.format("没有找到项目%s，请先去创建项目", projectName));
        }
        return describeBranchByProjectId(project.getId(), branchName);
    }

    /**
     * 关键字分页获取分支
     *
     * @param projectId 项目id
     * @param searchKey 关键字
     * @return /
     */
    public List<Branch> searchBranchesByProjectId(Long projectId, String searchKey) {
        List<Branch> list = new ArrayList<>();
        try (GitLabApi client = gitlabAuthAdmin.auth()) {
            Pager<Branch> pager = client.getRepositoryApi().getBranches(projectId, searchKey, 100);
            while (pager.hasNext()) {
                list.addAll(pager.next());
            }
        } catch (Exception e) {
            log.error("获取分支列表失败", e);
        }
        return list;
    }

    /**
     * 关键字分页获取分支
     *
     * @param projectName 项目名称
     * @param searchKey   关键字
     * @return /
     */
    public List<Branch> searchBranchesByProjectName(String projectName, String searchKey) {
        Project project = gitlabProjectRepository.describeProjectByProjectName(projectName);
        if (project == null) {
            throw new BadRequestException(String.format("没有找到项目%s，请先去创建项目", projectName));
        }
        return searchBranchesByProjectId(project.getId(), searchKey);
    }

    /**
     * 项目id删除分支
     *
     * @param projectId  项目id
     * @param branchName 分支名称
     */
    public void deleteBranchByProjectId(Long projectId, String branchName) {
        try (GitLabApi client = gitlabAuthAdmin.auth()) {
            client.getRepositoryApi().deleteBranch(projectId, branchName);
        } catch (Exception e) {
            log.error("根据projectId删除分支失败", e);
        }
    }

    /**
     * 项目名称删除分支
     *
     * @param projectName 项目名称
     * @param branchName  分支名称
     */
    public void deleteBranchByProjectName(String projectName, String branchName) {
        Project project = gitlabProjectRepository.describeProjectByProjectName(projectName);
        if (project == null) {
            throw new BadRequestException(String.format("没有找到项目%s，请先去创建项目", projectName));
        }
        deleteBranchByProjectId(project.getId(), branchName);
    }

    /**
     * 比较分支(合并分支之前, 展示改动的内容)
     *
     * @param projectId    项目id
     * @param sourceBranch 来源分支
     * @param targetBranch 目标分支
     * @return /
     */
    public CompareResults compareBranchByProjectId(Long projectId, String sourceBranch, String targetBranch) {
        try (GitLabApi client = gitlabAuthAdmin.auth()) {
            // 比较两个分支
            CompareResults compareResults = client.getRepositoryApi().compare(projectId, sourceBranch, targetBranch);
            // 输出差异信息
            System.out.println("Commits:");
            for (Commit commit : compareResults.getCommits()) {
                System.out.println("  " + commit.getId() + " - " + commit.getMessage());
            }
            System.out.println("Diffs:");
            for (Diff diff : compareResults.getDiffs()) {
                System.out.println("  " + diff.getOldPath() + " -> " + diff.getNewPath());
            }
            return compareResults;
        } catch (Exception e) {
            log.error("根据projectId比较分支差异失败", e);
            throw new BadRequestException("根据projectId比较分支差异失败");
        }
    }

    /**
     * 比较分支(合并分支之前, 展示改动的内容)
     *
     * @param projectName  项目名称
     * @param sourceBranch 源分支
     * @param targetBranch 目标分支
     * @return /
     */
    public CompareResults compareBranchByProjectName(String projectName, String sourceBranch, String targetBranch) {
        Project project = gitlabProjectRepository.describeProjectByProjectName(projectName);
        if (project == null) {
            throw new BadRequestException(String.format("没有找到项目%s，请先去创建项目", projectName));
        }
        return compareBranchByProjectId(project.getId(), sourceBranch, targetBranch);
    }
}
