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

import cn.odboy.model.CommonModel;
import cn.odboy.model.MetaOption;
import cn.odboy.model.PageResult;
import cn.odboy.modules.vital.domain.ContactInfo;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 联系人管理 服务类
 * </p>
 *
 * @author codegen
 * @since 2025-01-20
 */
public interface ContactInfoService extends IService<ContactInfo> {
    PageResult<ContactInfo> searchContactInfos(ContactInfo args, Page<ContactInfo> page);

    void createContactInfo(ContactInfo.CreateArgs args);

    void updateContactInfo(ContactInfo.UpdateArgs args);

    void deleteContactInfos(CommonModel.IdsArgs args);

    List<MetaOption> listMetaContactInfos(ContactInfo args, Page<ContactInfo> page);
}
