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

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.odboy.infra.exception.BadRequestException;
import cn.odboy.repository.GitlabPipelineJobRepository;
import cn.odboy.repository.GitlabPipelineRepository;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.gitlab4j.api.models.Job;
import org.gitlab4j.api.models.JobStatus;
import org.gitlab4j.api.models.Pipeline;
import org.gitlab4j.api.models.PipelineStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class GitlabPipelineTests {
    @Autowired
    private GitlabPipelineRepository gitlabPipelineRepository;
    @Autowired
    private GitlabPipelineJobRepository gitlabPipelineJobRepository;

    /**
     * 创建pipeline
     */
    @Test
    public void testCreatePipeline() {
        Pipeline pipeline = gitlabPipelineRepository.createPipelineByProjectName("eladmin", "master", null);
        System.err.println(pipeline);
        // PipelineStatus 这里面的枚举是按顺序排列的
        System.err.println(pipeline.getStatus());
    }

    /**
     * 获取pipeline详情
     */
    @Test
    public void testDescribePipeline() {
        long pipelineId = 23L;
        Pipeline pipeline = gitlabPipelineRepository.describePipelineByProjectName("eladmin", pipelineId);
        System.err.println(pipeline);
    }

    /**
     * 流水线运行流程
     *
     * @return
     */
    @Test
    @SneakyThrows
    public void testRunPipelineFailed() {
        String projectName = "eladmin";
        String ref = "master";
        Map<String, String> variables = new HashMap<>();
        variables.put("appname", "eladmin");
        variables.put("versioncode", "202501172000");
        variables.put("envcode", "online");
        runPipeline(projectName, ref, variables);
    }

    @Test
    @SneakyThrows
    public void testRunPipelineSuccess() {
        String projectName = "eladmin";
        String ref = "master";
        Map<String, String> variables = new HashMap<>();
        variables.put("appname", "eladmin-system");
        variables.put("versioncode", "202501172000");
        variables.put("envcode", "online");
        runPipeline(projectName, ref, variables);
    }

    private void runPipeline(String projectName, String ref, Map<String, String> variables) throws Exception {
        int maxRetryCnt = 60;
        // =================================================================== 流水线任务开始
        // 拉起前检查是否有未结束的任务
        List<Pipeline> pipelines = gitlabPipelineRepository.listPipelines(projectName);
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
                    gitlabPipelineRepository.stopPipelineByProjectId(waitStopPipeline.getProjectId(), waitStopPipeline.getId());
                    log.info("流水线停止成功");
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
                try {
                    gitlabPipelineRepository.deletePipelineByProjectId(waitStopPipeline.getProjectId(), waitStopPipeline.getId());
                    log.info("流水线删除成功");
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            }
        }
        Pipeline pipeline = gitlabPipelineRepository.createPipelineByProjectName(projectName, ref, variables);
        Pipeline currentPipeline;
        while (true) {
            ThreadUtil.safeSleep(2000);
            currentPipeline = gitlabPipelineRepository.describePipelineByProjectId(pipeline.getProjectId(), pipeline.getId());
            if (PipelineStatus.PENDING.equals(currentPipeline.getStatus())) {
                if (maxRetryCnt <= 0) {
                    log.info("已达最大重试次数，删除流水线------------------------------------------------------");
                    gitlabPipelineRepository.deletePipelineByProjectId(currentPipeline.getProjectId(), currentPipeline.getId());
                    throw new BadRequestException("流水线执行失败，已达最大重试次数");
                }
                maxRetryCnt--;
                log.info("等待Runner资源中...");
                continue;
            }
            if (PipelineStatus.RUNNING.equals(currentPipeline.getStatus())) {
                log.info("流水线运行中------------------------------------------------------");
                showJobInfo(currentPipeline);
                continue;
            }
            if (PipelineStatus.SUCCESS.equals(currentPipeline.getStatus())) {
                log.info("流水线执行成功------------------------------------------------------");
                showJobInfo(currentPipeline);
                break;
            }
            if (PipelineStatus.FAILED.equals(currentPipeline.getStatus())) {
                log.info("流水线执行失败------------------------------------------------------");
                showJobInfo(currentPipeline);
                throw new BadRequestException("流水线执行失败");
            }
            if (PipelineStatus.CANCELED.equals(currentPipeline.getStatus())) {
                log.info("流水线被手动停止------------------------------------------------------");
                gitlabPipelineRepository.deletePipelineByProjectId(currentPipeline.getProjectId(), currentPipeline.getId());
                throw new BadRequestException("流水线执行失败，被手动停止");
            }
        }
    }

    private void showJobInfo(Pipeline currentPipeline) {
        List<Job> jobs = gitlabPipelineJobRepository.listJobsByProjectId(currentPipeline.getProjectId(), currentPipeline.getId());
        log.info("-------------------------jobs content start----------------------------------");
        for (Job job : jobs) {
            log.info("id={}", job.getId());
            log.info("createdAt={}", DateUtil.formatDateTime(job.getCreatedAt()));
            log.info("startedAt={}", DateUtil.formatDateTime(job.getStartedAt()));
            log.info("duration={}", job.getDuration());
            log.info("status={}", job.getStatus());
            log.info("webUrl={}", job.getWebUrl());
            if (JobStatus.RUNNING.equals(job.getStatus())) {
                log.info("stage={}执行中", job.getStage());
            } else if (JobStatus.FAILED.equals(job.getStatus())) {
                log.info("stage={}执行失败", job.getStage());
            } else if (JobStatus.SUCCESS.equals(job.getStatus())) {
                log.info("stage={}执行成功", job.getStage());
            }
        }
        log.info("-------------------------jobs content end------------------------------------");
    }
}
