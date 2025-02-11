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
package cn.odboy.modules.vital.rest;

import cn.odboy.annotation.Log;
import cn.odboy.model.CommonModel;
import cn.odboy.model.PageArgs;
import cn.odboy.modules.vital.domain.ContactInfo;
import cn.odboy.modules.vital.service.ContactInfoService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 联系人管理 前端控制器
 * </p>
 *
 * @author codegen
 * @since 2025-01-20
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/vital/contactInfo")
public class ContactInfoController {
    private final ContactInfoService contactInfoService;

    @ApiOperation(value = "查询联系人")
    @PostMapping(value = "searchContactInfos")
    @PreAuthorize("@el.check('vital:contactInfo:list')")
    public ResponseEntity<Object> searchContactInfos(@Validated @RequestBody PageArgs<ContactInfo> args) {
        Page<ContactInfo> page = new Page<>(args.getCurrent(), args.getSize());
        return new ResponseEntity<>(contactInfoService.searchContactInfos(args.getArgs(), page), HttpStatus.OK);
    }

    @Log("新增联系人")
    @ApiOperation(value = "新增联系人")
    @PostMapping(value = "createContactInfo")
    @PreAuthorize("@el.check('vital:contactInfo:create')")
    public ResponseEntity<Object> createContactInfo(@Validated @RequestBody ContactInfo.CreateArgs args) {
        contactInfoService.createContactInfo(args);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @Log("修改联系人")
    @ApiOperation(value = "修改联系人")
    @PostMapping(value = "updateContactInfo")
    @PreAuthorize("@el.check('vital:contactInfo:update')")
    public ResponseEntity<Object> updateContactInfo(@Validated @RequestBody ContactInfo.UpdateArgs args) {
        contactInfoService.updateContactInfo(args);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除联系人")
    @ApiOperation(value = "删除联系人")
    @PostMapping(value = "deleteContactInfos")
    @PreAuthorize("@el.check('vital:contactInfo:delete')")
    public ResponseEntity<Object> deleteContactInfos(@Validated @RequestBody CommonModel.IdsArgs args) {
        contactInfoService.deleteContactInfos(args);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping(value = "listMetaContactInfos")
    public ResponseEntity<Object> listMetaContactInfos(@Validated @RequestBody PageArgs<ContactInfo> args) {
        Page<ContactInfo> page = new Page<>(args.getCurrent(), args.getSize());
        return new ResponseEntity<>(contactInfoService.listMetaContactInfos(args.getArgs(), page), HttpStatus.OK);
    }
}
