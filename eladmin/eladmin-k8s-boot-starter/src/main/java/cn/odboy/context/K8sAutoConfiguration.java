package cn.odboy.context;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * k8s支持
 *
 * @author odboy
 * @date 2025-01-13
 */
@Configuration
@EnableConfigurationProperties(K8sProperties.class)
@ConditionalOnClass(K8sConfigHelper.class)
public class K8sAutoConfiguration {
    @Bean
    public K8sConfigHelper k8sConfigHelper() {
        return new K8sConfigHelper();
    }
}
