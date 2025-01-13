package me.zhengjie.modules.devops.rest;

import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import me.zhengjie.infra.security.annotation.AnonymousAccess;
import me.zhengjie.model.MetaOption;
import me.zhengjie.modules.devops.service.K8sClusterConfigService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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
