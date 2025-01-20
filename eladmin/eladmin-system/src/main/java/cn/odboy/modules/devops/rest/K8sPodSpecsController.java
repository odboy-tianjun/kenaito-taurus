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
import cn.odboy.model.MetaOption;
import cn.odboy.model.PageArgs;
import cn.odboy.modules.devops.domain.K8sPodSpecs;
import cn.odboy.modules.devops.service.K8sPodSpecsService;
import cn.odboy.modules.devops.service.K8sPodSpecsService;
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

import java.util.List;

/**
 * <p>
 * K8sPod规格 前端控制器
 * </p>
 *
 * @author codegen
 * @since 2025-02-11
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/k8s/podSpecs")
public class K8sPodSpecsController {
    private final K8sPodSpecsService k8sPodSpecsService;

    @ApiOperation(value = "查询K8sPod规格")
    @PostMapping(value = "searchPodSpecss")
    @PreAuthorize("@el.check('devops:k8s:podSpecs:list')")
    public ResponseEntity<Object> searchPodSpecss(@Validated @RequestBody PageArgs<K8sPodSpecs> args) {
        Page<K8sPodSpecs> page = new Page<>(args.getCurrent(), args.getSize());
        return new ResponseEntity<>(k8sPodSpecsService.searchPodSpecss(args.getArgs(), page), HttpStatus.OK);
    }

    @Log("新增K8sPod规格")
    @ApiOperation(value = "新增K8sPod规格")
    @PostMapping(value = "createPodSpecs")
    @PreAuthorize("@el.check('devops:k8s:podSpecs:create')")
    public ResponseEntity<Object> createPodSpecs(@Validated @RequestBody K8sPodSpecs.CreateArgs args) {
        k8sPodSpecsService.createPodSpecs(args);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @Log("删除K8sPod规格")
    @ApiOperation(value = "删除K8sPod规格")
    @PostMapping(value = "deletePodSpecss")
    @PreAuthorize("@el.check('devops:k8s:podSpecs:delete')")
    public ResponseEntity<Object> deletePodSpecss(@Validated @RequestBody CommonModel.IdArgs args) {
        k8sPodSpecsService.deletePodSpecss(args);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Log("启用K8sPod规格")
    @ApiOperation(value = "启用K8sPod规格")
    @PostMapping(value = "enablePodSpecs")
    @PreAuthorize("@el.check('devops:k8s:podSpecs:enable')")
    public ResponseEntity<Object> enablePodSpecs(@Validated @RequestBody CommonModel.IdArgs args) {
        k8sPodSpecsService.enablePodSpecs(args);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Log("禁用K8sPod规格")
    @ApiOperation(value = "禁用K8sPod规格")
    @PostMapping(value = "disablePodSpecs")
    @PreAuthorize("@el.check('devops:k8s:podSpecs:disable')")
    public ResponseEntity<Object> disablePodSpecs(@Validated @RequestBody CommonModel.IdArgs args) {
        k8sPodSpecsService.disablePodSpecs(args);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
