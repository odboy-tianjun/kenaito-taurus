package cn.odboy.service;

//import cn.odboy.base.model.SelectOption;

import cn.odboy.domain.App;
import cn.odboy.model.MetaOption;
import cn.odboy.model.PageArgs;
import com.aliyun.dingtalkworkflow_1_0.models.SelectOption;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 应用 服务类
 * </p>
 *
 * @author odboy
 * @since 2024-09-13
 */
public interface AppService extends IService<App> {
    /**
     * 新建应用
     *
     * @param args /
     */
    void create(App.CreateArgs args);

    /**
     * 分页查询应用
     *
     * @param args /
     * @return /
     */
    IPage<App.QueryPage> queryPage(PageArgs<App> args);

    /**
     * 获取应用等级列表
     *
     * @return /
     */
    List<MetaOption> queryLevelList();

    /**
     * 获取应用语言列表
     *
     * @return /
     */
    List<MetaOption> queryLanguageList();

    /**
     * 收藏应用
     *
     * @param args /
     */
    void changeCollect(App.ChangeCollectArgs args);

    /**
     * 获取收藏的应用列表
     *
     * @return /
     */
    List<App.QueryPage> queryCollectList();

    /**
     * 获取应用角色列表
     *
     * @return /
     */
    List<MetaOption> queryRoleList();

    /**
     * 绑定应用成员
     *
     * @param args /
     */
    void bindMember(App.BindMemberArgs args);

    /**
     * 获取应用成员角色组
     *
     * @return /
     */
    Map<String, List<App.QueryMemberRoleGroup>> queryMemberRoleGroup(App.QueryMemberRoleGroup args);

    /**
     * 解除应用成员关联关系
     *
     * @param args /
     */
    void unBindMember(App.UnBindMemberArgs args);

    List<MetaOption> queryProjectUrlList(App.QueryProjectUrlListArgs args);
}
