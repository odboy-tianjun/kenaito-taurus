package cn.odboy.mapper;

import cn.odboy.domain.ProductLineApp;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 产品线、应用关联 Mapper 接口
 * </p>
 *
 * @author odboy
 * @since 2024-09-13
 */
@Mapper
public interface ProductLineAppMapper extends BaseMapper<ProductLineApp> {
}
