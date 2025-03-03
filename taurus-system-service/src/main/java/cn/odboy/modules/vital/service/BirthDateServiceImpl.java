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
import cn.odboy.vital.constant.BirthDateComputeModeEnum;
import cn.odboy.vital.domain.BirthDate;
import cn.odboy.modules.vital.mapper.BirthDateMapper;
import cn.odboy.vital.service.BirthDateService;
import cn.odboy.system.core.util.PageUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 * 生日管理 服务实现类
 * </p>
 *
 * @author codegen
 * @since 2025-01-20
 */
@Service
public class BirthDateServiceImpl extends ServiceImpl<BirthDateMapper, BirthDate> implements BirthDateService {
    @Override
    public PageResult<BirthDate.SearchBirthDates> searchBirthDates(BirthDate.SearchBirthDates args, Page<BirthDate> page) {
        return PageUtil.toPage(baseMapper.selectBirthDates(page, args));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createBirthDate(BirthDate.CreateArgs args) {
        try {
            if (BirthDateComputeModeEnum.LUNAR.getCode().equals(args.getComputeMode()) && args.getRegexStr().contains("null")) {
                throw new BadRequestException("表达式不完整, 请选择年月日～");
            }
            save(BeanUtil.copyProperties(args, BirthDate.class));
        } catch (Exception e) {
            log.error("添加生日失败", e);
            throw new BadRequestException("添加生日失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteBirthDates(CommonModel.IdsArgs args) {
        try {
            removeByIds(args.getIds());
        } catch (Exception e) {
            log.error("删除生日失败", e);
            throw new BadRequestException("删除生日失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void modifyBirthDateNotifyStatus(BirthDate.ModifyBirthDateNotifyStatusArgs args) {
        try {
            updateById(BeanUtil.copyProperties(args, BirthDate.class));
        } catch (Exception e) {
            log.error("更新生日通知开关失败", e);
            throw new BadRequestException("更新生日通知开关失败");
        }
    }
}
