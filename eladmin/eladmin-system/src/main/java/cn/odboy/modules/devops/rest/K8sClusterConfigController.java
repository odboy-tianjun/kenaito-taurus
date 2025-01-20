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
import cn.odboy.modules.devops.domain.K8sClusterConfig;
import cn.odboy.modules.devops.service.K8sClusterConfigService;
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
 * k8s集群配置 前端控制器
 * </p>
 *
 * @author codegen
 * @since 2025-01-12
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/k8s/clusterConfig")
public class K8sClusterConfigController {
    private final K8sClusterConfigService k8sClusterConfigService;

    @ApiOperation("查询k8s集群配置编码元数据")
    @PostMapping(value = "/listClusterCodes")
    public ResponseEntity<List<MetaOption>> listClusterCodes() {
        return new ResponseEntity<>(k8sClusterConfigService.listClusterCodes(), HttpStatus.OK);
    }

    @ApiOperation(value = "查询k8s集群配置")
    @PostMapping(value = "searchClusterConfigs")
    @PreAuthorize("@el.check('devops:k8s:clusterConfig:list')")
    public ResponseEntity<Object> searchClusterConfigs(@Validated @RequestBody PageArgs<K8sClusterConfig> args) {
        Page<K8sClusterConfig> page = new Page<>(args.getCurrent(), args.getSize());
        return new ResponseEntity<>(k8sClusterConfigService.searchClusterConfigs(args.getArgs(), page), HttpStatus.OK);
    }

    @Log("新增k8s集群配置")
    @ApiOperation(value = "新增k8s集群配置")
    @PostMapping(value = "createClusterConfig")
    @PreAuthorize("@el.check('devops:k8s:clusterConfig:create')")
    public ResponseEntity<Object> createClusterConfig(@Validated @RequestBody K8sClusterConfig.CreateArgs args) {
        k8sClusterConfigService.createClusterConfig(args);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @Log("删除k8s集群配置")
    @ApiOperation(value = "删除k8s集群配置")
    @PostMapping(value = "deleteClusterConfigs")
    @PreAuthorize("@el.check('devops:k8s:clusterConfig:delete')")
    public ResponseEntity<Object> deleteClusterConfigs(@Validated @RequestBody CommonModel.IdsArgs args) {
        k8sClusterConfigService.deleteClusterConfigs(args);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
