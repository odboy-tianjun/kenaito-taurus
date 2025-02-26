package cn.odboy.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.odboy.domain.ContainerdClusterNode;
import cn.odboy.constant.K8sEnvEnum;
import cn.odboy.model.PageArgs;
import cn.odboy.mapper.ContainerdClusterNodeMapper;
import cn.odboy.service.ContainerdClusterNodeService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 容器集群node节点 服务实现类
 * </p>
 *
 * @author odboy
 * @since 2024-11-18
 */
@Service
public class ContainerdClusterNodeServiceImpl extends ServiceImpl<ContainerdClusterNodeMapper, ContainerdClusterNode> implements ContainerdClusterNodeService {

    @Override
    public ContainerdClusterNode getOneByEnvIp(K8sEnvEnum envEnum, String nodeInternalIp) {
        return getOne(new LambdaQueryWrapper<ContainerdClusterNode>()
                .eq(ContainerdClusterNode::getEnvCode, envEnum.getCode())
                .eq(ContainerdClusterNode::getNodeInternalIp, nodeInternalIp)
        );
    }

    @Override
    public IPage<ContainerdClusterNode> queryPage(PageArgs<ContainerdClusterNode> args) {
        LambdaQueryWrapper<ContainerdClusterNode> wrapper = new LambdaQueryWrapper<>();
        ContainerdClusterNode body = args.getBody();
        if (body != null) {
            wrapper.eq(StrUtil.isNotBlank(body.getEnvCode()), ContainerdClusterNode::getEnvCode, body.getEnvCode());
        }
        wrapper.orderByDesc(ContainerdClusterNode::getId);
        return page(new Page<>(1, 100), wrapper);
    }
}
