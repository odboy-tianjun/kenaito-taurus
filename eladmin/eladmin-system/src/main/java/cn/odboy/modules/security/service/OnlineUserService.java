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

import cn.hutool.core.util.RandomUtil;
import cn.odboy.model.PageResult;
import cn.odboy.modules.security.config.SecurityProperties;
import cn.odboy.modules.security.security.TokenProvider;
import cn.odboy.modules.security.service.dto.JwtUserDto;
import cn.odboy.modules.security.service.dto.OnlineUserDto;
import cn.odboy.util.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author Zheng Jie
 * @date 2019年10月26日21:56:27
 */
@Service
@Slf4j
@AllArgsConstructor
public class OnlineUserService {
    private final SecurityProperties properties;
    private final TokenProvider tokenProvider;
    private final RedisUtil redisUtil;

    /**
     * 保存在线用户信息
     *
     * @param jwtUserDto /
     * @param token      /
     * @param request    /
     */
    public void save(JwtUserDto jwtUserDto, String token, HttpServletRequest request) {
        String dept = jwtUserDto.getUser().getDept().getName();
        String ip = StringUtil.getIp(request);
        String browser = StringUtil.getBrowser(request);
        String address = StringUtil.getCityInfo(ip);
        OnlineUserDto onlineUserDto = null;
        try {
            onlineUserDto = new OnlineUserDto();
            onlineUserDto.setUserName(jwtUserDto.getUsername());
            onlineUserDto.setNickName(jwtUserDto.getUser().getNickName());
            onlineUserDto.setDept(dept);
            onlineUserDto.setBrowser(browser);
            onlineUserDto.setIp(ip);
            onlineUserDto.setAddress(address);
            onlineUserDto.setKey(EncryptUtil.desEncrypt(token));
            onlineUserDto.setLoginTime(new Date());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        String loginKey = tokenProvider.loginKey(token);
        redisUtil.set(loginKey, onlineUserDto, properties.getTokenValidityInSeconds(), TimeUnit.MILLISECONDS);
        redisUtil.set(String.format("user:token:%s", token), jwtUserDto.getUsername());
    }

    /**
     * 查询全部数据
     *
     * @param username /
     * @param pageable /
     * @return /
     */
    public PageResult<OnlineUserDto> getAll(String username, Pageable pageable) {
        List<OnlineUserDto> onlineUsers = getAll(username);
        return PageUtil.toPage(
                PageUtil.paging(pageable.getPageNumber(), pageable.getPageSize(), onlineUsers),
                onlineUsers.size()
        );
    }

    /**
     * 查询全部数据，不分页
     *
     * @param username /
     * @return /
     */
    public List<OnlineUserDto> getAll(String username) {
        String loginKey = properties.getOnlineKey() + (StringUtil.isBlank(username) ? "" : "*" + username);
        List<String> keys = redisUtil.scan(loginKey + "*");
        Collections.reverse(keys);
        List<OnlineUserDto> onlineUsers = new ArrayList<>();
        for (String key : keys) {
            onlineUsers.add(redisUtil.get(key, OnlineUserDto.class));
        }
        onlineUsers.sort((o1, o2) -> o2.getLoginTime().compareTo(o1.getLoginTime()));
        return onlineUsers;
    }

    /**
     * 退出登录
     *
     * @param token /
     */
    public void logout(String token) {
        String loginKey = tokenProvider.loginKey(token);
        redisUtil.del(loginKey);
        String username = redisUtil.get(String.format("user:token:%s", token), String.class);
        redisUtil.del(String.format("user:login:%s", username));
    }

    /**
     * 导出
     *
     * @param all      /
     * @param response /
     * @throws IOException /
     */
    public void download(List<OnlineUserDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (OnlineUserDto user : all) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("用户名", user.getUserName());
            map.put("部门", user.getDept());
            map.put("登录IP", user.getIp());
            map.put("登录地点", user.getAddress());
            map.put("浏览器", user.getBrowser());
            map.put("登录日期", user.getLoginTime());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    /**
     * 查询用户
     *
     * @param key /
     * @return /
     */
    public OnlineUserDto getOne(String key) {
        return redisUtil.get(key, OnlineUserDto.class);
    }

    /**
     * 根据用户名强退用户
     *
     * @param username /
     */
    public void kickOutForUsername(String username) {
        String loginKey = properties.getOnlineKey() + username + "*";
        redisUtil.scanDel(loginKey);
        redisUtil.del(String.format("user:login:%s", username));
    }

    public void addUserCache(String username, JwtUserDto jwtUserDto) {
        int randomInt = RandomUtil.randomInt(900, 7200);
        redisUtil.set(String.format("user:login:%s", username), jwtUserDto, randomInt);
    }

    public JwtUserDto getUserCache(String username) {
        String loginKey = String.format("user:login:%s", username);
        return redisUtil.get(loginKey, JwtUserDto.class);
    }
}
