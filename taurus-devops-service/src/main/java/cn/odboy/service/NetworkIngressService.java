package cn.odboy.service;

import cn.odboy.domain.NetworkIngress;
import cn.odboy.mybatisplus.model.PageArgs;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * devops网络ingress-nginx 服务类
 * </p>
 *
 * @author odboy
 * @since 2024-11-21
 */
public interface NetworkIngressService extends IService<NetworkIngress> {

    IPage<NetworkIngress.QueryPage> queryPage(PageArgs<NetworkIngress> args);

    void create(NetworkIngress.CreateArgs args);

    void remove(NetworkIngress.RemoveArgs args);
}
