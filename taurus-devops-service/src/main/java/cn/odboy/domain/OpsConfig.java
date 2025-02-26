package cn.odboy.domain;

import cn.odboy.base.MyLogicEntity;
import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * 运维配置
 * </p>
 *
 * @author odboy
 * @since 2024-11-19
 */
@Getter
@Setter
@TableName("devops_ops_config")
public class OpsConfig extends MyLogicEntity {

    /**
     * 配置编码
     */
    @TableField("config_code")
    private String configCode;

    /**
     * 配置名称
     */
    @TableField("config_name")
    private String configName;

    /**
     * 配置值
     */
    @TableField("config_value")
    private String configValue;

    /**
     * 配置说明
     */
    @TableField("config_remark")
    private String configRemark;

    /**
     * 封网配置
     */
    @Data
    public static class BlockNetwork {
        /**
         * 是否禁用WebSSH
         */
        private boolean disableWebSsh;
        /**
         * 规则列表
         */
        private List<BlockList> blockList;

        @Data
        public static class BlockList {
            /**
             * 规则名称
             */
            private String name;
            /**
             * 时间范围
             */
            @JSONField(format = "yyyy-MM-dd HH:mm:ss")
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
            private List<Date> betweenTime;
            /**
             * 应用白名单
             */
            private List<String> appWhiteList;
            /**
             * 变更白名单
             */
            private List<String> actionWhiteList;
        }
    }
}
