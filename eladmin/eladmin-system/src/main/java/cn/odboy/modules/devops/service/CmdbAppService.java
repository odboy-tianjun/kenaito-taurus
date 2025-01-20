package cn.odboy.modules.devops.service;

import cn.odboy.model.CommonModel;
import cn.odboy.model.PageResult;
import cn.odboy.modules.devops.domain.CmdbApp;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 应用列表 服务类
 * </p>
 *
 * @author codegen
 * @since 2025-02-11
 */
public interface CmdbAppService extends IService<CmdbApp> {
    PageResult<CmdbApp> searchApps(CmdbApp args, Page<CmdbApp> page);
    void createApp(CmdbApp.CreateArgs args);
    void offlineApp(CommonModel.IdArgs args);
}
