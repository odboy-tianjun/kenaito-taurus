package cn.odboy.service.impl;

import cn.odboy.domain.ProductLineUser;
import cn.odboy.mapper.ProductLineUserMapper;
import cn.odboy.model.MetaOption;
import cn.odboy.service.ProductLineUserService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * <p>
 * 产品线、成员关联 服务实现类
 * </p>
 *
 * @author odboy
 * @since 2024-09-13
 */
@Service
public class ProductLineUserServiceImpl extends ServiceImpl<ProductLineUserMapper, ProductLineUser> implements ProductLineUserService {

    @Override
    public void removeByProductLineId(Long id) {
        remove(new LambdaQueryWrapper<ProductLineUser>()
                .eq(ProductLineUser::getProductLineId, id)
        );
    }

    @Override
    public void removeBatchByProductLineIds(List<Long> ids) {
        remove(new LambdaQueryWrapper<ProductLineUser>()
                .in(ProductLineUser::getProductLineId, ids)
        );
    }

    @Override
    public List<MetaOption> queryProductLineUserList(Long productLineId, String roleCode) {
        return getBaseMapper().selectProductLineUserList(productLineId, roleCode);
    }
}
