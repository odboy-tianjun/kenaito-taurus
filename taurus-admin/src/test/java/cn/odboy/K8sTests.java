package cn.odboy;

import cn.odboy.constant.K8sEnvEnum;
import cn.odboy.infra.k8s.model.K8sResource;
import cn.odboy.infra.k8s.repository.*;
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
    private K8sSvcRepository k8sSvcRepository;
    @Autowired
    private K8sIngressRepository k8sIngressRepository;
    @Autowired
    private K8sStatefulSetRepository k8sStatefulSetRepository;
    @Autowired
    private K8sPodRepository k8sPodRepository;

    @Test
    public void listTest() throws ApiException {
        List<K8sResource.Pod> infos1 = k8sPodRepository.queryNamespaceList(K8sEnvEnum.Daily, "default", null);
        System.err.println(JSON.toJSONString(infos1));
        System.err.println("========================================");
        List<K8sResource.Pod> infos2 = k8sPodRepository.queryNamespaceList(K8sEnvEnum.Daily, "kube-system", null);
        System.err.println(JSON.toJSONString(infos2));
        System.err.println("========================================");
        List<K8sResource.Pod> infos3 = k8sPodRepository.queryNamespaceList(K8sEnvEnum.Daily, "kube-system", new HashMap<>() {{
            put("k8s-app", "calico-kube-controllers");
        }});
        System.err.println(JSON.toJSONString(infos3));
    }

    @Test
    public void list2Test() throws ApiException {
        Map<String, String> fieldSelector = new HashMap<>(1);
        fieldSelector.put("spec.nodeName", "192.168.235.103");
        List<K8sResource.Pod> infos1 = k8sPodRepository.queryList(K8sEnvEnum.Daily, fieldSelector, null);
        System.err.println(JSON.toJSONString(infos1));
    }

    @Test
    public void createServiceTest() throws ApiException {
        k8sSvcRepository.create(
                K8sEnvEnum.Daily,
                "kenaito-devops",
                "kenaito-devops",
                null,
                80,
                12800,
                null
        );
    }

    @Test
    public void createIngressTest() throws ApiException {
        k8sIngressRepository.create(
                K8sEnvEnum.Daily,
                "kenaito-devops",
                "kenaito-devops",
                null,
                "/",
                "kenaito-devops.odboy.com",
                "service-kenaito-devops",
                80
        );
    }

    @Test
    public void createStatefulSetTest() throws ApiException {
        k8sStatefulSetRepository.create(
                K8sEnvEnum.Daily,
                "kenaito-devops",
                "kenaito-devops",
                null,
                1,
                "registry.cn-shanghai.aliyuncs.com/odboy/ops:redis-6.2.14-alpine3.20",
                12800
        );
    }

    @Test
    public void changeStatefulSetImageTest() throws ApiException {
        V1StatefulSet v1StatefulSet = k8sStatefulSetRepository.changeImage(
                K8sEnvEnum.Daily,
                "kenaito-devops",
                "kenaito-devops",
// 填写一个错误的镜像地址, 为了测是替换镜像功能
//                "registry.cn-shanghai.aliyuncs.com/odboy/ops:redis-6.2.14-alpine3.20"
                "registry.cn-shanghai.aliyuncs.com/odboy/ops:xxxredis-6.2.14-alpine3.20"
        );
        System.err.println(v1StatefulSet);
    }

    @Test
    public void changeStatefulSetImageV2Test() throws ApiException {
        V1StatefulSet v1StatefulSet = k8sStatefulSetRepository.changeImageV2(
                K8sEnvEnum.Daily,
                "kenaito-devops",
                "kenaito-devops",
                "registry.cn-shanghai.aliyuncs.com/odboy/ops:redis-6.2.14-alpine3.20"
        );
        System.err.println(v1StatefulSet);
    }

    @Test
    public void changeStatefulSetReplicasTest() throws ApiException {
        V1StatefulSet v1StatefulSet = k8sStatefulSetRepository.changeReplicas(
                K8sEnvEnum.Daily,
                "kenaito-devops",
                "kenaito-devops",
                4
        );
        System.err.println(v1StatefulSet);
    }

    @Test
    public void changeStatefulSetPodSpecsTest() throws ApiException {
        V1StatefulSet v1StatefulSet = k8sStatefulSetRepository.changePodSpecs(
                K8sEnvEnum.Daily,
                "kenaito-devops",
                "kenaito-devops",
                1,
                3
        );
        System.err.println(v1StatefulSet);
    }

    @Test
    public void createDeploymentTest() throws ApiException {
        V1Deployment v1Deployment = k8SDeploymentRepository.createDeployment(
                K8sEnvEnum.Daily,
                "kenaito-devops",
                "kenaito-devops",
                null,
                "registry.cn-shanghai.aliyuncs.com/odboy/ops:redis-6.2.14-alpine3.20",
                1,
                6379
        );
        System.err.println(v1Deployment);
    }

    @Autowired
    private K8sNodeRepository k8sNodeRepository;

    @Test
    public void listNode() {
        V1NodeList v1NodeList = k8sNodeRepository.queryList(K8sEnvEnum.Daily);
        // 打印节点信息
        for (io.kubernetes.client.openapi.models.V1Node item : v1NodeList.getItems()) {
            System.err.println(item.getMetadata().getName());
        }
    }

    @Autowired
    private K8sNamespaceRepository k8sNamespaceRepository;

    @Test
    public void testCreateNamespace(){
        K8sResource.Namespace namespace = k8sNamespaceRepository.create(K8sEnvEnum.Daily, "test");
        System.err.println(JSON.toJSONString(namespace));
    }
}

