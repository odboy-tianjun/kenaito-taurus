package cn.odboy;

import cn.odboy.constant.K8sEnvEnum;
import cn.odboy.model.request.*;
import cn.odboy.model.response.K8sResource;
import cn.odboy.repository.*;
import com.alibaba.fastjson.JSON;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.models.V1Deployment;
import io.kubernetes.client.openapi.models.V1NodeList;
import io.kubernetes.client.openapi.models.V1StatefulSet;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class K8sTests {
    @Autowired
    private K8sDeploymentRepository k8SDeploymentRepository;
    @Autowired
    private K8sServiceRepository k8sSvcRepository;
    @Autowired
    private K8sIngressRepository k8sIngressRepository;
    @Autowired
    private K8sStatefulSetRepository k8sStatefulSetRepository;
    @Autowired
    private K8sPodRepository k8sPodRepository;

    @Test
    public void listTest() throws ApiException {
        List<K8sResource.Pod> infos1 = k8sPodRepository.listPods(K8sEnvEnum.Daily.getCode(), "default", new HashMap<>());
        System.err.println(JSON.toJSONString(infos1));
        System.err.println("========================================");
        List<K8sResource.Pod> infos2 = k8sPodRepository.listPods(K8sEnvEnum.Daily.getCode(), "kube-system", new HashMap<>());
        System.err.println(JSON.toJSONString(infos2));
        System.err.println("========================================");
        List<K8sResource.Pod> infos3 = k8sPodRepository.listPods(K8sEnvEnum.Daily.getCode(), "kube-system", new HashMap<>() {{
            put("k8s-app", "calico-kube-controllers");
        }});
        System.err.println(JSON.toJSONString(infos3));
    }

    @Test
    public void list2Test() throws ApiException {
        Map<String, String> fieldSelector = new HashMap<>(1);
        fieldSelector.put("spec.nodeName", "192.168.235.103");
        List<K8sResource.Pod> infos1 = k8sPodRepository.listPods(K8sEnvEnum.Daily.getCode(), fieldSelector, new HashMap<>());
        System.err.println(JSON.toJSONString(infos1));
    }

    @Test
    public void createServiceTest() throws ApiException {
        k8sSvcRepository.createService(K8sService.CreateArgs.builder()
                .clusterCode(K8sEnvEnum.Daily.getCode())
                .appName("kenaito-devops")
                .labelSelector(null)
                .annotations(null)
                .dryRun(false)
                .port(80)
                .targetPort(12800)
                .build()
        );
    }

    @Test
    public void createIngressTest() throws ApiException {
        k8sIngressRepository.createIngress(K8sIngress.CreateArgs.builder()
                .clusterCode(K8sEnvEnum.Daily.getCode())
                .appName("kenaito-devops")
                .annotations(null)
                .hostname("kenaito-devops.odboy.com")
                .path("/")
                .serviceName("service-kenaito-devops")
                .servicePort(80)
                .dryRun(false)
                .build());
    }

    @Test
    public void createStatefulSetTest() throws ApiException {
        k8sStatefulSetRepository.createStatefulSet(K8sStatefulSet.CreateArgs.builder()
                .clusterCode(K8sEnvEnum.Daily.getCode())
                .annotations(null)
                .appName("kenaito-devops")
                .image("registry.cn-shanghai.aliyuncs.com/odboy/ops:redis-6.2.14-alpine3.20")
                .replicas(1)
                .requestCpuNum(1)
                .requestMemNum(1)
                .limitsCpuNum(1)
                .limitsMemNum(2)
                .port(12800)
                .dryRun(false)
                .build());
    }

    @Test
    public void changeStatefulSetImageTest() throws ApiException {
        V1StatefulSet v1StatefulSet = k8sStatefulSetRepository.changeStatefulSetImage(K8sStatefulSet.ChangeImageArgs.builder()
                .clusterCode(K8sEnvEnum.Daily.getCode())
                .appName("kenaito-devops")
                // 填写一个错误的镜像地址, 为了测是替换镜像功能
                // "registry.cn-shanghai.aliyuncs.com/odboy/ops:redis-6.2.14-alpine3.20"
                .newImage("registry.cn-shanghai.aliyuncs.com/odboy/ops:xxxredis-6.2.14-alpine3.20")
                .dryRun(false)
                .build());
        System.err.println(v1StatefulSet);
    }

    @Test
    public void changeStatefulSetImageV2Test() throws ApiException {
        V1StatefulSet v1StatefulSet = k8sStatefulSetRepository.changeStatefulSetImageV2(K8sStatefulSet.ChangeImageArgs.builder()
                .clusterCode(K8sEnvEnum.Daily.getCode())
                .appName("kenaito-devops")
                // 填写一个错误的镜像地址, 为了测是替换镜像功能
                // "registry.cn-shanghai.aliyuncs.com/odboy/ops:redis-6.2.14-alpine3.20"
                .newImage("registry.cn-shanghai.aliyuncs.com/odboy/ops:xxxredis-6.2.14-alpine3.20")
                .dryRun(false)
                .build());
        System.err.println(v1StatefulSet);
    }

    @Test
    public void changeStatefulSetReplicasTest() throws ApiException {
        V1StatefulSet v1StatefulSet = k8sStatefulSetRepository.changeStatefulSetReplicas(K8sStatefulSet.ChangeReplicasArgs.builder()
                .clusterCode(K8sEnvEnum.Daily.getCode())
                .appName("kenaito-devops")
                .newReplicas(4)
                .dryRun(false)
                .build());
        System.err.println(v1StatefulSet);
    }

    @Test
    public void changeStatefulSetPodSpecsTest() throws ApiException {
        V1StatefulSet v1StatefulSet = k8sStatefulSetRepository.changeStatefulSetSpecs(K8sStatefulSet.ChangeSpecsArgs.builder()
                .clusterCode(K8sEnvEnum.Daily.getCode())
                .appName("kenaito-devops")
                .requestCpuNum(1)
                .requestMemNum(1)
                .requestCpuNum(2)
                .requestMemNum(4)
                .dryRun(false)
                .build());
        System.err.println(v1StatefulSet);
    }

    @Test
    public void createDeploymentTest() throws ApiException {
        V1Deployment v1Deployment = k8SDeploymentRepository.createDeployment(K8sDeployment.CreateArgs.builder()
                .clusterCode(K8sEnvEnum.Daily.getCode())
                .annotations(null)
                .appName("kenaito-devops")
                .image("registry.cn-shanghai.aliyuncs.com/odboy/ops:redis-6.2.14-alpine3.20")
                .replicas(1)
                .port(6379)
                .dryRun(false)
                .build());
        System.err.println(v1Deployment);
    }

    @Autowired
    private K8sNodeRepository k8sNodeRepository;

    @Test
    public void listNode() {
        V1NodeList v1NodeList = k8sNodeRepository.listNodes(K8sEnvEnum.Daily.getCode());
        // 打印节点信息
        for (io.kubernetes.client.openapi.models.V1Node item : v1NodeList.getItems()) {
            System.err.println(item.getMetadata().getName());
        }
    }

    @Autowired
    private K8sNamespaceRepository k8sNamespaceRepository;

    @Test
    public void testCreateNamespace() {
        K8sResource.Namespace namespace = k8sNamespaceRepository.createNamespace(K8sNamespace.CreateArgs.builder()
                .clusterCode(K8sEnvEnum.Daily.getCode())
                .appName("test")
                .dryRun(false)
                .build());
        System.err.println(JSON.toJSONString(namespace));
    }
}

