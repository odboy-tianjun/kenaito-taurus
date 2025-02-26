package cn.odboy.service;

import cn.odboy.constant.ContainerdClusterConfigEnvStatusEnum;
import cn.odboy.domain.ContainerdClusterConfig;
import cn.odboy.model.MetaOption;
import cn.odboy.model.PageArgs;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * k8s集群配置 服务类
 * </p>
 *
 * @author odboy
 * @since 2024-11-15
 */
public interface ContainerdClusterConfigService extends IService<ContainerdClusterConfig> {
    IPage<ContainerdClusterConfig.QueryPage> queryPage(PageArgs<ContainerdClusterConfig> args);

    void create(ContainerdClusterConfig.CreateArgs args);

    List<MetaOption> queryEnvList();

    void modify(ContainerdClusterConfig.ModifyArgs args);

    ContainerdClusterConfig getByEnvCode(String envCode);

    void modifyStatus(Long id, ContainerdClusterConfigEnvStatusEnum containerdClusterConfigEnvStatusEnum);

    List<ContainerdClusterConfig> queryHealthConfigList();

    void remove(ContainerdClusterConfig.RemoveArgs args);
}
