package cn.odboy.mapper;

import cn.odboy.domain.ProductLineUser;
import cn.odboy.common.model.MetaOptionModel;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 产品线、成员关联 Mapper 接口
 * </p>
 *
 * @author odboy
 * @since 2024-09-13
 */
@Mapper
public interface ProductLineUserMapper extends BaseMapper<ProductLineUser> {
    List<MetaOptionModel> selectProductLineUserList(@Param("productLineId") Long productLineId, @Param("roleCode") String roleCode);
}
