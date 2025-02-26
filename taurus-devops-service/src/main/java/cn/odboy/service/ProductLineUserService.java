package cn.odboy.service;

import cn.odboy.domain.ProductLineUser;
import cn.odboy.model.MetaOption;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 产品线、成员关联 服务类
 * </p>
 *
 * @author odboy
 * @since 2024-09-13
 */
public interface ProductLineUserService extends IService<ProductLineUser> {

    void removeByProductLineId(Long id);

    void removeBatchByProductLineIds(List<Long> ids);

    List<MetaOption> queryProductLineUserList(Long productLineId, String roleCode);
}
