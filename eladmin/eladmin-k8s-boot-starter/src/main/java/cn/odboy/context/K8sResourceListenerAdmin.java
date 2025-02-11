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
package cn.odboy.context;

import io.fabric8.kubernetes.api.model.*;
import io.fabric8.kubernetes.api.model.extensions.Ingress;
import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.informers.SharedInformerFactory;
import io.fabric8.kubernetes.client.informers.cache.Lister;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * K8s资源变动监听
 *
 * @author odboy
 * @date 2025-02-11
 */
@Slf4j
@Component
public class K8sResourceListenerAdmin {
    private static final Map<String, SharedInformerFactory> CLIENT_MAP = new HashMap<>();

    public Lister<Namespace> namespaceLister(String clusterCode) {
        return new Lister<>(CLIENT_MAP.get(clusterCode).getExistingSharedIndexInformer(Namespace.class).getIndexer());
    }

    public Lister<Node> nodeLister(String clusterCode) {
        return new Lister<>(CLIENT_MAP.get(clusterCode).getExistingSharedIndexInformer(Node.class).getIndexer());
    }

    public Lister<Service> serviceLister(String clusterCode) {
        return new Lister<>(CLIENT_MAP.get(clusterCode).getExistingSharedIndexInformer(Service.class).getIndexer());
    }

    public Lister<Ingress> ingressLister(String clusterCode) {
        return new Lister<>(CLIENT_MAP.get(clusterCode).getExistingSharedIndexInformer(Ingress.class).getIndexer());
    }

    public Lister<Pod> podLister(String clusterCode) {
        return new Lister<>(CLIENT_MAP.get(clusterCode).getExistingSharedIndexInformer(Pod.class).getIndexer());
    }

    public Lister<Endpoints> endpointsLister(String clusterCode) {
        return new Lister<>(CLIENT_MAP.get(clusterCode).getExistingSharedIndexInformer(Endpoints.class).getIndexer());
    }

    /**
     * @param clusterCode 集群编码
     * @param content     连接配置
     */
    public void addListener(String clusterCode, String content) {
        try {
            io.fabric8.kubernetes.client.Config config = Config.fromKubeconfig(content);
            KubernetesClient client = new DefaultKubernetesClient(config);
            // 注册 Informer
            SharedInformerFactory sharedInformerFactory = client.informers();
            int resyncPeriodInMillis = 1000 * 10;
            sharedInformerFactory.sharedIndexInformerFor(Pod.class, resyncPeriodInMillis);
            sharedInformerFactory.sharedIndexInformerFor(Namespace.class, resyncPeriodInMillis);
            sharedInformerFactory.sharedIndexInformerFor(Service.class, resyncPeriodInMillis);
            sharedInformerFactory.sharedIndexInformerFor(Endpoints.class, resyncPeriodInMillis);
            sharedInformerFactory.sharedIndexInformerFor(Node.class, resyncPeriodInMillis);
            sharedInformerFactory.sharedIndexInformerFor(Ingress.class, resyncPeriodInMillis);
            // 启动所有注册的 Informer
            sharedInformerFactory.startAllRegisteredInformers();
            CLIENT_MAP.put(clusterCode, sharedInformerFactory);
            log.info("集群 {} 初始化Informer成功", clusterCode);
        } catch (Exception e) {
            log.info("集群 {} 初始化Informer失败", clusterCode, e);
        }
    }
}
