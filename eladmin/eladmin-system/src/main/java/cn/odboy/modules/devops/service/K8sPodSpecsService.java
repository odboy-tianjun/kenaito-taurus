package cn.odboy.modules.devops.service;

import cn.odboy.model.CommonModel;
import cn.odboy.model.PageResult;
import cn.odboy.modules.devops.domain.K8sPodSpecs;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * k8s pod规格 服务类
 * </p>
 *
 * @author codegen
 * @since 2025-02-11
 */
public interface K8sPodSpecsService extends IService<K8sPodSpecs> {
    PageResult<K8sPodSpecs> searchPodSpecss(K8sPodSpecs args, Page<K8sPodSpecs> page);
    void deletePodSpecss(CommonModel.IdArgs args);
    void createPodSpecs(K8sPodSpecs.CreateArgs args);
    void enablePodSpecs(CommonModel.IdArgs args);
    void disablePodSpecs(CommonModel.IdArgs args);
}
