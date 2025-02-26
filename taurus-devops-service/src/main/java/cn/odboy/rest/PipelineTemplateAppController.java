package cn.odboy.rest;

import cn.odboy.domain.PipelineTemplateApp;
import cn.odboy.model.PageArgs;
import cn.odboy.service.PipelineTemplateAppService;
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
 * 应用流水线关系 前端控制器
 *
 * @author odboy
 * @date 2024-11-22
 */
@RestController
@RequiredArgsConstructor
@Api(tags = "DevOps：应用流水线关系")
@RequestMapping("/api/devops/pipelineTemplateApp")
public class PipelineTemplateAppController {
    private final PipelineTemplateAppService pipelineTemplateAppService;

    @ApiOperation("配置流水线")
    @PostMapping("/configure")
    public ResponseEntity<Object> configurePipeline(@Validated @RequestBody PipelineTemplateApp.CreateArgs args) {
        pipelineTemplateAppService.configurePipeline(args);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ApiOperation("获取应用流水线模板列表")
    @PostMapping("/queryTemplateDetailList")
    public ResponseEntity<Object> queryTemplateDetailList(@Validated @RequestBody PageArgs<PipelineTemplateApp.QueryTemplateDetailArgs> args) {
        return new ResponseEntity<>(pipelineTemplateAppService.queryTemplateDetailList(args), HttpStatus.OK);
    }

    @ApiOperation("移除流水线")
    @PostMapping("/remove")
    public ResponseEntity<Object> remove(@Validated @RequestBody PipelineTemplateApp.RemoveArgs args) {
        pipelineTemplateAppService.remove(args);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ApiOperation("获取应用部署流水线环境配置列表")
    @PostMapping("/queryAppDeployPipelineEnvConfigList")
    public ResponseEntity<Object> queryAppDeployPipelineEnvConfigList(@Validated @RequestBody PipelineTemplateApp.QueryAppDeployPipelineEnvConfigArgs args) {
        return new ResponseEntity<>(pipelineTemplateAppService.queryAppDeployPipelineEnvConfigList(args), HttpStatus.OK);
    }
}
