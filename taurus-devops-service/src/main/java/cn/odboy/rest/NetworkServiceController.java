package cn.odboy.rest;

import cn.odboy.domain.NetworkService;
import cn.odboy.model.PageArgs;
import cn.odboy.service.NetworkServiceService;
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
 * K8s容器Service 前端控制器
 *
 * @author odboy
 * @date 2024-11-20
 */
@RestController
@RequiredArgsConstructor
@Api(tags = "DevOps：K8s容器Service")
@RequestMapping("/api/devops/networkService")
public class NetworkServiceController {
    private final NetworkServiceService networkServiceService;

    @ApiOperation("获取Service列表")
    @PostMapping("/queryPage")
    public ResponseEntity<Object> queryPage(@RequestBody PageArgs<NetworkService> args) {
        return new ResponseEntity<>(networkServiceService.queryPage(args), HttpStatus.OK);
    }

    @ApiOperation("新建Service")
    @PostMapping("/create")
    public ResponseEntity<Object> create(@Validated @RequestBody NetworkService.CreateArgs args) {
        networkServiceService.create(args);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
