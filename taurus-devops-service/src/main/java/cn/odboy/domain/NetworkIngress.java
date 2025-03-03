package cn.odboy.domain;

import cn.odboy.mybatisplus.model.MyLogicEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * <p>
 * devops网络ingress-nginx
 * </p>
 *
 * @author odboy
 * @since 2024-11-21
 */
@Getter
@Setter
@TableName("devops_network_ingress")
public class NetworkIngress extends MyLogicEntity {

    /**
     * 自增ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 环境编码
     */
    @TableField("env_code")
    private String envCode;

    /**
     * 应用名称
     */
    @TableField("app_name")
    private String appName;

    /**
     * ingress名称
     */
    @TableField("ingress_name")
    private String ingressName;

    /**
     * 绑定域名
     */
    @TableField("hostname")
    private String hostname;

    /**
     * 绑定域名的类型，内网或者外网
     */
    @TableField("network_type")
    private String networkType;
    /**
     * 绑定的ServiceId
     */
    @TableField("service_id")
    private Long serviceId;

    /**
     * 绑定的路径，默认为"/"
     */
    @TableField("`path`")
    private String path;

    @Data
    public static class QueryPage {
        private Long id;
        private String envCode;
        private String appName;
        private String ingressName;
        private String hostname;
        private String networkType;
        private Long serviceId;
        private String path;
        private Date createTime;
        private NetworkService serviceInfo;
    }

    @Data
    public static class CreateArgs {
        @NotBlank(message = "必填")
        private String envCode;
        @NotBlank(message = "必填")
        private String appName;
        @NotBlank(message = "必填")
        private String hostname;
        @NotBlank(message = "必填")
        private String networkType;
        @NotNull(message = "必填")
        private Long serviceId;
        @NotBlank(message = "必填")
        private String path;
    }

    @Data
    public static class RemoveArgs {
        @NotNull(message = "必填")
        private Long id;
    }
}
