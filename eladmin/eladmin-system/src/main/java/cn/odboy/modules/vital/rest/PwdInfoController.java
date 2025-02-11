/*
 *  Copyright 2021-2025 Tian Jun
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package cn.odboy.modules.vital.rest;

import cn.odboy.annotation.Log;
import cn.odboy.model.CommonModel;
import cn.odboy.model.PageArgs;
import cn.odboy.modules.vital.domain.PwdInfo;
import cn.odboy.modules.vital.service.PwdInfoService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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
 * 密码管理 前端控制器
 * </p>
 *
 * @author codegen
 * @since 2025-01-20
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/vital/pwdInfo")
public class PwdInfoController {
    private final PwdInfoService pwdInfoService;
    @ApiOperation(value = "查询账号")
    @PostMapping(value = "searchPwdInfos")
    @PreAuthorize("@el.check('vital:pwdInfo:list')")
    public ResponseEntity<Object> searchPwdInfos(@Validated @RequestBody PageArgs<PwdInfo> args) {
        Page<PwdInfo> page = new Page<>(args.getCurrent(), args.getSize());
        return new ResponseEntity<>(pwdInfoService.searchPwdInfos(args.getArgs(), page), HttpStatus.OK);
    }

    @Log("新增账号")
    @ApiOperation(value = "新增账号")
    @PostMapping(value = "createPwdInfo")
    @PreAuthorize("@el.check('vital:pwdInfo:create')")
    public ResponseEntity<Object> createPwdInfo(@Validated @RequestBody PwdInfo.CreateArgs args) {
        pwdInfoService.createPwdInfo(args);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @Log("密码生成")
    @ApiOperation(value = "密码生成")
    @PostMapping(value = "generatePassword")
    @PreAuthorize("@el.check('vital:pwdInfo:list')")
    public ResponseEntity<Object> generatePassword(@Validated @RequestBody PwdInfo.GeneratorRuleArgs args) {
        return new ResponseEntity<>(pwdInfoService.generatePassword(args), HttpStatus.OK);
    }

    @Log("删除账号")
    @ApiOperation(value = "删除账号")
    @PostMapping(value = "deletePwdInfos")
    @PreAuthorize("@el.check('vital:pwdInfo:delete')")
    public ResponseEntity<Object> deletePwdInfos(@Validated @RequestBody CommonModel.IdsArgs args) {
        pwdInfoService.deletePwdInfos(args);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
