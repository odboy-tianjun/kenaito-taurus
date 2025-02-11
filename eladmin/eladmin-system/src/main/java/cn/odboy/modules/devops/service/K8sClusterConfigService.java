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
package cn.odboy.modules.devops.service;

import cn.odboy.infra.mybatisplus.EasyService;
import cn.odboy.model.CommonModel;
import cn.odboy.model.MetaOption;
import cn.odboy.model.PageResult;
import cn.odboy.modules.devops.domain.K8sClusterConfig;
import cn.odboy.modules.vital.domain.BirthDate;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.List;

/**
 * <p>
 * k8s集群配置 服务类
 * </p>
 *
 * @author codegen
 * @since 2025-01-12
 */
public interface K8sClusterConfigService extends EasyService<K8sClusterConfig> {
    /**
     * 查询集群配置编码元数据
     *
     * @return /
     */
    List<MetaOption> listClusterCodes();
    void deleteClusterConfigs(CommonModel.IdsArgs args);
    void createClusterConfig(K8sClusterConfig.CreateArgs args);
    PageResult<K8sClusterConfig> searchClusterConfigs(K8sClusterConfig args, Page<K8sClusterConfig> page);
}
