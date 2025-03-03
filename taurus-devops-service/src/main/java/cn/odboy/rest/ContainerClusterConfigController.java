package cn.odboy.rest;

import cn.odboy.domain.AppIteration;
import cn.odboy.domain.ContainerdClusterConfig;
import cn.odboy.mybatisplus.model.PageArgs;
import cn.odboy.service.ContainerdClusterConfigService;
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
 * K8s集群配置 前端控制器
 *
 * @author odboy
 * @date 2024-11-15
 */
@RestController
@RequiredArgsConstructor
@Api(tags = "DevOps：K8s集群配置")
@RequestMapping("/api/devops/containerClusterConfig")
public class ContainerClusterConfigController {
    private final ContainerdClusterConfigService containerdClusterConfigService;

    @ApiOperation("获取集群环境列表")
    @PostMapping("/queryEnvList")
    public ResponseEntity<Object> queryEnvList() {
        return new ResponseEntity<>(containerdClusterConfigService.queryEnvList(), HttpStatus.OK);
    }

    @ApiOperation("获取集群配置列表")
    @PostMapping("/queryPage")
    public ResponseEntity<Object> queryPage(@RequestBody PageArgs<ContainerdClusterConfig> args) {
        return new ResponseEntity<>(containerdClusterConfigService.queryPage(args), HttpStatus.OK);
    }

    @ApiOperation("新建集群配置")
    @PostMapping("/create")
    public ResponseEntity<Object> create(@Validated @RequestBody ContainerdClusterConfig.CreateArgs args) {
        containerdClusterConfigService.create(args);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ApiOperation("修改集群配置")
    @PostMapping("/modify")
    public ResponseEntity<Object> modify(@Validated @RequestBody ContainerdClusterConfig.ModifyArgs args) {
        containerdClusterConfigService.modify(args);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ApiOperation("删除集群配置")
    @PostMapping("/remove")
    public ResponseEntity<Object> remove(@Validated @RequestBody ContainerdClusterConfig.RemoveArgs args) {
        containerdClusterConfigService.remove(args);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
