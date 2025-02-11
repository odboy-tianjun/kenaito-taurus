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
package cn.odboy.modules.devops.rest;

import cn.odboy.annotation.Log;
import cn.odboy.model.CommonModel;
import cn.odboy.model.PageArgs;
import cn.odboy.modules.devops.domain.CmdbApp;
import cn.odboy.modules.devops.service.CmdbAppService;
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
 * 应用列表 前端控制器
 * </p>
 *
 * @author codegen
 * @since 2025-02-11
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/cmdb/app")
public class CmdbAppController {
    private final CmdbAppService cmdbAppService;

    @ApiOperation(value = "查询应用")
    @PostMapping(value = "searchApps")
    @PreAuthorize("@el.check('devops:cmdb:app:list')")
    public ResponseEntity<Object> searchApps(@Validated @RequestBody PageArgs<CmdbApp> args) {
        Page<CmdbApp> page = new Page<>(args.getCurrent(), args.getSize());
        return new ResponseEntity<>(cmdbAppService.searchApps(args.getArgs(), page), HttpStatus.OK);
    }

    @Log("创建应用")
    @ApiOperation(value = "创建应用")
    @PostMapping(value = "createApp")
    @PreAuthorize("@el.check('devops:cmdb:app:create')")
    public ResponseEntity<Object> createApp(@Validated @RequestBody CmdbApp.CreateArgs args) {
        cmdbAppService.createApp(args);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @Log("应用下线")
    @ApiOperation(value = "应用下线")
    @PostMapping(value = "offlineApp")
    @PreAuthorize("@el.check('devops:cmdb:app:offline')")
    public ResponseEntity<Object> offlineApp(@Validated @RequestBody CommonModel.IdArgs args) {
        cmdbAppService.offlineApp(args);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
