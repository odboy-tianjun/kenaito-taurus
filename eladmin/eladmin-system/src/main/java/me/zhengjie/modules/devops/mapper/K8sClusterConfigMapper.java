package me.zhengjie.modules.devops.mapper;

import me.zhengjie.infra.mybatisplus.EasyMapper;
import me.zhengjie.modules.devops.domain.K8sClusterConfig;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * k8s集群配置 Mapper 接口
 * </p>
 *
 * @author codegen
 * @since 2025-01-12
 */
@Mapper
public interface K8sClusterConfigMapper extends EasyMapper<K8sClusterConfig> {

}
