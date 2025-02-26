package cn.odboy.service;

import cn.odboy.domain.ContainerdSpecConfig;
import cn.odboy.model.PageArgs;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 容器规格配置 服务类
 * </p>
 *
 * @author odboy
 * @since 2024-11-19
 */
public interface ContainerdSpecConfigService extends IService<ContainerdSpecConfig> {

    IPage<ContainerdSpecConfig.QueryPage> queryPage(PageArgs<ContainerdSpecConfig> args);

    void create(ContainerdSpecConfig.CreateArgs args);

    List<ContainerdSpecConfig.QueryPage> queryAll();

    void remove(ContainerdSpecConfig.RemoveArgs args);
}
