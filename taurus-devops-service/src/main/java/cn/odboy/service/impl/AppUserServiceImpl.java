package cn.odboy.service.impl;

import cn.odboy.domain.App;
import cn.odboy.domain.AppUser;
import cn.odboy.mapper.AppUserMapper;
import cn.odboy.service.AppUserService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 应用、成员关联 服务实现类
 * </p>
 *
 * @author odboy
 * @since 2024-09-13
 */
@Service
public class AppUserServiceImpl extends ServiceImpl<AppUserMapper, AppUser> implements AppUserService {
    @Override
    public void removeBy(Long appId, String roleCode, Long userId) {
        getBaseMapper().delete(new LambdaQueryWrapper<AppUser>()
                .eq(AppUser::getAppId, appId)
                .eq(AppUser::getRoleCode, roleCode)
                .eq(AppUser::getUserId, userId)
        );
    }

    @Override
    public boolean existsBy(Long appId, String roleCode) {
        return getBaseMapper().exists(new LambdaQueryWrapper<AppUser>()
                .eq(AppUser::getAppId, appId)
                .eq(AppUser::getRoleCode, roleCode)
        );
    }

    @Override
    public List<App.QueryMemberRoleGroup> queryMemberRoleGroup(App.QueryMemberRoleGroup args) {
        return getBaseMapper().selectMemberRoleGroup(args.getId());
    }
}
