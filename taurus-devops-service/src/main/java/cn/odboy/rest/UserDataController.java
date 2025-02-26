package cn.odboy.rest;

import cn.odboy.model.MetaOption;
import cn.odboy.model.PageArgs;
import cn.odboy.model.PageResult;
import cn.odboy.modules.system.domain.User;
import cn.odboy.modules.system.service.UserService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 用户数据 前端控制器
 * </p>
 *
 * @author odboy
 * @since 2024-09-13
 */
@RestController
@RequiredArgsConstructor
@Api(tags = "DevOps：用户信息")
@RequestMapping("/api/devops/user")
public class UserDataController {
    private final UserService userService;

    @ApiOperation("分页查询用户列表")
    @PostMapping("/pageList")
    public ResponseEntity<Object> pageList(@RequestBody PageArgs<User.QueryArgs> args) {
        PageResult<User> result = userService.pageList(args.getBody(), new Page<>(args.getPage(), args.getPageSize()));
        List<MetaOption> selectOptions = result.getContent().stream().map(m -> {
            MetaOption option = new MetaOption();
            option.setValue(String.valueOf(m.getId()));
            option.setLabel(m.getUsername());
            return option;
        }).collect(Collectors.toList());
        return new ResponseEntity<>(selectOptions, HttpStatus.OK);
    }
}
