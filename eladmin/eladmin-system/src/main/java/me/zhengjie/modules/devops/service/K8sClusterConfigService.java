package me.zhengjie.modules.devops.service;

import me.zhengjie.infra.mybatisplus.EasyService;
import me.zhengjie.model.MetaOption;
import me.zhengjie.modules.devops.domain.K8sClusterConfig;

import java.util.List;

/**
 * <p>
 * k8s集群配置 服务类
 * </p>
 *
 * @author codegen
 * @since 2025-01-12
 */
public interface K8sClusterConfigService extends EasyService<K8sClusterConfig> {
    /**
     * 查询集群配置编码元数据
     * @return /
     */
    List<MetaOption> listClusterCodes();
}
