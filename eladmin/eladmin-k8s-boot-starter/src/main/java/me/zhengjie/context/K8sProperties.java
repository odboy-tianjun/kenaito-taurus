package me.zhengjie.context;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * k8s配置
 *
 * @author odboy
 * @date 2025-01-13
 */
@Data
@ConfigurationProperties(prefix = "k8s")
public class K8sProperties {
    private boolean debug;
}
