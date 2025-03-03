package cn.odboy.service;

import cn.odboy.domain.ContainerdClusterNode;
import cn.odboy.constant.K8sEnvEnum;
import cn.odboy.mybatisplus.model.PageArgs;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 容器集群node节点 服务类
 * </p>
 *
 * @author odboy
 * @since 2024-11-18
 */
public interface ContainerdClusterNodeService extends IService<ContainerdClusterNode> {
    ContainerdClusterNode getOneByEnvIp(K8sEnvEnum envEnum, String nodeInternalIp);

    IPage<ContainerdClusterNode> queryPage(PageArgs<ContainerdClusterNode> args);
}
