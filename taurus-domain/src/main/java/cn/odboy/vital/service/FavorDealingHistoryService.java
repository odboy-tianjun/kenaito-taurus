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
package cn.odboy.vital.service;

import cn.odboy.common.model.CommonModel;
import cn.odboy.mybatisplus.model.PageResult;
import cn.odboy.vital.domain.FavorDealingHistory;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 人情往来 服务类
 * </p>
 *
 * @author codegen
 * @since 2025-01-20
 */
public interface FavorDealingHistoryService extends IService<FavorDealingHistory> {

    PageResult<FavorDealingHistory.SearchFavorDealingHistorys> searchFavorDealingHistorys(FavorDealingHistory args, Page<FavorDealingHistory> page);

    void createFavorDealingHistory(FavorDealingHistory.CreateArgs args);

    void updateFavorDealingHistory(FavorDealingHistory.UpdateArgs args);

    void deleteFavorDealingHistorys(CommonModel.IdsArgs args);
}
