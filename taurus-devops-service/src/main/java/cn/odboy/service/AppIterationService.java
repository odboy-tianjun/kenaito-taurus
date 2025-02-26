package cn.odboy.service;

import cn.odboy.domain.AppIteration;
import cn.odboy.model.PageArgs;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 应用迭代记录 服务类
 * </p>
 *
 * @author odboy
 * @since 2024-11-15
 */
public interface AppIterationService extends IService<AppIteration> {

    IPage<AppIteration.QueryPage> queryPage(PageArgs<AppIteration> args);

    void create(AppIteration.CreateArgs args);

    AppIteration queryById(Long iterationId);
}
