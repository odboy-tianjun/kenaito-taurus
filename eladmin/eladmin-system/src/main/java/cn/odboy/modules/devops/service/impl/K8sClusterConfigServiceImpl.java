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
package cn.odboy.modules.devops.service.impl;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.StrUtil;
import cn.odboy.constant.K8sClusterStatusEnum;
import cn.odboy.context.K8sClientAdmin;
import cn.odboy.context.K8sConfigHelper;
import cn.odboy.context.K8sHealthChecker;
import cn.odboy.infra.mybatisplus.impl.EasyServiceImpl;
import cn.odboy.model.CommonModel;
import cn.odboy.model.MetaOption;
import cn.odboy.model.PageResult;
import cn.odboy.modules.devops.domain.K8sClusterConfig;
import cn.odboy.modules.devops.mapper.K8sClusterConfigMapper;
import cn.odboy.modules.devops.service.K8sClusterConfigService;
import cn.odboy.util.PageUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.kubernetes.client.openapi.ApiClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * k8s集群配置 服务实现类
 * </p>
 *
 * @author codegen
 * @since 2025-01-12
 */
@Service
@RequiredArgsConstructor
public class K8sClusterConfigServiceImpl extends EasyServiceImpl<K8sClusterConfigMapper, K8sClusterConfig> implements K8sClusterConfigService {
    private final K8sClientAdmin k8sClientAdmin;
    private final K8sHealthChecker k8sHealthChecker;
    private final K8sConfigHelper k8sConfigHelper;

    @Override
    public PageResult<K8sClusterConfig> searchClusterConfigs(K8sClusterConfig args, Page<K8sClusterConfig> page) {
        LambdaQueryWrapper<K8sClusterConfig> wrapper = new LambdaQueryWrapper<>();
        if (args != null) {
            wrapper.like(StrUtil.isNotBlank(args.getClusterCode()), K8sClusterConfig::getClusterCode, args.getClusterCode());
            wrapper.like(StrUtil.isNotBlank(args.getClusterName()), K8sClusterConfig::getClusterName, args.getClusterName());
            wrapper.eq(StrUtil.isNotBlank(args.getEnvCode()), K8sClusterConfig::getEnvCode, args.getEnvCode());
        }
        return PageUtil.toPage(baseMapper.selectPage(page, wrapper));
    }

    @Override
    public List<MetaOption> listClusterCodes() {
        return quickList(null).stream()
                .map(m -> MetaOption.builder()
                        .label(m.getClusterName())
                        .value(m.getClusterCode())
                        .ext(Dict.create()
                                .set("envCode", m.getEnvCode())
                                .set("status", m.getStatus())
                        )
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteClusterConfigs(CommonModel.IdsArgs args) {
        Long id = args.getIds().stream().findFirst().get();
        K8sClusterConfig localClusterConfig = getById(id);
        Assert.notNull(localClusterConfig, "非法操作");
        removeById(id);
        k8sClientAdmin.deleteClient(localClusterConfig.getClusterCode());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createClusterConfig(K8sClusterConfig.CreateArgs args) {
        ApiClient apiClient = k8sConfigHelper.loadFormContent(args.getConfigContent());
        k8sHealthChecker.checkConfigContent(apiClient);

        String clusterCode = args.getClusterCode();
        boolean exists = lambdaQuery().eq(K8sClusterConfig::getClusterCode, clusterCode).exists();
        Assert.isFalse(exists, "集群编码已存在");

        K8sClusterConfig record = new K8sClusterConfig();
        record.setEnvCode(args.getEnvCode());
        record.setClusterCode(args.getClusterCode());
        record.setClusterName(args.getClusterName());
        record.setConfigContent(args.getConfigContent().getBytes(StandardCharsets.UTF_8));
        record.setStatus(K8sClusterStatusEnum.HEALTH.getCode());
        save(record);

        k8sClientAdmin.putClientEnv(clusterCode, args.getEnvCode(), apiClient);
    }
}
