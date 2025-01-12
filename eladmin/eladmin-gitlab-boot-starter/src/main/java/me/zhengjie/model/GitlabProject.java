package me.zhengjie.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * Gitlab项目
 *
 * @author odboy
 * @date 2024-09-09
 */
public class GitlabProject {
    @Data
    public static class CreateArgs {
        /**
         * 项目中文名称
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
    @EqualsAndHashCode(callSuper = true)
    public static class CreateResp extends CreateArgs {
        /**
         * OwnerId
         */
        private Long creatorId;
        /**
         * 创建时间
         */
        private Date createdAt;
        /**
         * 默认分支
         */
        private String defaultBranch;
        /**
         * git clone 地址
         */
        private String httpUrlToRepo;
        /**
         * 项目id
         */
        private Long projectId;
        /**
         * 可见级别
         */
        private String visibility;
        /**
         * 项目地址
         */
        private String homeUrl;
    }
}
