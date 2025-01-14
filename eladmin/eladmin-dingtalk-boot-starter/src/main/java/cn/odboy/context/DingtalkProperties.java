package cn.odboy.context;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "dingtalk")
public class DingtalkProperties {
    /**
     * 原企业内部应用AgentId
     */
    private String agentId;
    /**
     * Client ID (原 AppKey 和 SuiteKey)
     */
    private String appKey;
    /**
     * Client Secret (原 AppSecret 和 SuiteSecret)
     */
    private String appSecret;
    /**
     * 应用内机器人代码
     */
    private String robotCode;
}
