package cn.odboy.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.odboy.domain.PipelineTemplateLanguage;
import cn.odboy.domain.PipelineTemplateType;
import cn.odboy.mapper.PipelineTemplateLanguageMapper;
import cn.odboy.common.model.MetaOptionModel;
import cn.odboy.mybatisplus.model.PageArgs;
import cn.odboy.service.PipelineTemplateLanguageConfigService;
import cn.odboy.service.PipelineTemplateLanguageService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.ws.rs.BadRequestException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 流水线各语言模版 服务实现类
 * </p>
 *
 * @author odboy
 * @since 2024-11-22
 */
@Service
@RequiredArgsConstructor
public class PipelineTemplateLanguageServiceImpl extends ServiceImpl<PipelineTemplateLanguageMapper, PipelineTemplateLanguage> implements PipelineTemplateLanguageService {
    private final PipelineTemplateLanguageConfigService pipelineTemplateLanguageConfigService;

    @Override
    public List<MetaOptionModel> queryLanguageList(PipelineTemplateLanguage.QueryArgs args) {
        return list(new LambdaQueryWrapper<PipelineTemplateLanguage>()
                        .eq(PipelineTemplateLanguage::getTemplateId, args.getTemplateId())
                        .eq(PipelineTemplateLanguage::getEnvCode, args.getEnvCode())
                        .eq(PipelineTemplateLanguage::getAppLanguage, args.getAppLanguage())
                ).stream().map(m -> MetaOptionModel.builder().value(String.valueOf(m.getId())).label(m.getTemplateName()).build())
                .collect(Collectors.toList());
    }

    @Override
    public IPage<PipelineTemplateLanguage.QueryPage> queryPage(PageArgs<PipelineTemplateLanguage> args) {
        LambdaQueryWrapper<PipelineTemplateLanguage> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(PipelineTemplateLanguage::getTemplateId);
        Page<PipelineTemplateLanguage> page = page(new Page<>(args.getPage(), args.getPageSize()), wrapper);
        return page.convert(m -> BeanUtil.copyProperties(m, PipelineTemplateLanguage.QueryPage.class));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void create(PipelineTemplateLanguage.CreateArgs args) {
        save(BeanUtil.copyProperties(args, PipelineTemplateLanguage.class));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void remove(PipelineTemplateLanguage.RemoveArgs args) {
        PipelineTemplateLanguage pipelineTemplateLanguage = getById(args.getId());
        pipelineTemplateLanguageConfigService.existBindRelative(args.getId(), pipelineTemplateLanguage.getTemplateId());
        removeById(args.getId());
    }

    @Override
    public void existBindRelative(PipelineTemplateType.RemoveArgs args) {
        boolean exists = exists(new LambdaQueryWrapper<PipelineTemplateLanguage>()
                .eq(PipelineTemplateLanguage::getTemplateId, args.getId())
        );
        if (exists) {
            throw new BadRequestException("该类型下存在流水线语言模版关系，无法删除");
        }
    }
}
