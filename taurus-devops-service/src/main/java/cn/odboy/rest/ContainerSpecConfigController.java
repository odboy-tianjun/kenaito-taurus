package cn.odboy.rest;

import cn.odboy.domain.ContainerdSpecConfig;
import cn.odboy.model.PageArgs;
import cn.odboy.service.ContainerdSpecConfigService;
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
 * K8s容器规格配置 前端控制器
 *
 * @author odboy
 * @date 2024-11-19
 */
@RestController
@RequiredArgsConstructor
@Api(tags = "DevOps：K8s容器规格配置")
@RequestMapping("/api/devops/containerSpecConfig")
public class ContainerSpecConfigController {
    private final ContainerdSpecConfigService containerdSpecConfigService;

    @ApiOperation("获取容器规格列表")
    @PostMapping("/queryPage")
    public ResponseEntity<Object> queryPage(@RequestBody PageArgs<ContainerdSpecConfig> args) {
        return new ResponseEntity<>(containerdSpecConfigService.queryPage(args), HttpStatus.OK);
    }

    @ApiOperation("新建容器规格")
    @PostMapping("/create")
    public ResponseEntity<Object> create(@Validated @RequestBody ContainerdSpecConfig.CreateArgs args) {
        containerdSpecConfigService.create(args);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ApiOperation("删除容器规格")
    @PostMapping("/remove")
    public ResponseEntity<Object> remove(@Validated @RequestBody ContainerdSpecConfig.RemoveArgs args) {
        containerdSpecConfigService.remove(args);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ApiOperation("获取所有规格")
    @PostMapping("/queryAll")
    public ResponseEntity<Object> queryAll() {
        return new ResponseEntity<>(containerdSpecConfigService.queryAll(), HttpStatus.OK);
    }
}
