package me.zhengjie.model;

import lombok.Data;

/**
 * Gitlab流水线
 *
 * @author odboy
 * @date 2024-11-15
 */
public class GitlabPipeline {
    @Data
    public static class CreateArgs {
        /**
         * 应用模块名称
         */
        private String name;
        /**
         * 项目英文名称
         */
        private String appName;
        /**
         * 项目描述
         */
        private String description;
    }

    @Data
    public static class CreateResp {
    }
}
