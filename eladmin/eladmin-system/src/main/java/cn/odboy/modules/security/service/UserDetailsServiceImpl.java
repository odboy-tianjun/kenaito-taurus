/*
 *  Copyright 2019-2020 Zheng Jie
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
package cn.odboy.modules.security.service;

import cn.odboy.infra.exception.BadRequestException;
import cn.odboy.infra.exception.EntityNotFoundException;
import cn.odboy.modules.security.service.dto.JwtUserDto;
import cn.odboy.modules.system.domain.User;
import cn.odboy.modules.system.service.DataService;
import cn.odboy.modules.system.service.RoleService;
import cn.odboy.modules.system.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * @author Zheng Jie
 * @date 2018-11-22
 */
@Slf4j
@RequiredArgsConstructor
@Service("userDetailsService")
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserService userService;
    private final RoleService roleService;
    private final DataService dataService;
    private final OnlineUserService onlineUserService;

    @Override
    public JwtUserDto loadUserByUsername(String username) {
        JwtUserDto jwtUserDto = onlineUserService.getUserCache(username);
        if (jwtUserDto == null) {
            try {
                User user = userService.getLoginData(username);
                if (user == null) {
                    throw new UsernameNotFoundException("");
                }
                if (!user.getEnabled()) {
                    throw new BadRequestException("账号未激活！");
                }
                jwtUserDto = new JwtUserDto(
                        user,
                        dataService.getDeptIds(user),
                        roleService.mapToGrantedAuthorities(user)
                );
                // 添加缓存数据
                onlineUserService.addUserCache(username, jwtUserDto);
            } catch (EntityNotFoundException e) {
                // SpringSecurity会自动转换UsernameNotFoundException为BadCredentialsException
                throw new UsernameNotFoundException(username, e);
            }
        }
        return jwtUserDto;
    }
}
