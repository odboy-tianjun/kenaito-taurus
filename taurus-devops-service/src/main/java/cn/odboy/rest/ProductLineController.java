package cn.odboy.rest;

import cn.odboy.annotation.Log;
import cn.odboy.domain.ProductLine;
import cn.odboy.model.PageArgs;
import cn.odboy.service.ProductLineService;
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
 * 产品线 前端控制器
 * </p>
 *
 * @author odboy
 * @since 2024-09-13
 */
@RestController
@RequiredArgsConstructor
@Api(tags = "DevOps：产品线管理")
@RequestMapping("/api/devops/productLine")
public class ProductLineController {
    private final ProductLineService productLineService;

    @ApiOperation("获取产品线树")
    @PostMapping("/getTree")
    public ResponseEntity<Object> getTree() {
        return new ResponseEntity<>(productLineService.getTree(), HttpStatus.OK);
    }

    @ApiOperation("分页获取产品线列表")
    @PostMapping("/queryPage")
    public ResponseEntity<Object> queryPage(@RequestBody PageArgs<ProductLine> args) {
        return new ResponseEntity<>(productLineService.queryPage(args), HttpStatus.OK);
    }

    @ApiOperation("获取所有产品线列表")
    @PostMapping("/queryList")
    public ResponseEntity<Object> queryList() {
        return new ResponseEntity<>(productLineService.queryList(), HttpStatus.OK);
    }

    @ApiOperation("获取所有产品线路径列表")
    @PostMapping("/queryPathList")
    public ResponseEntity<Object> queryPathList() {
        return new ResponseEntity<>(productLineService.queryPathList(), HttpStatus.OK);
    }

    @Log("新增产品线")
    @ApiOperation("新增产品线")
    @PostMapping("/create")
    @PreAuthorize("@el.check('productLine:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody ProductLine.CreateArgs args) {
        productLineService.create(args);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @Log("修改产品线")
    @ApiOperation("修改产品线")
    @PostMapping("/modify")
    @PreAuthorize("@el.check('productLine:edit')")
    public ResponseEntity<Object> modify(@Validated @RequestBody ProductLine.ModifyArgs args) {
        productLineService.modify(args);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Log("删除产品线")
    @ApiOperation("删除产品线")
    @PostMapping("/remove")
    @PreAuthorize("@el.check('productLine:del')")
    public ResponseEntity<Object> remove(@Validated @RequestBody ProductLine.RemoveByIdsArgs args) {
        productLineService.remove(args);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
