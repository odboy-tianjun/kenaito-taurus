package cn.odboy.service;

import cn.odboy.domain.App;
import cn.odboy.domain.AppUser;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 应用、成员关联 服务类
 * </p>
 *
 * @author odboy
 * @since 2024-09-13
 */
public interface AppUserService extends IService<AppUser> {

    void removeBy(Long appId, String roleCode, Long userId);

    boolean existsBy(Long appId, String roleCode);

    List<App.QueryMemberRoleGroup> queryMemberRoleGroup(App.QueryMemberRoleGroup args);
}
