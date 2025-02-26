package cn.odboy.service;

import cn.odboy.domain.OpsConfig;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 运维配置 服务类
 * </p>
 *
 * @author odboy
 * @since 2024-11-19
 */
public interface OpsConfigService extends IService<OpsConfig> {
    OpsConfig.BlockNetwork getBlockNetworkConfig();
}
