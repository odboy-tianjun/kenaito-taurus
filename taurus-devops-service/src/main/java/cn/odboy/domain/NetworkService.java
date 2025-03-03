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
 * k8s网络service
 * </p>
 *
 * @author odboy
 * @since 2024-11-20
 */
@Getter
@Setter
@TableName("devops_network_service")
public class NetworkService extends MyLogicEntity {

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
     * 应用、资源名称
     */
    @TableField("app_name")
    private String appName;

    /**
     * 服务类型(应用 app 资源 resource)
     */
    @TableField("service_type")
    private String serviceType;

    /**
     * 服务名称
     */
    @TableField("service_name")
    private String serviceName;

    /**
     * 服务端口
     */
    @TableField("service_port")
    private Integer servicePort;

    /**
     * 容器端口
     */
    @TableField("service_target_port")
    private Integer serviceTargetPort;

    @Data
    public static class QueryPage {
        private Long id;
        private String envCode;
        private String appName;
        private String serviceName;
        private String serviceType;
        private Integer servicePort;
        private Integer serviceTargetPort;
        private Date createTime;
    }


    @Data
    public static class CreateArgs {
        @NotBlank(message = "必填")
        private String envCode;
        @NotBlank(message = "必填")
        private String appName;
        @NotBlank(message = "必填")
        private String serviceType;
        @NotNull(message = "必填")
        private Integer serviceTargetPort;
    }
}
