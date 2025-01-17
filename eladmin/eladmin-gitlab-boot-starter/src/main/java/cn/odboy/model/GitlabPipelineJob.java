package cn.odboy.model;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.gitlab4j.api.models.JobStatus;

import java.util.Date;

/**
 * 流水线任务
 *
 * @author odboy
 * @date 2025-01-17
 */
public class GitlabPipelineJob {
    @Data
    @Builder
    @EqualsAndHashCode(callSuper = false)
    public static class Info extends MyObject {
        private Long id;
        /**
         * 创建时间
         */
        private Date createdAt;
        /**
         * 启用时间
         */
        private Date startedAt;
        /**
         * 执行耗时，单位秒
         */
        private Float duration;
        /**
         * 任务状态
         */
        private JobStatus status;
        /**
         * 任务执行过程url
         */
        private String webUrl;
    }
}
