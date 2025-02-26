package cn.odboy.service;

import cn.odboy.domain.PipelineTemplateLanguage;
import cn.odboy.domain.PipelineTemplateType;
import cn.odboy.model.MetaOption;
import cn.odboy.model.PageArgs;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 流水线各语言模版 服务类
 * </p>
 *
 * @author odboy
 * @since 2024-11-22
 */
public interface PipelineTemplateLanguageService extends IService<PipelineTemplateLanguage> {
    List<MetaOption> queryLanguageList(PipelineTemplateLanguage.QueryArgs args);

    IPage<PipelineTemplateLanguage.QueryPage> queryPage(PageArgs<PipelineTemplateLanguage> args);

    void create(PipelineTemplateLanguage.CreateArgs args);

    void remove(PipelineTemplateLanguage.RemoveArgs args);

    void existBindRelative(PipelineTemplateType.RemoveArgs args);
}
