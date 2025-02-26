package cn.odboy.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.odboy.domain.OpsConfig;
import cn.odboy.mapper.OpsConfigMapper;
import cn.odboy.service.OpsConfigService;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

/**
 * <p>
 * 运维配置 服务实现类
 * </p>
 *
 * @author odboy
 * @since 2024-11-19
 */
@Service
public class OpsConfigServiceImpl extends ServiceImpl<OpsConfigMapper, OpsConfig> implements OpsConfigService {

    @Override
    public OpsConfig.BlockNetwork getBlockNetworkConfig() {
        OpsConfig blockNetwork = getOne(new LambdaQueryWrapper<OpsConfig>().eq(OpsConfig::getConfigCode, "blockNetwork"));
        if (StrUtil.isNotBlank(blockNetwork.getConfigValue())) {
            return JSON.parseObject(blockNetwork.getConfigValue(), OpsConfig.BlockNetwork.class);
        }
        OpsConfig.BlockNetwork defaultBlockNetwork = new OpsConfig.BlockNetwork();
        defaultBlockNetwork.setDisableWebSsh(false);
        defaultBlockNetwork.setBlockList(new ArrayList<>());
        return defaultBlockNetwork;
    }
}
