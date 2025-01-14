package cn.odboy.constant;

/**
 * 公共常量
 *
 * @author odboy
 * @date 2024-09-09
 */
public interface GitlabConst {
    /**
     * 正则表达式
     */
    String REGEX_APP_NAME = "^[a-z][a-z0-9]*(-[a-z0-9]+)*$";
    /**
     * ROOT用户命名空间id
     */
    Long ROOT_NAMESPACE_ID = 1L;
    /**
     * 项目默认分支master
     */
    String PROJECT_DEFAULT_BRANCH = "master";
}
