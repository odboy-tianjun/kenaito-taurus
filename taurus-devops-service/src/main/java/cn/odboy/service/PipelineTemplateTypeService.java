package cn.odboy.service;

import cn.odboy.domain.PipelineTemplateType;
import cn.odboy.model.MetaOption;
import cn.odboy.model.PageArgs;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 流水线模板类别 服务类
 * </p>
 *
 * @author odboy
 * @since 2024-11-22
 */
public interface PipelineTemplateTypeService extends IService<PipelineTemplateType> {
    List<MetaOption> queryList();

    IPage<PipelineTemplateType.QueryPage> queryPage(PageArgs<PipelineTemplateType> args);

    void create(PipelineTemplateType.CreateArgs args);

    void remove(PipelineTemplateType.RemoveArgs args);
}
