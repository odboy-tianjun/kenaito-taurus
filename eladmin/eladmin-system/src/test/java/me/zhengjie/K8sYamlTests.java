package me.zhengjie;

import cn.hutool.core.lang.Dict;
import cn.hutool.extra.template.Template;
import cn.hutool.extra.template.TemplateConfig;
import cn.hutool.extra.template.TemplateEngine;
import cn.hutool.extra.template.TemplateUtil;
import lombok.SneakyThrows;
import me.zhengjie.model.request.K8sNamespace;
import me.zhengjie.model.request.K8sService;
import me.zhengjie.repository.K8sNamespaceRepository;
import me.zhengjie.repository.K8sServiceRepository;
import me.zhengjie.util.K8sResourceNameUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class K8sYamlTests {
    @Autowired
    private K8sNamespaceRepository k8sNamespaceRepository;

    @Test
    @SneakyThrows
    public void testNamespace() {
        String appName = "demo";
        String clusterCode = "local";
        TemplateEngine engine = TemplateUtil.createEngine(new TemplateConfig("templates/v20250113", TemplateConfig.ResourceMode.CLASSPATH));
        Template template = engine.getTemplate("Namespace.yaml");
        String content = template.render(Dict.create().set("appName", appName));
        System.err.println(content);
        K8sNamespace.LoadFromYamlArgs loadFromYamlArgs = new K8sNamespace.LoadFromYamlArgs();
        loadFromYamlArgs.setClusterCode(clusterCode);
        loadFromYamlArgs.setYamlContent(content);
        k8sNamespaceRepository.loadNamespaceFromYaml(loadFromYamlArgs);
    }

    @Autowired
    private K8sServiceRepository k8sServiceRepository;

    @Test
    @SneakyThrows
    public void testService() {
        String appName = "demo";
        String appEnv = "daily";
        String clusterCode = "local";
        String serviceName = K8sResourceNameUtil.getServiceName(appName, appEnv);
        TemplateEngine engine = TemplateUtil.createEngine(new TemplateConfig("templates/v20250113", TemplateConfig.ResourceMode.CLASSPATH));
        Template template = engine.getTemplate("Service.yaml");
        String content = template.render(Dict.create()
                .set("appName", appName)
                .set("appEnv", appEnv)
                .set("serviceName", serviceName)
        );
        System.err.println(content);
        K8sService.LoadFromYamlArgs loadFromYamlArgs = new K8sService.LoadFromYamlArgs();
        loadFromYamlArgs.setClusterCode(clusterCode);
        loadFromYamlArgs.setYamlContent(content);
        k8sServiceRepository.loadServiceFromYaml(loadFromYamlArgs);
    }
}

