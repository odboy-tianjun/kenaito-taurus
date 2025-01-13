package me.zhengjie.context;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * dingtalk支持
 *
 * @author odboy
 * @date 2025-01-13
 */
@Configuration
@EnableConfigurationProperties(DingtalkProperties.class)
@ConditionalOnClass(DingtalkAuthAdmin.class)
public class DingtalkAutoConfiguration {
    @Bean
    public DingtalkAuthAdmin dingtalkAuthAdmin() {
        return new DingtalkAuthAdmin();
    }
}
