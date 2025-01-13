package me.zhengjie;

import cn.hutool.core.lang.Dict;
import cn.hutool.extra.template.Template;
import cn.hutool.extra.template.TemplateConfig;
import cn.hutool.extra.template.TemplateEngine;
import cn.hutool.extra.template.TemplateUtil;
import lombok.SneakyThrows;
import me.zhengjie.context.K8sConfigHelper;
import me.zhengjie.model.K8sNamespace;
import me.zhengjie.repository.K8sNamespaceRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class K8sYamlTests {
    @Autowired
    private K8sConfigHelper k8sConfigHelper;
    @Autowired
    private K8sNamespaceRepository k8sNamespaceRepository;

    @Test
    @SneakyThrows
    public void contextLoads() {
        String appName = "demo";
        TemplateEngine engine = TemplateUtil.createEngine(new TemplateConfig("templates/v20250113", TemplateConfig.ResourceMode.CLASSPATH));
        Template template = engine.getTemplate("Namespace.yaml");
        String content = template.render(Dict.create().set("appName", appName));
        System.err.println(content);
        K8sNamespace.LoadFromYamlArgs loadFromYamlArgs = new K8sNamespace.LoadFromYamlArgs();
        loadFromYamlArgs.setClusterCode("local");
        loadFromYamlArgs.setAppName(appName);
        loadFromYamlArgs.setYamlContent(content);
        k8sNamespaceRepository.loadNamespaceFromYaml(loadFromYamlArgs);
    }

    public static void main(String[] args) {
    }
}

