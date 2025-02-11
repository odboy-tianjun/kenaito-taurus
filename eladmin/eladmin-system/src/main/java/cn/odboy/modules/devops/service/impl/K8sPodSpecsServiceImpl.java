package cn.odboy.modules.devops.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.odboy.model.CommonModel;
import cn.odboy.model.PageResult;
import cn.odboy.modules.devops.domain.K8sPodSpecs;
import cn.odboy.modules.devops.mapper.K8sPodSpecsMapper;
import cn.odboy.modules.devops.service.K8sPodSpecsService;
import cn.odboy.util.PageUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 * k8s pod规格 服务实现类
 * </p>
 *
 * @author codegen
 * @since 2025-02-11
 */
@Service
public class K8sPodSpecsServiceImpl extends ServiceImpl<K8sPodSpecsMapper, K8sPodSpecs> implements K8sPodSpecsService {
    @Override
    public PageResult<K8sPodSpecs> searchPodSpecss(K8sPodSpecs args, Page<K8sPodSpecs> page) {
        LambdaQueryWrapper<K8sPodSpecs> wrapper = new LambdaQueryWrapper<>();
        if (args != null) {
            wrapper.like(StrUtil.isNotBlank(args.getSpecsName()), K8sPodSpecs::getSpecsName, args.getSpecsName());
            wrapper.eq(args.getStatus() != null, K8sPodSpecs::getStatus, args.getStatus());
        }
        return PageUtil.toPage(baseMapper.selectPage(page, wrapper));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deletePodSpecss(CommonModel.IdArgs args) {
        removeById(args.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createPodSpecs(K8sPodSpecs.CreateArgs args) {
        K8sPodSpecs record = new K8sPodSpecs();
        record.setSpecsName(args.getSpecsName());
        record.setRequestCpu(args.getRequestCpu());
        record.setRequestMemory(args.getRequestMemory());
        record.setLimitCpu(args.getLimitCpu());
        record.setLimitMemory(args.getLimitMemory());
        record.setStatus(true);
        save(record);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void enablePodSpecs(CommonModel.IdArgs args) {
        K8sPodSpecs record = new K8sPodSpecs();
        record.setId(args.getId());
        record.setStatus(true);
        updateById(record);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void disablePodSpecs(CommonModel.IdArgs args) {
        K8sPodSpecs record = new K8sPodSpecs();
        record.setId(args.getId());
        record.setStatus(false);
        updateById(record);
    }
}
