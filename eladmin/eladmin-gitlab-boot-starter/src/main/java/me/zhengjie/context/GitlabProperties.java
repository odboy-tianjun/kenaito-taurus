package me.zhengjie.context;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "gitlab")
public class GitlabProperties {
    /**
     * gitlab地址
     * 所需权限：api、read_api、read_user、create_runner、manage_runner、read_repository、write_repository
     * v14.8.2 http://192.168.235.100:20080/-/profile/personal_access_tokens
     * v17.x http://192.168.235.100:20080/-/user_settings/personal_access_tokens
     * 默认分支
     * v17.x http://192.168.235.100:20080/admin/application_settings/repository#js-default-branch-name
     */
    private String url;
    /**
     * root用户的令牌
     */
    private String accessToken;
}
