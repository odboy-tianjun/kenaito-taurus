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
package cn.odboy.modules.vital.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.odboy.infra.exception.BadRequestException;
import cn.odboy.model.CommonModel;
import cn.odboy.model.MetaOption;
import cn.odboy.model.PageResult;
import cn.odboy.modules.vital.domain.ContactInfo;
import cn.odboy.modules.vital.mapper.ContactInfoMapper;
import cn.odboy.modules.vital.service.ContactInfoService;
import cn.odboy.util.PageUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 联系人管理 服务实现类
 * </p>
 *
 * @author codegen
 * @since 2025-01-20
 */
@Service
public class ContactInfoServiceImpl extends ServiceImpl<ContactInfoMapper, ContactInfo> implements ContactInfoService {

    @Override
    public PageResult<ContactInfo> searchContactInfos(ContactInfo args, Page<ContactInfo> page) {
        LambdaQueryWrapper<ContactInfo> wrapper = new LambdaQueryWrapper<>();
        if (args != null) {
            wrapper.like(StrUtil.isNotBlank(args.getRealName()), ContactInfo::getRealName, args.getRealName());
        }
        return PageUtil.toPage(baseMapper.selectPage(page, wrapper));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createContactInfo(ContactInfo.CreateArgs args) {
        try {
            ContactInfo contactInfo = BeanUtil.copyProperties(args, ContactInfo.class);
            if (contactInfo.getTagName() == null) {
                contactInfo.setTagName("");
            }
            save(contactInfo);
        } catch (Exception e) {
            log.error("添加联系人失败", e);
            throw new BadRequestException("添加联系人失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateContactInfo(ContactInfo.UpdateArgs args) {
        try {
            updateById(BeanUtil.copyProperties(args, ContactInfo.class));
        } catch (Exception e) {
            log.error("更新联系人失败", e);
            throw new BadRequestException("更新联系人失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteContactInfos(CommonModel.IdsArgs args) {
        try {
            removeByIds(args.getIds());
        } catch (Exception e) {
            log.error("删除联系人失败", e);
            throw new BadRequestException("删除联系人失败");
        }
    }

    @Override
    public List<MetaOption> listMetaContactInfos(ContactInfo args, Page<ContactInfo> page) {
        return searchContactInfos(args, page)
                .getContent()
                .stream()
                .map(m -> MetaOption.builder()
                        .label(m.getRealName())
                        .value(String.valueOf(m.getId()))
                        .build())
                .collect(Collectors.toList());
    }
}
