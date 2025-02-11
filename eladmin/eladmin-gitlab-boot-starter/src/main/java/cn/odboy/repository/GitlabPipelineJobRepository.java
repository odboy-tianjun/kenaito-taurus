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
import org.gitlab4j.api.models.Job;
import org.gitlab4j.api.models.Project;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Gitlab流水线任务
 *
 * @author odboy
 * @date 2024-11-17
 */
@Slf4j
@Component
public class GitlabPipelineJobRepository {
    @Autowired
    private GitlabAuthAdmin gitlabAuthAdmin;
    @Autowired
    private GitlabProjectRepository gitlabProjectRepository;

    /**
     * 获取任务列表
     *
     * @param projectId  项目id
     * @param pipelineId 流水线id
     * @return /
     */
    public List<Job> listJobsByProjectId(Long projectId, Long pipelineId) {
        try (GitLabApi auth = gitlabAuthAdmin.auth()) {
            return auth.getJobApi().getJobsForPipeline(projectId, pipelineId);
        } catch (Exception e) {
            log.info("获取任务列表失败", e);
            throw new BadRequestException("获取任务列表失败");
        }
    }

    /**
     * 获取任务列表
     *
     * @param projectName 项目名称
     * @param pipelineId  流水线id
     * @return /
     */
    public List<Job> listJobsByProjectName(String projectName, Long pipelineId) {
        Project project = gitlabProjectRepository.describeProjectByProjectName(projectName);
        if (project == null) {
            throw new BadRequestException(String.format("没有找到项目%s，请先去创建项目", projectName));
        }
        return listJobsByProjectId(project.getId(), pipelineId);
    }

    /**
     * 获取任务详情
     *
     * @param projectId 项目id
     * @param jobId     任务id
     * @return /
     */
    public Job describeJobsByProjectId(Long projectId, Long jobId) {
        try (GitLabApi auth = gitlabAuthAdmin.auth()) {
            return auth.getJobApi().getJob(projectId, jobId);
        } catch (Exception e) {
            log.info("获取任务详情失败", e);
            throw new BadRequestException("获取任务详情失败");
        }
    }

    /**
     * 获取任务详情
     *
     * @param projectName 项目名称
     * @param jobId       任务id
     * @return /
     */
    public Job describeJobsByProjectName(String projectName, Long jobId) {
        Project project = gitlabProjectRepository.describeProjectByProjectName(projectName);
        if (project == null) {
            throw new BadRequestException(String.format("没有找到项目%s，请先去创建项目", projectName));
        }
        return describeJobsByProjectId(project.getId(), jobId);
    }
}
