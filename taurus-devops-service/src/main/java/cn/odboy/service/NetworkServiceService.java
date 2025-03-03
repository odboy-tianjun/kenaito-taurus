package cn.odboy.service;

import cn.odboy.domain.NetworkService;
import cn.odboy.mybatisplus.model.PageArgs;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * k8s网络service 服务类
 * </p>
 *
 * @author odboy
 * @since 2024-11-20
 */
public interface NetworkServiceService extends IService<NetworkService> {

    IPage<NetworkService.QueryPage> queryPage(PageArgs<NetworkService> args);

    void create(NetworkService.CreateArgs args);
}
