package cn.odboy.model;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.gitlab4j.api.models.Changes;
import org.gitlab4j.api.models.Commit;
import org.gitlab4j.api.models.Project;
import org.gitlab4j.api.models.Repository;

import java.util.List;

/**
 * Gitlab 系统钩子 事件回调
 *
 * @author odboy
 * @date 2025-01-17
 */
public class GitlabSystemHook {
    /**
     * 仓库更新事件
     */
    @Data
    @EqualsAndHashCode(callSuper = false)
    public static class ProjectUpdateEvent extends MyObject {
        /**
         * eg. http://example.com/avatar/user.png
         */
        @JSONField(name = "user_avatar")
        private String userAvatar;
        /**
         * eg. test@example.com
         */
        @JSONField(name = "user_email")
        private String userEmail;
        /**
         * eg. 10
         */
        @JSONField(name = "user_id")
        private Integer userId;
        /**
         * eg. 40
         */
        @JSONField(name = "project_id")
        private Integer projectId;
        /**
         * eg. ["refs/heads/master"]
         */
        @JSONField(name = "refs")
        private List<String> refs;
        /**
         * eg. john.doe
         */
        @JSONField(name = "user_name")
        private String userName;
        /**
         * eg. [{"ref":"refs/heads/master","before":"8205ea8d81ce0c6b90fbe8280d118cc9fdad6130","after":"4045ea7a3df38697b3730a20fb73c8bed8a3e69e"}]
         */
        @JSONField(name = "changes")
        private List<Changes> changes;
        /**
         * repository_update
         */
        @JSONField(name = "event_name")
        private String eventName;
    }

    /**
     * 推送提交到项目
     */
    @Data
    @EqualsAndHashCode(callSuper = false)
    public static class PushCommitsToProjectEvent extends MyObject {
        /**
         * eg. https://s.gravatar.com/avatar/d4c74594d841139328695756648b6bd6?s=8://s.gravatar.com/avatar/d4c74594d841139328695756648b6bd6?s=80
         */
        @JSONField(name = "user_avatar")
        private String userAvatar;
        /**
         * eg. john@example.com
         */
        @JSONField(name = "user_email")
        private String userEmail;
        /**
         * eg. 1
         */
        @JSONField(name = "total_commits_count")
        private Integer totalCommitsCount;
        /**
         * eg. 95790bf891e76fee5e1747ab589903a6a1f80f22
         */
        @JSONField(name = "before")
        private String before;
        /**
         * eg. John Smith
         */
        @JSONField(name = "user_name")
        private String userName;
        /**
         * eg. {"ci":{"skip":true}}
         */
        @JSONField(name = "push_options")
        private JSONObject pushOptions;
        /**
         * eg. da1560886d4f094c3e6c9ef40349f7d38b5d27d7
         */
        @JSONField(name = "checkout_sha")
        private String checkoutSha;
        /**
         * {
         * "web_url": "http://test.example.com/gitlab/gitlab",
         * "avatar_url": "https://s.gravatar.com/avatar/d4c74594d841139328695756648b6bd6?s=8://s.gravatar.com/avatar/d4c74594d841139328695756648b6bd6?s=80",
         * "path_with_namespace": "gitlab/gitlab",
         * "name": "gitlab",
         * "namespace": "gitlab",
         * "description": "",
         * "visibility_level": 0,
         * "default_branch": "master",
         * "id": 15,
         * "git_http_url": "http://test.example.com/gitlab/gitlab.git",
         * "git_ssh_url": "git@test.example.com:gitlab/gitlab.git"
         * }
         */
        @JSONField(name = "project")
        private Project project;
        /**
         * eg. true
         */
        @JSONField(name = "ref_protected")
        private boolean refProtected;
        /**
         * eg. Hello World
         */
        @JSONField(name = "message")
        private String message;
        /**
         * eg. push
         */
        @JSONField(name = "object_kind")
        private String objectKind;
        /**
         * eg. refs/heads/master
         */
        @JSONField(name = "ref")
        private String ref;
        /**
         * eg. 4
         */
        @JSONField(name = "user_id")
        private int userId;
        /**
         * eg. 15
         */
        @JSONField(name = "project_id")
        private int projectId;
        /**
         * eg. push
         */
        @JSONField(name = "event_name")
        private String eventName;
        /**
         * {
         * "author": {
         * "name": "Test User",
         * "email": "test@example.com"
         * },
         * "id": "c5feabde2d8cd023215af4d2ceeb7a64839fc428",
         * "message": "Add simple search to projects in public area\n\ncommit message body",
         * "title": "Add simple search to projects in public area",
         * "url": "https://test.example.com/gitlab/gitlab/-/commit/c5feabde2d8cd023215af4d2ceeb7a64839fc428",
         * "timestamp": "2013-05-13T18:18:08+00:00"    * 	}
         */
        @JSONField(name = "commits")
        private List<Commit> commits;
        /**
         * eg. da1560886d4f094c3e6c9ef40349f7d38b5d27d7
         */
        @JSONField(name = "after")
        private String after;
    }

    /**
     * 发起合并分支请求
     */
    @Data
    @EqualsAndHashCode(callSuper = false)
    public static class MergeRequestEvent extends MyObject {
        /**
         * eg. merge_request
         */
        @JSONField(name = "event_type")
        private String eventType;
        @JSONField(name = "object_attributes")
        private org.gitlab4j.api.webhook.MergeRequestEvent.ObjectAttributes objectAttributes;
        @JSONField(name = "changes")
        private Changes changes;
        @JSONField(name = "project")
        private Project project;
        @JSONField(name = "repository")
        private Repository repository;
        @JSONField(name = "user")
        private org.gitlab4j.api.models.User user;
        @JSONField(name = "object_kind")
        private String objectKind;
        @JSONField(name = "labels")
        private List<String> labels;
    }
}
