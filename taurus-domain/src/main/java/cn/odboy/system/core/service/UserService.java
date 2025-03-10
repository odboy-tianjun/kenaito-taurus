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
package cn.odboy.system.core.service;

import cn.odboy.mybatisplus.model.PageResult;
import cn.odboy.system.core.domain.User;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Zheng Jie
 * @date 2018-11-23
 */
public interface UserService extends IService<User> {
    /**
     * 根据ID查询
     *
     * @param id ID
     * @return /
     */
    User findById(long id);

    /**
     * 新增用户
     *
     * @param resources /
     */
    void create(User resources);

    /**
     * 编辑用户
     *
     * @param resources /
     * @throws Exception /
     */
    void update(User resources) throws Exception;

    /**
     * 删除用户
     *
     * @param ids /
     */
    void delete(Set<Long> ids);

    /**
     * 根据用户名查询
     *
     * @param userName /
     * @return /
     */
    User findByName(String userName);

    /**
     * 根据用户名查询
     *
     * @param userName /
     * @return /
     */
    User getLoginData(String userName);

    /**
     * 修改密码
     *
     * @param username        用户名
     * @param encryptPassword 密码
     */
    void updatePass(String username, String encryptPassword);

    /**
     * 修改头像
     *
     * @param file 文件
     * @return /
     */
    Map<String, String> updateAvatar(MultipartFile file);

    /**
     * 修改邮箱
     *
     * @param username 用户名
     * @param email    邮箱
     */
    void updateEmail(String username, String email);

    /**
     * 查询全部
     *
     * @param criteria 条件
     * @param page     分页参数
     * @return /
     */
    PageResult<User> queryAll(User.QueryArgs criteria, Page<Object> page);

    /**
     * 查询全部不分页
     *
     * @param criteria 条件
     * @return /
     */
    List<User> queryAll(User.QueryArgs criteria);

    /**
     * 导出数据
     *
     * @param queryAll 待导出的数据
     * @param response /
     * @throws IOException /
     */
    void download(List<User> queryAll, HttpServletResponse response) throws IOException;

    /**
     * 用户自助修改资料
     *
     * @param resources /
     */
    void updateCenter(User resources);

    /**
     * 重置密码
     *
     * @param ids 用户id
     * @param pwd 密码
     */
    void resetPwd(Set<Long> ids, String pwd);

    /**
     * 提供前端分页查询用户列表
     */
    PageResult<User> pageList(User.QueryArgs body, Page<User> page);
}
