package me.zhengjie.context;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "k8s")
public class K8sProperties {
    private boolean debug;
}
