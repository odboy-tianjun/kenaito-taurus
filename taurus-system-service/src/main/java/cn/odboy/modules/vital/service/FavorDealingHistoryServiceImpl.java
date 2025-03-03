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
package cn.odboy.modules.vital.service;

import cn.hutool.core.bean.BeanUtil;
import cn.odboy.exception.BadRequestException;
import cn.odboy.common.model.CommonModel;
import cn.odboy.mybatisplus.model.PageResult;
import cn.odboy.vital.domain.FavorDealingHistory;
import cn.odboy.modules.vital.mapper.FavorDealingHistoryMapper;
import cn.odboy.vital.service.FavorDealingHistoryService;
import cn.odboy.system.core.util.PageUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 * 人情往来 服务实现类
 * </p>
 *
 * @author codegen
 * @since 2025-01-20
 */
@Service
public class FavorDealingHistoryServiceImpl extends ServiceImpl<FavorDealingHistoryMapper, FavorDealingHistory> implements FavorDealingHistoryService {

    @Override
    public PageResult<FavorDealingHistory.SearchFavorDealingHistorys> searchFavorDealingHistorys(FavorDealingHistory args, Page<FavorDealingHistory> page) {
        return PageUtil.toPage(baseMapper.selectFavorDealingHistorys(page, args));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createFavorDealingHistory(FavorDealingHistory.CreateArgs args) {
        try {
            save(BeanUtil.copyProperties(args, FavorDealingHistory.class));
        } catch (Exception e) {
            log.error("添加人情来往历史记录失败", e);
            throw new BadRequestException("添加人情来往历史记录失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateFavorDealingHistory(FavorDealingHistory.UpdateArgs args) {
        try {
            updateById(BeanUtil.copyProperties(args, FavorDealingHistory.class));
        } catch (Exception e) {
            log.error("更新人情来往历史记录失败", e);
            throw new BadRequestException("更新人情来往历史记录失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteFavorDealingHistorys(CommonModel.IdsArgs args) {
        try {
            removeByIds(args.getIds());
        } catch (Exception e) {
            log.error("删除人情来往历史记录失败", e);
            throw new BadRequestException("删除人情来往历史记录失败");
        }
    }
}
