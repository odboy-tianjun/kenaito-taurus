package cn.odboy.callback;

import cn.odboy.model.GitlabPipelineJob;

/**
 * 流水线任务执行回调
 *
 * @author odboy
 * @date 2025-01-17
 */
public interface GitlabPipelineJobExecuteCallback {
    void execute(GitlabPipelineJob.Info info);
}
