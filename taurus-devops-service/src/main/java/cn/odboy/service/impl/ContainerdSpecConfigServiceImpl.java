package cn.odboy.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.odboy.domain.ContainerdSpecConfig;
import cn.odboy.infra.exception.BadRequestException;
import cn.odboy.model.PageArgs;
import cn.odboy.mapper.ContainerdSpecConfigMapper;
import cn.odboy.service.ContainerdSpecConfigService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>
 * 容器规格配置 服务实现类
 * </p>
 *
 * @author odboy
 * @since 2024-11-19
 */
@Service
public class ContainerdSpecConfigServiceImpl extends ServiceImpl<ContainerdSpecConfigMapper, ContainerdSpecConfig> implements ContainerdSpecConfigService {

    @Override
    public IPage<ContainerdSpecConfig.QueryPage> queryPage(PageArgs<ContainerdSpecConfig> args) {
        ContainerdSpecConfig body = args.getBody();
        LambdaQueryWrapper<ContainerdSpecConfig> wrapper = new LambdaQueryWrapper<>();
        if (body != null) {
            if (StrUtil.isNotBlank(body.getSpecName())) {
                wrapper.like(ContainerdSpecConfig::getSpecName, body.getSpecName());
            }
        }
        wrapper.orderByDesc(ContainerdSpecConfig::getId);
        Page<ContainerdSpecConfig> page = page(new Page<>(args.getPage(), args.getPageSize()), wrapper);
        return page.convert(m -> BeanUtil.copyProperties(m, ContainerdSpecConfig.QueryPage.class));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void create(ContainerdSpecConfig.CreateArgs args) {
        String specName = args.getSpecName().trim();
        if (exists(new LambdaQueryWrapper<ContainerdSpecConfig>().eq(ContainerdSpecConfig::getSpecName, specName))) {
            throw new BadRequestException("规格名称 " + specName + " 已存在");
        }
        ContainerdSpecConfig newSpecConfig = BeanUtil.copyProperties(args, ContainerdSpecConfig.class);
        save(newSpecConfig);
    }

    @Override
    public List<ContainerdSpecConfig.QueryPage> queryAll() {
        return BeanUtil.copyToList(list(), ContainerdSpecConfig.QueryPage.class);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void remove(ContainerdSpecConfig.RemoveArgs args) {
        removeById(args.getId());
    }
}
