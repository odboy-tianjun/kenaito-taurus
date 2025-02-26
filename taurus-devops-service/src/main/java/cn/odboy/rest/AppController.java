package cn.odboy.rest;

import cn.odboy.annotation.Log;
import cn.odboy.domain.App;
import cn.odboy.model.PageArgs;
import cn.odboy.service.AppService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 应用 前端控制器
 * </p>
 *
 * @author odboy
 * @since 2024-09-13
 */
@RestController
@RequiredArgsConstructor
@Api(tags = "DevOps：应用管理")
@RequestMapping("/api/devops/app")
public class AppController {
    private final AppService appService;

    @ApiOperation("分页获取应用列表")
    @PostMapping("/queryPage")
    public ResponseEntity<Object> queryPage(@RequestBody PageArgs<App> args) {
        return new ResponseEntity<>(appService.queryPage(args), HttpStatus.OK);
    }

    @ApiOperation("分页获取收藏的应用列表")
    @PostMapping("/queryCollectList")
    public ResponseEntity<Object> queryCollectList() {
        return new ResponseEntity<>(appService.queryCollectList(), HttpStatus.OK);
    }

    @Log("新增应用")
    @ApiOperation("新增应用")
    @PostMapping("/create")
    @PreAuthorize("@el.check('app:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody App.CreateArgs args) {
        appService.create(args);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @Log("收藏应用")
    @ApiOperation("收藏应用")
    @PostMapping("/changeCollect")
    @PreAuthorize("@el.check('app:changeCollect')")
    public ResponseEntity<Object> changeCollect(@Validated @RequestBody App.ChangeCollectArgs args) {
        appService.changeCollect(args);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Log("关联应用成员")
    @ApiOperation("关联应用成员")
    @PostMapping("/bindMember")
    @PreAuthorize("@el.check('app:bindMember')")
    public ResponseEntity<Object> bindMember(@Validated @RequestBody App.BindMemberArgs args) {
        appService.bindMember(args);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Log("解除应用成员关联关系")
    @ApiOperation("解除应用成员关联关系")
    @PostMapping("/unBindMember")
    @PreAuthorize("@el.check('app:unBindMember')")
    public ResponseEntity<Object> unBindMember(@Validated @RequestBody App.UnBindMemberArgs args) {
        appService.unBindMember(args);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Log("获取应用成员角色组")
    @ApiOperation("获取应用成员角色组")
    @PostMapping("/queryMemberRoleGroup")
    public ResponseEntity<Object> queryMemberRoleGroup(@Validated @RequestBody App.QueryMemberRoleGroup args) {
        return new ResponseEntity<>(appService.queryMemberRoleGroup(args), HttpStatus.OK);
    }

    @ApiOperation("获取应用角色列表")
    @PostMapping("/queryRoleList")
    public ResponseEntity<Object> queryRoleList() {
        return new ResponseEntity<>(appService.queryRoleList(), HttpStatus.OK);
    }

    @ApiOperation("获取应用等级列表")
    @PostMapping("/queryLevelList")
    public ResponseEntity<Object> queryLevelList() {
        return new ResponseEntity<>(appService.queryLevelList(), HttpStatus.OK);
    }

    @ApiOperation("获取应用语言列表")
    @PostMapping("/queryLanguageList")
    public ResponseEntity<Object> queryLanguageList() {
        return new ResponseEntity<>(appService.queryLanguageList(), HttpStatus.OK);
    }

    @ApiOperation("根据关键字分页获取项目地址")
    @PostMapping("/queryProjectUrlList")
    public ResponseEntity<Object> queryProjectUrlList(@RequestBody App.QueryProjectUrlListArgs args) {
        return new ResponseEntity<>(appService.queryProjectUrlList(args), HttpStatus.OK);
    }
}
