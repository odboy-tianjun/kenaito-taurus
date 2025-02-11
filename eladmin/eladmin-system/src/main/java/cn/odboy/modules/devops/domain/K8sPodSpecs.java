package cn.odboy.modules.devops.domain;

import cn.odboy.base.MyEntity;
import cn.odboy.model.MyObject;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * <p>
 * k8s pod规格
 * </p>
 *
 * @author codegen
 * @since 2025-02-11
 */
@Getter
@Setter
@TableName("devops_k8s_pod_specs")
@ApiModel(value = "K8sPodSpecs对象", description = "k8s pod规格")
public class K8sPodSpecs extends MyEntity {

    @ApiModelProperty("id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty("规格名称")
    @TableField("specs_name")
    private String specsName;

    @ApiModelProperty("需求cpu数")
    @TableField("request_cpu")
    private Integer requestCpu;

    @ApiModelProperty("需求内存数，单位Gi")
    @TableField("request_memory")
    private Integer requestMemory;

    @ApiModelProperty("最大cpu数")
    @TableField("limit_cpu")
    private Integer limitCpu;

    @ApiModelProperty("最大内存数，单位Gi")
    @TableField("limit_memory")
    private Integer limitMemory;

    @ApiModelProperty("是否启用")
    @TableField("`status`")
    private Boolean status;

    @Data
    @EqualsAndHashCode(callSuper = false)
    public static class CreateArgs extends MyObject {
        @NotBlank(message = "规格名称不能为空")
        private String specsName;
        @NotNull(message = "需求cpu数不能为空")
        private Integer requestCpu;
        @NotNull(message = "需求内存数不能为空")
        private Integer requestMemory;
        @NotNull(message = "最大cpu数不能为空")
        private Integer limitCpu;
        @NotNull(message = "最大内存数不能为空")
        private Integer limitMemory;
    }
}
