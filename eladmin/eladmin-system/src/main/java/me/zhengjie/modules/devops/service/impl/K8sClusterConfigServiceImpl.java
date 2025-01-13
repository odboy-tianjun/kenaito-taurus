package me.zhengjie.modules.devops.service.impl;

import cn.hutool.core.lang.Dict;
import me.zhengjie.infra.mybatisplus.impl.EasyServiceImpl;
import me.zhengjie.model.MetaOption;
import me.zhengjie.modules.devops.domain.K8sClusterConfig;
import me.zhengjie.modules.devops.mapper.K8sClusterConfigMapper;
import me.zhengjie.modules.devops.service.K8sClusterConfigService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * k8s集群配置 服务实现类
 * </p>
 *
 * @author codegen
 * @since 2025-01-12
 */
@Service
public class K8sClusterConfigServiceImpl extends EasyServiceImpl<K8sClusterConfigMapper, K8sClusterConfig> implements K8sClusterConfigService {
    @Override
    public List<MetaOption> listClusterCodes() {
        return quickList(null).stream()
                .map(m -> MetaOption.builder()
                        .label(m.getClusterName())
                        .value(m.getClusterCode())
                        .ext(Dict.create()
                                .set("envCode", m.getEnvCode())
                                .set("status", m.getStatus())
                        )
                        .build())
                .collect(Collectors.toList());
    }
}
