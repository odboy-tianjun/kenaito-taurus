package cn.odboy.mapper;

import cn.odboy.domain.ContainerdClusterConfig;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * k8s集群配置 Mapper 接口
 * </p>
 *
 * @author odboy
 * @since 2024-11-15
 */
@Mapper
public interface ContainerdClusterConfigMapper extends BaseMapper<ContainerdClusterConfig> {

}
