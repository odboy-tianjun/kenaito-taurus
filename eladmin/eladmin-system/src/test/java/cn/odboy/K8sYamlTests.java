/*
 *  Copyright 2021-2025 Tian Jun
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package cn.odboy;

import cn.hutool.core.lang.Dict;
import cn.hutool.extra.template.Template;
import cn.hutool.extra.template.TemplateConfig;
import cn.hutool.extra.template.TemplateEngine;
import cn.hutool.extra.template.TemplateUtil;
import cn.odboy.model.request.K8sIngress;
import cn.odboy.model.request.K8sNamespace;
import cn.odboy.model.request.K8sService;
import cn.odboy.model.request.K8sStatefulSet;
import cn.odboy.repository.K8sIngressRepository;
import cn.odboy.repository.K8sNamespaceRepository;
import cn.odboy.repository.K8sServiceRepository;
import cn.odboy.repository.K8sStatefulSetRepository;
import cn.odboy.repository.openkruise.K8sOpenKruiseStatefulSetRepository;
import cn.odboy.util.K8sResourceNameUtil;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * k8sYaml载入 测试
 *
 * @author odboy
 * @date 2025-01-16
 */
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
        String envCode = "daily";
        String clusterCode = "local";
        String serviceName = K8sResourceNameUtil.getServiceName(appName, envCode);
        TemplateEngine engine = TemplateUtil.createEngine(new TemplateConfig("templates/v20250113", TemplateConfig.ResourceMode.CLASSPATH));
        Template template = engine.getTemplate("Service.yaml");
        String content = template.render(Dict.create()
                .set("appName", appName)
                .set("envCode", envCode)
                .set("serviceName", serviceName)
        );
        K8sService.LoadFromYamlArgs loadFromYamlArgs = new K8sService.LoadFromYamlArgs();
        loadFromYamlArgs.setClusterCode(clusterCode);
        loadFromYamlArgs.setYamlContent(content);
        k8sServiceRepository.loadServiceFromYaml(loadFromYamlArgs);
    }

    @Autowired
    private K8sIngressRepository k8sIngressRepository;

    @Test
    @SneakyThrows
    public void testIngress() {
        String appName = "demo";
        String clusterCode = "local";
        String envCode = "daily";
        String serviceName = K8sResourceNameUtil.getServiceName(appName, envCode);
        TemplateEngine engine = TemplateUtil.createEngine(new TemplateConfig("templates/v20250113", TemplateConfig.ResourceMode.CLASSPATH));
        Template template = engine.getTemplate("Ingress.yaml");
        String content = template.render(Dict.create()
                .set("appName", appName)
                .set("envCode", envCode)
                .set("serviceName", serviceName)
                .set("hostname", "kenaito-demo.odboy.com")
        );
        K8sIngress.LoadFromYamlArgs loadFromYamlArgs = new K8sIngress.LoadFromYamlArgs();
        loadFromYamlArgs.setClusterCode(clusterCode);
        loadFromYamlArgs.setYamlContent(content);
        k8sIngressRepository.loadIngressFromYaml(loadFromYamlArgs);
    }

    @Autowired
    private K8sStatefulSetRepository k8sStatefulSetRepository;

    @Test
    @SneakyThrows
    public void testStatefulSet() {
        String appName = "demo";
        String clusterCode = "local";
        String envCode = "daily";
        String podName = K8sResourceNameUtil.getPodName(appName, envCode);
        Integer replicas = 2;
        String appVersion = "online_20250116";
        TemplateEngine engine = TemplateUtil.createEngine(new TemplateConfig("templates/v20250113", TemplateConfig.ResourceMode.CLASSPATH));
        Template template = engine.getTemplate("StatefulSet.yaml");
        String content = template.render(Dict.create()
                .set("appName", appName)
                .set("envCode", envCode)
                .set("podName", podName)
                .set("replicas", replicas)
                .set("appVersion", appVersion)
        );
        K8sStatefulSet.LoadFromYamlArgs loadFromYamlArgs = new K8sStatefulSet.LoadFromYamlArgs();
        loadFromYamlArgs.setClusterCode(clusterCode);
        loadFromYamlArgs.setYamlContent(content);
        k8sStatefulSetRepository.loadStatefulSetFromYaml(loadFromYamlArgs);
    }

    @Autowired
    private K8sOpenKruiseStatefulSetRepository k8sOpenKruiseStatefulSetRepository;

    @Test
    @SneakyThrows
    public void testKruiseStatefulSet() {
        String appName = "demo";
        String clusterCode = "local";
        String envCode = "daily";
        String podName = K8sResourceNameUtil.getPodName(appName, envCode);
        Integer replicas = 2;
        String appVersion = "online_20250116";
        TemplateEngine engine = TemplateUtil.createEngine(new TemplateConfig("templates/v20250113", TemplateConfig.ResourceMode.CLASSPATH));
        Template template = engine.getTemplate("KruiseStatefulSet.yaml");
        String content = template.render(Dict.create()
                .set("appName", appName)
                .set("envCode", envCode)
                .set("podName", podName)
                .set("replicas", replicas)
                .set("appVersion", appVersion)
        );
        K8sStatefulSet.LoadFromYamlArgs loadFromYamlArgs = new K8sStatefulSet.LoadFromYamlArgs();
        loadFromYamlArgs.setClusterCode(clusterCode);
        loadFromYamlArgs.setYamlContent(content);
        k8sOpenKruiseStatefulSetRepository.loadStatefulSetFromYaml(loadFromYamlArgs);
    }
}
