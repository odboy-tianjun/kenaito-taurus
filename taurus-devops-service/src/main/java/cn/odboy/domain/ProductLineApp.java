package cn.odboy.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 产品线、应用关联
 * </p>
 *
 * @author odboy
 * @since 2024-09-13
 */
@Getter
@Setter
@TableName("devops_product_line_app")
public class ProductLineApp {
    /**
     * 产品线Id
     */
    @TableField(value = "product_line_id")
    private Long productLineId;

    /**
     * 因公Id
     */
    @TableField(value = "app_id")
    private Long appId;
}
