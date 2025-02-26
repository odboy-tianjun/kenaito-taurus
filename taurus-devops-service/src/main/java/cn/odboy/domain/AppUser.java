package cn.odboy.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 应用、成员关联
 * </p>
 *
 * @author odboy
 * @since 2024-09-13
 */
@Getter
@Setter
@TableName("devops_app_user")
public class AppUser {
    /**
     * 应用id
     */
    @TableField(value = "app_id")
    private Long appId;

    /**
     * 用户Id
     */
    @TableField(value = "user_id")
    private Long userId;

    /**
     * 角色编码
     */
    @TableField(value = "role_code")
    private String roleCode;
}
