package me.zhengjie.context;

import org.gitlab4j.api.GitLabApi;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * gitlab 客户端认证
 *
 * @author odboy
 * @date 2025-01-12
 */
public class GitlabAuthRepository {
    @Autowired
    private GitlabProperties properties;

    public GitLabApi auth() {
        return new GitLabApi(properties.getUrl(), properties.getAccessToken());
    }
}
