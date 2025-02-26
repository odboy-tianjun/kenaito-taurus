package cn.odboy.domain;

import cn.odboy.base.MyLogicEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * <p>
 * 容器规格配置
 * </p>
 *
 * @author odboy
 * @since 2024-11-19
 */
@Getter
@Setter
@TableName("devops_containerd_spec_config")
public class ContainerdSpecConfig extends MyLogicEntity {
    /**
     * 自增ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 规格名称
     */
    @TableField("spec_name")
    private String specName;

    /**
     * cpu核数
     */
    @TableField("cpu_num")
    private Integer cpuNum;

    /**
     * 内存大小，单位Gi
     */
    @TableField("mem_num")
    private Integer memNum;

    /**
     * 磁盘大小，单位Gi
     */
    @TableField("disk_size")
    private Integer diskSize;

    @Data
    public static class QueryPage {
        private Long id;
        private String specName;
        private Integer cpuNum;
        private Integer memNum;
        private Integer diskSize;
    }

    @Data
    public static class CreateArgs {
        @NotBlank(message = "必填")
        private String specName;
        @NotNull(message = "必填")
        @Min(message = "最小值为1", value = 1)
        @Max(message = "最大值为256", value = 256)
        private Integer cpuNum;
        @NotNull(message = "必填")
        @Min(message = "最小值为1", value = 1)
        @Max(message = "最大值为512", value = 512)
        private Integer memNum;
        @NotNull(message = "必填")
        @Min(message = "最小值为60", value = 60)
        @Max(message = "最大值为180", value = 180)
        private Integer diskSize;
    }

    @Data
    public static class RemoveArgs {
        @NotNull(message = "必填")
        private Long id;
    }
}
