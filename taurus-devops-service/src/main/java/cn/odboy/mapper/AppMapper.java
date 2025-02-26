package cn.odboy.mapper;

import cn.odboy.domain.App;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 应用 Mapper 接口
 * </p>
 *
 * @author odboy
 * @since 2024-09-13
 */
@Mapper
public interface AppMapper extends BaseMapper<App> {
    List<App.QueryPage> selectCollectList(@Param("userId") Long userId);
}
