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
package cn.odboy.modules.devops.rest;

import cn.odboy.model.PageArgs;
import cn.odboy.repository.GitlabGroupRepository;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.gitlab4j.api.models.Group;
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
 * gitlab 前端控制器
 * </p>
 *
 * @author codegen
 * @since 2025-02-11
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/cmdb/gitlab")
public class CmdbGitlabController {
    private final GitlabGroupRepository gitlabGroupRepository;

    @ApiOperation(value = "查询GitGroup")
    @PostMapping(value = "listGitGroups")
    @PreAuthorize("@el.check('devops:cmdb:gitlab:list')")
    public ResponseEntity<Object> listGitGroups(@Validated @RequestBody PageArgs<Group> args) {
        return new ResponseEntity<>(gitlabGroupRepository.listGroups(args.getCurrent()), HttpStatus.OK);
    }

    @ApiOperation(value = "查询GitProject")
    @PostMapping(value = "listGitGroupProjects")
    @PreAuthorize("@el.check('devops:cmdb:gitlab:list')")
    public ResponseEntity<Object> listGitGroupProjects(@Validated @RequestBody PageArgs<Group> args) {
        return new ResponseEntity<>(gitlabGroupRepository.listProjects(args.getArgs().getId(), args.getCurrent()), HttpStatus.OK);
    }
}
