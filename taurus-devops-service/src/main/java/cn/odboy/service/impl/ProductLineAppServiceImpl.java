package cn.odboy.service.impl;

import cn.odboy.domain.ProductLineApp;
import cn.odboy.infra.exception.BadRequestException;
import cn.odboy.mapper.ProductLineAppMapper;
import cn.odboy.service.ProductLineAppService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 * 产品线、应用关联 服务实现类
 * </p>
 *
 * @author odboy
 * @since 2024-09-13
 */
@Service
public class ProductLineAppServiceImpl extends ServiceImpl<ProductLineAppMapper, ProductLineApp> implements ProductLineAppService {
    @Override
    public void isBindApp(Long productLineParentId) {
        boolean bindStatus = getBaseMapper().exists(new LambdaQueryWrapper<ProductLineApp>()
                .eq(ProductLineApp::getProductLineId, productLineParentId)
        );
        if (bindStatus) {
            throw new BadRequestException("产品线已绑定应用, 无法新增产品线");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void bindApp(Long appId, Long productLineId) {
        ProductLineApp record = new ProductLineApp();
        record.setProductLineId(productLineId);
        record.setAppId(appId);
        getBaseMapper().insert(record);
    }
}
