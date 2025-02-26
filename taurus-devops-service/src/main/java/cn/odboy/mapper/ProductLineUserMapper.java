package cn.odboy.mapper;

import cn.odboy.domain.ProductLineUser;
import cn.odboy.model.MetaOption;
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
    List<MetaOption> selectProductLineUserList(@Param("productLineId") Long productLineId, @Param("roleCode") String roleCode);
}
