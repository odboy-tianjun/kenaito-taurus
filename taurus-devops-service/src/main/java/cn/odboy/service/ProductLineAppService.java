package cn.odboy.service;

import cn.odboy.domain.ProductLineApp;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 产品线、应用关联 服务类
 * </p>
 *
 * @author odboy
 * @since 2024-09-13
 */
public interface ProductLineAppService extends IService<ProductLineApp> {
    /**
     * 判断产品线下是否挂靠应用
     *
     * @param productLineParentId 父产品线id
     */
    void isBindApp(Long productLineParentId);

    /**
     * 应用绑定产品线
     * @param appId 应用id
     * @param productLineId 产品线id
     */
    void bindApp(Long appId, Long productLineId);
}
