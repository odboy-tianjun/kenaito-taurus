package cn.odboy.rest;

import cn.odboy.domain.ContainerdClusterConfig;
import cn.odboy.domain.ContainerdClusterNode;
import cn.odboy.mybatisplus.model.PageArgs;
import cn.odboy.service.ContainerdClusterConfigService;
import cn.odboy.service.ContainerdClusterNodeService;
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
 * K8s集群节点 前端控制器
 *
 * @author odboy
 * @date 2024-11-18
 */
@RestController
@RequiredArgsConstructor
@Api(tags = "DevOps：K8s集群节点")
@RequestMapping("/api/devops/containerClusterNode")
public class ContainerClusterNodeController {
    private final ContainerdClusterNodeService containerdClusterNodeService;

    @ApiOperation("获取集群配置列表")
    @PostMapping("/queryPage")
    public ResponseEntity<Object> queryPage(@RequestBody PageArgs<ContainerdClusterNode> args) {
        return new ResponseEntity<>(containerdClusterNodeService.queryPage(args), HttpStatus.OK);
    }
}
