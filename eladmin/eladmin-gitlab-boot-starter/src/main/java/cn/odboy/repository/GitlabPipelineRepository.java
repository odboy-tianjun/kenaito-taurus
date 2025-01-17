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

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.odboy.callback.GitlabPipelineJobExecuteCallback;
import cn.odboy.context.GitlabAuthAdmin;
import cn.odboy.infra.exception.BadRequestException;
import cn.odboy.model.GitlabPipelineJob;
import lombok.extern.slf4j.Slf4j;
import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Gitlab流水线
 *
 * @author odboy
 * @date 2024-11-17
 */
@Slf4j
@Component
public class GitlabPipelineRepository {
    @Autowired
    private GitlabAuthAdmin gitlabAuthAdmin;
    @Autowired
    private GitlabProjectRepository gitlabProjectRepository;
    @Autowired
    private GitlabPipelineJobRepository gitlabPipelineJobRepository;

    /**
     * 创建流水线
     *
     * @param projectId 项目id
     * @param ref       分支名称
     * @param variables 流水线变量，可在.gitlab-ci.yml文件中通过语法 $变量名称，获取变量值
     * @return /
     */
    public Pipeline createPipelineByProjectId(Long projectId, String ref, Map<String, String> variables) {
        try (GitLabApi client = gitlabAuthAdmin.auth()) {
            return client.getPipelineApi().createPipeline(projectId, ref, variables);
        } catch (Exception e) {
            log.error("创建流水线失败", e);
            throw new BadRequestException("创建流水线失败, " + e.getMessage());
        }
    }

    /**
     * 创建流水线
     *
     * @param projectName 项目名称
     * @param ref         分支名称
     * @param variables   流水线变量，可在.gitlab-ci.yml文件中通过语法 $变量名称，获取变量值
     * @return /
     */
    public Pipeline createPipelineByProjectName(String projectName, String ref, Map<String, String> variables) {
        Project project = gitlabProjectRepository.describeProjectByProjectName(projectName);
        if (project == null) {
            throw new BadRequestException(String.format("没有找到项目%s，请先去创建项目", projectName));
        }
        return createPipelineByProjectId(project.getId(), ref, variables);
    }

    /**
     * 获取流水线详情
     *
     * @param projectId  项目id
     * @param pipelineId 流水线id
     * @return /
     */
    public Pipeline describePipelineByProjectId(Long projectId, Long pipelineId) {
        try (GitLabApi auth = gitlabAuthAdmin.auth()) {
            return auth.getPipelineApi().getPipeline(projectId, pipelineId);
        } catch (Exception e) {
            log.info("获取流水线详情失败", e);
            throw new BadRequestException("获取流水线详情失败");
        }
    }

    /**
     * 获取流水线详情
     *
     * @param projectName 项目名称
     * @param pipelineId  流水线id
     * @return /
     */
    public Pipeline describePipelineByProjectName(String projectName, Long pipelineId) {
        Project project = gitlabProjectRepository.describeProjectByProjectName(projectName);
        if (project == null) {
            throw new BadRequestException(String.format("没有找到项目%s，请先去创建项目", projectName));
        }
        return describePipelineByProjectId(project.getId(), pipelineId);
    }

    /**
     * 获取流水线列表
     *
     * @param projectId 项目id
     * @return /
     */
    public List<Pipeline> listPipelines(Long projectId) {
        try (GitLabApi auth = gitlabAuthAdmin.auth()) {
            return auth.getPipelineApi().getPipelines(projectId);
        } catch (Exception e) {
            log.info("获取流水线列表失败", e);
            throw new BadRequestException("获取流水线列表失败");
        }
    }

    /**
     * 获取流水线列表
     *
     * @param projectName 项目名称
     * @return /
     */
    public List<Pipeline> listPipelines(String projectName) {
        Project project = gitlabProjectRepository.describeProjectByProjectName(projectName);
        if (project == null) {
            throw new BadRequestException(String.format("没有找到项目%s，请先去创建项目", projectName));
        }
        return listPipelines(project.getId());
    }

    /**
     * 停止流水线任务
     *
     * @param projectId  项目id
     * @param pipelineId 流水线id
     * @return /
     */
    public Pipeline stopPipelineByProjectId(Long projectId, Long pipelineId) {
        try (GitLabApi auth = gitlabAuthAdmin.auth()) {
            return auth.getPipelineApi().cancelPipelineJobs(projectId, pipelineId);
        } catch (Exception e) {
            log.info("停止流水线任务失败", e);
            throw new BadRequestException("停止流水线任务失败");
        }
    }

    /**
     * 停止流水线任务
     *
     * @param projectName 项目名称
     * @param pipelineId  流水线id
     * @return /
     */
    public Pipeline stopPipelineByProjectName(String projectName, Long pipelineId) {
        Project project = gitlabProjectRepository.describeProjectByProjectName(projectName);
        if (project == null) {
            throw new BadRequestException(String.format("没有找到项目%s，请先去创建项目", projectName));
        }
        return stopPipelineByProjectId(project.getId(), pipelineId);
    }

    /**
     * 重试流水线任务
     *
     * @param projectId  项目id
     * @param pipelineId 流水线id
     * @return /
     */
    public Pipeline retryPipelineJobByProjectId(Long projectId, Long pipelineId) {
        try (GitLabApi auth = gitlabAuthAdmin.auth()) {
            return auth.getPipelineApi().retryPipelineJob(projectId, pipelineId);
        } catch (Exception e) {
            log.info("重试流水线任务失败", e);
            throw new BadRequestException("重试流水线任务失败");
        }
    }

    /**
     * 重试流水线任务
     *
     * @param projectName 项目名称
     * @param pipelineId  流水线id
     * @return /
     */
    public Pipeline retryPipelineJobByProjectName(String projectName, Long pipelineId) {
        Project project = gitlabProjectRepository.describeProjectByProjectName(projectName);
        if (project == null) {
            throw new BadRequestException(String.format("没有找到项目%s，请先去创建项目", projectName));
        }
        return retryPipelineJobByProjectId(project.getId(), pipelineId);
    }

    /**
     * 删除流水线
     *
     * @param projectId  项目id
     * @param pipelineId 流水线id
     */
    public void deletePipelineByProjectId(Long projectId, Long pipelineId) {
        try (GitLabApi auth = gitlabAuthAdmin.auth()) {
            auth.getPipelineApi().deletePipeline(projectId, pipelineId);
        } catch (Exception e) {
            log.info("删除流水线失败", e);
            throw new BadRequestException("删除流水线失败");
        }
    }

    /**
     * 删除流水线
     *
     * @param projectName 项目名称
     * @param pipelineId  流水线id
     */
    public void deletePipelineByProjectName(String projectName, Long pipelineId) {
        Project project = gitlabProjectRepository.describeProjectByProjectName(projectName);
        if (project == null) {
            throw new BadRequestException(String.format("没有找到项目%s，请先去创建项目", projectName));
        }
        deletePipelineByProjectId(project.getId(), pipelineId);
    }

    /**
     * 执行流水线
     *
     * @param projectId 项目id
     * @param ref       分支名称
     * @param variables 流水线环境变量
     * @param callback  流水线回调
     * @return /
     * @throws Exception
     */
    public Pipeline executePipelineByProjectId(Long projectId, String ref, Map<String, String> variables, GitlabPipelineJobExecuteCallback callback) throws Exception {
        Project project = gitlabProjectRepository.describeProjectById(projectId);
        if (project == null) {
            throw new BadRequestException(String.format("没有找到项目id=%s，请先去创建项目", projectId));
        }
        int maxRetryCnt = 60;
        // =================================================================== 流水线任务开始
        // 拉起前检查是否有未结束的任务
        List<Pipeline> pipelines = listPipelines(projectId);
        List<Pipeline> waitStopPipelines = pipelines.stream().filter(f ->
                // RUNNING 正在运行中，尚未完成的
                PipelineStatus.RUNNING.equals(f.getStatus()) ||
                        // PENDING 正在等待资源的
                        PipelineStatus.PENDING.equals(f.getStatus()) ||
                        // CANCELED 被取消的
                        PipelineStatus.CANCELED.equals(f.getStatus()) ||
                        // MANUAL 手动停止的
                        PipelineStatus.MANUAL.equals(f.getStatus())
        ).collect(Collectors.toList());
        if (!waitStopPipelines.isEmpty()) {
            // 停止符合条件的流水线
            for (Pipeline waitStopPipeline : waitStopPipelines) {
                try {
                    stopPipelineByProjectId(waitStopPipeline.getProjectId(), waitStopPipeline.getId());
                    log.info("流水线停止成功");
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
                try {
                    deletePipelineByProjectId(waitStopPipeline.getProjectId(), waitStopPipeline.getId());
                    log.info("流水线删除成功");
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            }
        }
        Pipeline pipeline = createPipelineByProjectName(project.getName(), ref, variables);
        Pipeline currentPipeline;
        while (true) {
            ThreadUtil.safeSleep(2000);
            currentPipeline = describePipelineByProjectId(pipeline.getProjectId(), pipeline.getId());
            if (PipelineStatus.PENDING.equals(currentPipeline.getStatus())) {
                if (maxRetryCnt <= 0) {
                    log.info("已达最大重试次数，删除流水线------------------------------------------------------");
                    deletePipelineByProjectId(currentPipeline.getProjectId(), currentPipeline.getId());
                    throw new BadRequestException("流水线执行失败，已达最大重试次数");
                }
                maxRetryCnt--;
                log.info("等待Runner资源中...");
                continue;
            }
            if (PipelineStatus.RUNNING.equals(currentPipeline.getStatus())) {
                log.info("流水线运行中------------------------------------------------------");
                showJobInfo(currentPipeline, callback);
                continue;
            }
            if (PipelineStatus.SUCCESS.equals(currentPipeline.getStatus())) {
                log.info("流水线执行成功------------------------------------------------------");
                showJobInfo(currentPipeline, callback);
                return currentPipeline;
            }
            if (PipelineStatus.FAILED.equals(currentPipeline.getStatus())) {
                log.info("流水线执行失败------------------------------------------------------");
                showJobInfo(currentPipeline, callback);
                throw new BadRequestException("流水线执行失败");
            }
            if (PipelineStatus.CANCELED.equals(currentPipeline.getStatus())) {
                log.info("流水线被手动停止------------------------------------------------------");
                deletePipelineByProjectId(currentPipeline.getProjectId(), currentPipeline.getId());
                throw new BadRequestException("流水线执行失败，被手动停止");
            }
        }
    }

    /**
     * 执行流水线
     *
     * @param projectName 项目名称
     * @param ref         分支名称
     * @param variables   流水线环境变量
     * @param callback    流水线回调
     * @return /
     * @throws Exception
     */
    public Pipeline executePipelineByProjectName(String projectName, String ref, Map<String, String> variables, GitlabPipelineJobExecuteCallback callback) throws Exception {
        Project project = gitlabProjectRepository.describeProjectByProjectName(projectName);
        if (project == null) {
            throw new BadRequestException(String.format("没有找到项目%s，请先去创建项目", projectName));
        }
        return executePipelineByProjectId(project.getId(), ref, variables, callback);
    }

    private void showJobInfo(Pipeline currentPipeline, GitlabPipelineJobExecuteCallback callback) {
        List<Job> jobs = gitlabPipelineJobRepository.listJobsByProjectId(currentPipeline.getProjectId(), currentPipeline.getId());
        log.info("-------------------------jobs content start----------------------------------");
        for (Job job : jobs) {
            log.info("id={}", job.getId());
            log.info("createdAt={}", DateUtil.formatDateTime(job.getCreatedAt()));
            log.info("startedAt={}", DateUtil.formatDateTime(job.getStartedAt()));
            log.info("duration={}", job.getDuration());
            log.info("status={}", job.getStatus());
            log.info("webUrl={}", job.getWebUrl());
            if (JobStatus.PENDING.equals(job.getStatus())) {
                log.info("stage={}挂起中", job.getStage());
            } else if (JobStatus.RUNNING.equals(job.getStatus())) {
                log.info("stage={}执行中", job.getStage());
            } else if (JobStatus.FAILED.equals(job.getStatus())) {
                log.info("stage={}执行失败", job.getStage());
            } else if (JobStatus.SUCCESS.equals(job.getStatus())) {
                log.info("stage={}执行成功", job.getStage());
            }
            callback.execute(GitlabPipelineJob.Info.builder()
                    .id(job.getId())
                    .createdAt(job.getCreatedAt())
                    .startedAt(job.getStartedAt())
                    .duration(job.getDuration())
                    .status(job.getStatus())
                    .webUrl(job.getWebUrl())
                    .build());
        }
        log.info("-------------------------jobs content end------------------------------------");
    }
}
