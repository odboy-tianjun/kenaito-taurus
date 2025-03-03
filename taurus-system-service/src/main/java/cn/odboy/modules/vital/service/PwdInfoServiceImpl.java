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
import cn.hutool.core.util.StrUtil;
import cn.odboy.common.model.CommonModel;
import cn.odboy.exception.BadRequestException;
import cn.odboy.mybatisplus.model.PageResult;
import cn.odboy.vital.domain.PwdInfo;
import cn.odboy.modules.vital.mapper.PwdInfoMapper;
import cn.odboy.vital.service.PwdInfoService;
import cn.odboy.vital.util.PasswordGeneratorUtil;
import cn.odboy.system.core.util.PageUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 * 密码管理 服务实现类
 * </p>
 *
 * @author codegen
 * @since 2025-01-20
 */
@Service
@RequiredArgsConstructor
public class PwdInfoServiceImpl extends ServiceImpl<PwdInfoMapper, PwdInfo> implements PwdInfoService {
    private final PasswordGeneratorUtil passwordGeneratorUtil;

    @Override
    public PageResult<PwdInfo> searchPwdInfos(PwdInfo args, Page<PwdInfo> page) {
        LambdaQueryWrapper<PwdInfo> wrapper = new LambdaQueryWrapper<>();
        if (args != null) {
            wrapper.like(StrUtil.isNotBlank(args.getRemark()), PwdInfo::getRemark, args.getRemark());
        }
        Page<PwdInfo> pwdInfoPage = baseMapper.selectPage(page, wrapper);
        pwdInfoPage.getRecords().forEach(c -> c.setPassword("******"));
        return PageUtil.toPage(pwdInfoPage);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createPwdInfo(PwdInfo.CreateArgs args) {
        try {
            PwdInfo pwdInfo = BeanUtil.copyProperties(args, PwdInfo.class);
            save(pwdInfo);
        } catch (Exception e) {
            log.error("添加账号失败", e);
            throw new BadRequestException("添加账号失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deletePwdInfos(CommonModel.IdsArgs args) {
        try {
            removeByIds(args.getIds());
        } catch (Exception e) {
            log.error("删除账号失败", e);
            throw new BadRequestException("删除账号失败");
        }
    }

    @Override
    public PwdInfo.PasswordContent generatePassword(PwdInfo.GeneratorRuleArgs args) {
        return passwordGeneratorUtil.generator(args);
    }
}
