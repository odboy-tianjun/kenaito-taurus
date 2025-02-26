package cn.odboy.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 产品线、用户关联
 * </p>
 *
 * @author odboy
 * @since 2024-09-13
 */
@Getter
@Setter
@TableName("devops_product_line_user")
public class ProductLineUser {
    /**
     * 产品线Id
     */
    @TableField(value = "product_line_id")
    private Long productLineId;

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
