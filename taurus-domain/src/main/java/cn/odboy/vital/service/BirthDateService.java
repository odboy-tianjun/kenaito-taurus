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
import cn.odboy.vital.domain.BirthDate;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 生日管理 服务类
 * </p>
 *
 * @author codegen
 * @since 2025-01-20
 */
public interface BirthDateService extends IService<BirthDate> {
    PageResult<BirthDate.SearchBirthDates> searchBirthDates(BirthDate.SearchBirthDates args, Page<BirthDate> page);
    void createBirthDate(BirthDate.CreateArgs args);
    void deleteBirthDates(CommonModel.IdsArgs args);
    void modifyBirthDateNotifyStatus(BirthDate.ModifyBirthDateNotifyStatusArgs args);
}
