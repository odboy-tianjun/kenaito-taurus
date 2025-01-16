/*
 *  Copyright 2022-2025 Tian Jun
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

import cn.odboy.infra.security.annotation.AnonymousAccess;
import cn.odboy.model.MetaOption;
import cn.odboy.modules.devops.service.K8sClusterConfigService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
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

    @AnonymousAccess
    @ApiOperation("查询集群配置编码元数据")
    @PostMapping(value = "/listClusterCodes")
    public ResponseEntity<List<MetaOption>> listClusterCodes() {
        return new ResponseEntity<>(k8sClusterConfigService.listClusterCodes(), HttpStatus.OK);
    }
}
