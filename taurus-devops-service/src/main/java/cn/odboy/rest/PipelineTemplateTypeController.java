package cn.odboy.rest;

import cn.odboy.domain.PipelineTemplateType;
import cn.odboy.model.PageArgs;
import cn.odboy.service.PipelineTemplateTypeService;
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
 * 流水线类型 前端控制器
 *
 * @author odboy
 * @date 2024-11-22
 */
@RestController
@RequiredArgsConstructor
@Api(tags = "DevOps：流水线类型")
@RequestMapping("/api/devops/pipelineTemplateType")
public class PipelineTemplateTypeController {
    private final PipelineTemplateTypeService pipelineTemplateTypeService;

    @ApiOperation("获取流水线类型列表")
    @PostMapping("/queryTypeList")
    public ResponseEntity<Object> queryTypeList() {
        return new ResponseEntity<>(pipelineTemplateTypeService.queryList(), HttpStatus.OK);
    }

    @ApiOperation("分页获取流水线类型列表")
    @PostMapping("/queryPage")
    public ResponseEntity<Object> queryPage(@RequestBody PageArgs<PipelineTemplateType> args) {
        return new ResponseEntity<>(pipelineTemplateTypeService.queryPage(args), HttpStatus.OK);
    }

    @ApiOperation("新建流水线类型")
    @PostMapping("/create")
    public ResponseEntity<Object> create(@Validated @RequestBody PipelineTemplateType.CreateArgs args) {
        pipelineTemplateTypeService.create(args);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ApiOperation("删除流水线类型")
    @PostMapping("/remove")
    public ResponseEntity<Object> remove(@Validated @RequestBody PipelineTemplateType.RemoveArgs args) {
        pipelineTemplateTypeService.remove(args);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
