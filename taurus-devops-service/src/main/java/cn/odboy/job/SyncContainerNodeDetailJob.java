package cn.odboy.job;

import cn.hutool.core.date.BetweenFormatter;
import cn.hutool.core.date.DateUtil;
import cn.odboy.constant.K8sEnvEnum;
import cn.odboy.domain.ContainerdClusterConfig;
import cn.odboy.domain.ContainerdClusterNode;
import cn.odboy.model.response.K8sResource;
import cn.odboy.repository.K8sNodeRepository;
import cn.odboy.repository.K8sPodRepository;
import cn.odboy.service.ContainerdClusterConfigService;
import cn.odboy.service.ContainerdClusterNodeService;
import cn.odboy.common.util.CollUtil;
import io.kubernetes.client.openapi.models.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 同步集群Node节点
 *
 * @author odboy
 * @date 2024-11-18
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SyncContainerNodeDetailJob {
    private final ContainerdClusterConfigService containerdClusterConfigService;
    private final ContainerdClusterNodeService containerdClusterNodeService;
    private final K8sNodeRepository k8sNodeRepository;
    private final K8sPodRepository k8sPodRepository;

    public void run() {
        List<ContainerdClusterConfig> list = containerdClusterConfigService.queryHealthConfigList();
        if (CollUtil.isEmpty(list)) {
            log.info("没有找到配置信息");
            return;
        }
        List<ContainerdClusterNode> newRecordList = new ArrayList<>();
        List<ContainerdClusterNode> updRecordList = new ArrayList<>();
        for (ContainerdClusterConfig containerdClusterConfig : list) {
            try {
                K8sEnvEnum envEnum = K8sEnvEnum.getByCode(containerdClusterConfig.getEnvCode());
                if (envEnum != null) {
                    V1NodeList v1NodeList = k8sNodeRepository.listNodes(envEnum.getCode());
                    List<V1Node> items = v1NodeList.getItems();
                    for (V1Node item : items) {
                        this.injectNodeInfo(containerdClusterConfig, item, envEnum, newRecordList, updRecordList);
                    }
                }
            } catch (Exception e) {
                log.error("集群 {} 异常", containerdClusterConfig.getEnvCode(), e);
            }
        }
        if (CollUtil.isNotEmpty(newRecordList)) {
            try {
                containerdClusterNodeService.saveBatch(newRecordList, 100);
            } catch (Exception e) {
                log.error("批量插入Node信息失败", e);
            }
        }
        if (CollUtil.isNotEmpty(updRecordList)) {
            try {
                containerdClusterNodeService.updateBatchById(updRecordList, 100);
            } catch (Exception e) {
                log.error("批量更新Node信息失败", e);
            }
        }
    }

    private void injectNodeInfo(ContainerdClusterConfig containerdClusterConfig, V1Node item, K8sEnvEnum envEnum, List<ContainerdClusterNode> newRecordList, List<ContainerdClusterNode> updRecordList) {
        ContainerdClusterNode clusterNode = new ContainerdClusterNode();
        clusterNode.setClusterConfigId(containerdClusterConfig.getId());
        clusterNode.setEnvCode(envEnum.getCode());
        if (item.getMetadata() != null) {
            clusterNode.setNodeName(item.getMetadata().getName());
            if (CollUtil.isNotEmpty(item.getMetadata().getLabels())) {
                item.getMetadata().getLabels().forEach((k, v) -> {
                    if (k.startsWith("kubernetes.io/role")) {
                        clusterNode.setNodeRoles(v);
                    }
                });
            }
            if (item.getMetadata().getCreationTimestamp() != null) {
                clusterNode.setNodeAge(DateUtil.formatBetween(Date.from(item.getMetadata().getCreationTimestamp().toInstant()), new Date(), BetweenFormatter.Level.DAY));
            }
        }
        if (item.getSpec() != null) {
            clusterNode.setNodePodCidr(item.getSpec().getPodCIDR());
        }
        V1NodeStatus status = item.getStatus();
        if (status != null) {
            if (CollUtil.isNotEmpty(status.getConditions())) {
                V1NodeCondition nodeReadyCondition = status.getConditions().stream().filter(f -> "Ready".equals(f.getType())).findFirst().orElse(null);
                if (nodeReadyCondition == null) {
                    clusterNode.setNodeStatus("False");
                } else {
                    clusterNode.setNodeStatus(nodeReadyCondition.getStatus());
                }
            }
            if (status.getNodeInfo() != null) {
                clusterNode.setNodeK8sVersion(status.getNodeInfo().getKubeletVersion());
                clusterNode.setNodeOsImage(status.getNodeInfo().getOsImage());
                clusterNode.setNodeOsKernelVersion(status.getNodeInfo().getKernelVersion());
                clusterNode.setNodeContainerRuntime(status.getNodeInfo().getContainerRuntimeVersion());
                clusterNode.setNodeOsArchitecture(status.getNodeInfo().getArchitecture());
            }
            if (CollUtil.isNotEmpty(status.getAddresses())) {
                clusterNode.setNodeInternalIp(status.getAddresses().stream().filter(f -> "InternalIP".equals(f.getType())).findFirst().orElse(new V1NodeAddress()).getAddress());
                clusterNode.setNodeHostname(status.getAddresses().stream().filter(f -> "Hostname".equals(f.getType())).findFirst().orElse(new V1NodeAddress()).getAddress());
            }
        }
        // 计算pod数量
        Map<String, String> fieldSelector = new HashMap<>(1);
        fieldSelector.put("spec.nodeName", clusterNode.getNodeName());
        List<K8sResource.Pod> podList = k8sPodRepository.listPods(envEnum.getCode(), fieldSelector, null);
        // 计算Pod数量
        clusterNode.setNodePodSize(podList.size());
        ContainerdClusterNode localContainerdClusterNode = containerdClusterNodeService.getOneByEnvIp(envEnum, clusterNode.getNodeInternalIp());
        if (localContainerdClusterNode == null) {
            newRecordList.add(clusterNode);
        } else {
            clusterNode.setId(localContainerdClusterNode.getId());
            updRecordList.add(clusterNode);
        }
    }
}
