package cn.odboy.rest;

import cn.odboy.domain.PipelineTemplateLanguageConfig;
import cn.odboy.service.PipelineTemplateLanguageConfigService;
import com.alibaba.fastjson.JSON;
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

import java.util.List;
import java.util.stream.Collectors;

/**
 * 流水线语言模板节点配置 前端控制器
 *
 * @author odboy
 * @date 2024-11-22
 */
@RestController
@RequiredArgsConstructor
@Api(tags = "DevOps：流水线语言模板节点配置")
@RequestMapping("/api/devops/pipelineLanguageConfig")
public class PipelineTemplateLanguageConfigController {
    private final PipelineTemplateLanguageConfigService pipelineTemplateLanguageConfigService;

    @ApiOperation("获取流水线节点列表")
    @PostMapping("/queryNodeList")
    public ResponseEntity<Object> queryNodeList(@Validated @RequestBody PipelineTemplateLanguageConfig.QueryArgs args) {
        List<PipelineTemplateLanguageConfig> pipelineTemplateLanguageConfigs = pipelineTemplateLanguageConfigService.queryNodeList(args);
        return new ResponseEntity<>(pipelineTemplateLanguageConfigs.stream().map(m -> {
            PipelineTemplateLanguageConfig.AppPipelineNodeConfig nodeConfig = new PipelineTemplateLanguageConfig.AppPipelineNodeConfig();
            nodeConfig.setId(m.getId());
            nodeConfig.setEnvCode(m.getEnvCode());
            nodeConfig.setTemplateId(m.getTemplateId());
            nodeConfig.setTemplateLanguageId(m.getTemplateLanguageId());
            nodeConfig.setNodeName(m.getNodeName());
            nodeConfig.setNodeType(m.getNodeType());
            nodeConfig.setIsClick(m.getIsClick());
            nodeConfig.setIsRetry(m.getIsRetry());
            nodeConfig.setIsJudge(m.getIsJudge());
            if (m.getJudgeBtnList() != null) {
                nodeConfig.setJudgeBtnList(JSON.parseArray(m.getJudgeBtnList(), PipelineTemplateLanguageConfig.AppPipelineNodeConfig.JudgeBtn.class));
            }
            nodeConfig.setHandleMethod(m.getHandleMethod());
            nodeConfig.setHandleParameters(m.getHandleParameters());
            nodeConfig.setAppName(null);
            nodeConfig.setOrderNum(m.getOrderNum());
            return nodeConfig;
        }).collect(Collectors.toList()), HttpStatus.OK);
    }

    @ApiOperation("保存流水线节点配置")
    @PostMapping("/saveNodeConfig")
    public ResponseEntity<Object> saveNodeConfig(@RequestBody List<PipelineTemplateLanguageConfig.AppPipelineNodeConfig> args) {
        pipelineTemplateLanguageConfigService.saveNodeConfig(args);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
