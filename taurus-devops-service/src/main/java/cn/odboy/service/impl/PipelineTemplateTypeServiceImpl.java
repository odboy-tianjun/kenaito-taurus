package cn.odboy.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ReUtil;
import cn.odboy.constant.RegexConst;
import cn.odboy.domain.PipelineTemplateType;
import cn.odboy.exception.BadRequestException;
import cn.odboy.mapper.PipelineTemplateTypeMapper;
import cn.odboy.common.model.MetaOptionModel;
import cn.odboy.mybatisplus.model.PageArgs;
import cn.odboy.service.PipelineTemplateLanguageService;
import cn.odboy.service.PipelineTemplateTypeService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 流水线模板类别 服务实现类
 * </p>
 *
 * @author odboy
 * @since 2024-11-22
 */
@Service
@RequiredArgsConstructor
public class PipelineTemplateTypeServiceImpl extends ServiceImpl<PipelineTemplateTypeMapper, PipelineTemplateType> implements PipelineTemplateTypeService {
    private final PipelineTemplateLanguageService pipelineTemplateLanguageService;

    @Override
    public List<MetaOptionModel> queryList() {
        return list().stream()
                .map(m -> MetaOptionModel.builder()
                        .label(m.getTemplateName())
                        .value(String.valueOf(m.getId()))
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public IPage<PipelineTemplateType.QueryPage> queryPage(PageArgs<PipelineTemplateType> args) {
        LambdaQueryWrapper<PipelineTemplateType> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(PipelineTemplateType::getId);
        Page<PipelineTemplateType> page = page(new Page<>(args.getPage(), args.getPageSize()), wrapper);
        return page.convert(m -> BeanUtil.copyProperties(m, PipelineTemplateType.QueryPage.class));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void create(PipelineTemplateType.CreateArgs args) {
        if (!ReUtil.isMatch(RegexConst.LOW_CHAR_LINE, args.getTemplateCode())) {
            throw new BadRequestException("类型编码只能由小写字母和下划线组成");
        }
        if (existByTemplateCode(args.getTemplateCode())) {
            throw new BadRequestException("类型编码已存在，请更换再试");
        }
        save(BeanUtil.copyProperties(args, PipelineTemplateType.class));
    }

    private boolean existByTemplateCode(String templateCode) {
        return exists(new LambdaQueryWrapper<PipelineTemplateType>()
                .eq(PipelineTemplateType::getTemplateCode, templateCode)
        );
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void remove(PipelineTemplateType.RemoveArgs args) {
        pipelineTemplateLanguageService.existBindRelative(args);
        removeById(args.getId());
    }
}
