package cn.odboy.rest;

import cn.odboy.domain.NetworkIngress;
import cn.odboy.domain.NetworkService;
import cn.odboy.mybatisplus.model.PageArgs;
import cn.odboy.service.NetworkIngressService;
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
 * K8s容器IngressNginx 前端控制器
 *
 * @author odboy
 * @date 2024-11-20
 */
@RestController
@RequiredArgsConstructor
@Api(tags = "DevOps：K8s容器IngressNginx")
@RequestMapping("/api/devops/networkIngress")
public class NetworkIngressController {
    private final NetworkIngressService networkIngressService;

    @ApiOperation("获取IngressNginx列表")
    @PostMapping("/queryPage")
    public ResponseEntity<Object> queryPage(@RequestBody PageArgs<NetworkIngress> args) {
        return new ResponseEntity<>(networkIngressService.queryPage(args), HttpStatus.OK);
    }

    @ApiOperation("新建IngressNginx")
    @PostMapping("/create")
    public ResponseEntity<Object> create(@Validated @RequestBody NetworkIngress.CreateArgs args) {
        networkIngressService.create(args);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ApiOperation("删除IngressNginx")
    @PostMapping("/remove")
    public ResponseEntity<Object> remove(@Validated @RequestBody NetworkIngress.RemoveArgs args) {
        networkIngressService.remove(args);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
