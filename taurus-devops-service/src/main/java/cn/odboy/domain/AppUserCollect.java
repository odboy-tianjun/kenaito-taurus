package cn.odboy.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 用户收藏应用
 * </p>
 *
 * @author odboy
 * @since 2024-09-13
 */
@Getter
@Setter
@TableName("devops_app_user_collect")
public class AppUserCollect {
    /**
     * 用户Id
     */
    @TableField(value = "user_id")
    private Long userId;

    /**
     * 应用id
     */
    @TableField(value = "app_id")
    private Long appId;
}
