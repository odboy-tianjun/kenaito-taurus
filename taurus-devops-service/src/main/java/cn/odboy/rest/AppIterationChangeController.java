package cn.odboy.rest;

import cn.odboy.domain.AppIterationChange;
import cn.odboy.mybatisplus.model.PageArgs;
import cn.odboy.service.AppIterationChangeService;
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
 * 应用变更 前端控制器
 *
 * @author odboy
 * @date 2024-11-15
 */
@RestController
@RequiredArgsConstructor
@Api(tags = "DevOps：应用变更")
@RequestMapping("/api/devops/appIterationChange")
public class AppIterationChangeController {
    private final AppIterationChangeService appIterationChangeService;

    @ApiOperation("分页获取应用变更列表")
    @PostMapping("/queryPage")
    public ResponseEntity<Object> queryPage(@RequestBody PageArgs<AppIterationChange> args) {
        return new ResponseEntity<>(appIterationChangeService.queryPage(args), HttpStatus.OK);
    }

    @ApiOperation("分页获取应用非变更列表")
    @PostMapping("/queryUnChangePage")
    public ResponseEntity<Object> queryUnChangePage(@RequestBody PageArgs<AppIterationChange> args) {
        return new ResponseEntity<>(appIterationChangeService.queryUnChangePage(args), HttpStatus.OK);
    }

    @ApiOperation("添加应用变更")
    @PostMapping("/create")
    public ResponseEntity<Object> create(@Validated @RequestBody AppIterationChange.CreateArgs args) {
        appIterationChangeService.create(args);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ApiOperation("退出集成区")
    @PostMapping("/exitCiArea")
    public ResponseEntity<Object> exitCiArea(@Validated @RequestBody AppIterationChange.ExistCiAreaArgs args) {
        appIterationChangeService.exitCiArea(args);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ApiOperation("加入集成区")
    @PostMapping("/joinCiArea")
    public ResponseEntity<Object> joinCiArea(@Validated @RequestBody AppIterationChange.JoinCiAreaArgs args) {
        appIterationChangeService.joinCiArea(args);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
