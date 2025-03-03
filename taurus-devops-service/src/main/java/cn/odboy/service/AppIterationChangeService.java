package cn.odboy.service;

import cn.odboy.domain.AppIterationChange;
import cn.odboy.mybatisplus.model.PageArgs;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 应用迭代变更列表 服务类
 * </p>
 *
 * @author odboy
 * @since 2024-11-15
 */
public interface AppIterationChangeService extends IService<AppIterationChange> {

    IPage<AppIterationChange.QueryPage> queryPage(PageArgs<AppIterationChange> args);

    void create(AppIterationChange.CreateArgs args);

    IPage<AppIterationChange.QueryPage> queryUnChangePage(PageArgs<AppIterationChange> args);

    void exitCiArea(AppIterationChange.ExistCiAreaArgs args);

    void joinCiArea(AppIterationChange.JoinCiAreaArgs args);
}
