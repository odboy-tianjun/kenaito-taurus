package cn.odboy.rest;

import cn.odboy.domain.PipelineTemplateLanguage;
import cn.odboy.model.PageArgs;
import cn.odboy.service.PipelineTemplateLanguageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 流水线语言模板 前端控制器
 *
 * @author odboy
 * @date 2024-11-22
 */
@RestController
@RequiredArgsConstructor
@Api(tags = "DevOps：流水线语言模板")
@RequestMapping("/api/devops/pipelineLanguage")
public class PipelineTemplateLanguageController {
    private final PipelineTemplateLanguageService pipelineTemplateLanguageService;

    @ApiOperation("获取流水线语言模板列表")
    @PostMapping("/queryLanguageList")
    public ResponseEntity<Object> queryLanguageList(@Validated @RequestBody PipelineTemplateLanguage.QueryArgs args) {
        return new ResponseEntity<>(pipelineTemplateLanguageService.queryLanguageList(args), HttpStatus.OK);
    }

     @ApiOperation("分页获取流水线语言模板列表")
    @PostMapping("/queryPage")
    public ResponseEntity<Object> queryPage(@RequestBody PageArgs<PipelineTemplateLanguage> args) {
        return new ResponseEntity<>(pipelineTemplateLanguageService.queryPage(args), HttpStatus.OK);
    }

    @ApiOperation("新建流水线语言模板")
    @PostMapping("/create")
    public ResponseEntity<Object> create(@Validated @RequestBody PipelineTemplateLanguage.CreateArgs args) {
        pipelineTemplateLanguageService.create(args);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ApiOperation("删除流水线语言模板")
    @PostMapping("/remove")
    public ResponseEntity<Object> remove(@Validated @RequestBody PipelineTemplateLanguage.RemoveArgs args) {
        pipelineTemplateLanguageService.remove(args);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
