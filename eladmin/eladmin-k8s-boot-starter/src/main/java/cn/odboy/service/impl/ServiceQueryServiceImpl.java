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
package cn.odboy.service.impl;

import cn.odboy.context.K8sResourceListenerAdmin;
import cn.odboy.service.ResourceQueryService;
import cn.odboy.util.K8sLabelSelectorUtil;
import io.fabric8.kubernetes.api.model.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * K8s Service，类似nginx的upstream
 *
 * @author odboy
 * @date 2021-03-17
 */
@org.springframework.stereotype.Service
public class ServiceQueryServiceImpl implements ResourceQueryService<Service> {

    @Autowired
    private K8sResourceListenerAdmin listenerAdmin;

    @Override
    public Service get(String clusterCode, String namespace, String name) {
        return listenerAdmin.serviceLister(clusterCode).get(namespace + "/" + name);
    }

    @Override
    public List<Service> list(String clusterCode, Map<String, String> labels) {
        return listenerAdmin.serviceLister(clusterCode).list().stream().filter(
                p -> K8sLabelSelectorUtil.match(p.getMetadata().getLabels(), labels)
        ).collect(Collectors.toList());
    }

    @Override
    public List<Service> list(String clusterCode, String namespace, Map<String, String> labels) {
        if (namespace != null) {
            return listenerAdmin.serviceLister(clusterCode)
                    .namespace(namespace)
                    .list()
                    .stream()
                    .filter(
                            p -> K8sLabelSelectorUtil.match(p.getMetadata().getLabels(), labels)
                    ).collect(Collectors.toList());
        }
        return listenerAdmin.serviceLister(clusterCode)
                .list()
                .stream()
                .filter(
                        p -> K8sLabelSelectorUtil.match(p.getMetadata().getLabels(), labels)
                ).collect(Collectors.toList());
    }

}
