/*
 *  Copyright 2021-2025 Tian Jun
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package cn.odboy.modules.vital.domain;

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
import javax.validation.constraints.Size;
import java.util.List;

/**
 * <p>
 * 联系人管理
 * </p>
 *
 * @author codegen
 * @since 2025-01-20
 */
@Getter
@Setter
@TableName("vital_contact_info")
@ApiModel(value = "ContactInfo对象", description = "联系人管理")
public class ContactInfo extends MyEntity {

    @ApiModelProperty("id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty("姓名")
    @TableField("real_name")
    private String realName;

    @ApiModelProperty("称谓")
    @TableField("nick_name")
    private String nickName;

    @ApiModelProperty("所属标签")
    @TableField("tag_name")
    private String tagName;

    @Data
    @EqualsAndHashCode(callSuper = false)
    public static class CreateArgs extends MyObject {
        @NotBlank(message = "姓名不能为空")
        private String realName;
        private String nickName;
        private String tagName;
    }

    @Data
    @EqualsAndHashCode(callSuper = false)
    public static class UpdateArgs extends MyObject {
        @NotNull(message = "id不能为空")
        private Long id;
        @NotBlank(message = "姓名不能为空")
        private String realName;
        private String nickName;
        private String tagName;
    }
}
