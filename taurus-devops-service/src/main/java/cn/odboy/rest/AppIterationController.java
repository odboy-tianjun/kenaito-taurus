package cn.odboy.rest;

import cn.odboy.domain.AppIteration;
import cn.odboy.mybatisplus.model.PageArgs;
import cn.odboy.service.AppIterationService;
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
 * 应用迭代 前端控制器
 *
 * @author odboy
 * @date 2024-11-15
 */
@RestController
@RequiredArgsConstructor
@Api(tags = "DevOps：应用迭代")
@RequestMapping("/api/devops/appIteration")
public class AppIterationController {
    private final AppIterationService appIterationService;

    @ApiOperation("分页获取迭代列表")
    @PostMapping("/queryPage")
    public ResponseEntity<Object> queryPage(@Validated @RequestBody PageArgs<AppIteration> args) {
        return new ResponseEntity<>(appIterationService.queryPage(args), HttpStatus.OK);
    }

    @ApiOperation("新建迭代")
    @PostMapping("/create")
    public ResponseEntity<Object> create(@Validated@RequestBody AppIteration.CreateArgs args) {
        appIterationService.create(args);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
