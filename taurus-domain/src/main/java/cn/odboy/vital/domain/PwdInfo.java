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
package cn.odboy.vital.domain;

import cn.odboy.mybatisplus.model.MyEntity;
import cn.odboy.mybatisplus.model.MyObject;
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
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 密码管理
 * </p>
 *
 * @author codegen
 * @since 2025-01-20
 */
@Getter
@Setter
@TableName("vital_pwd_info")
@ApiModel(value = "PwdInfo对象", description = "密码管理")
public class PwdInfo extends MyEntity {

    @ApiModelProperty("id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty("网址")
    @TableField("url")
    private String url;

    @ApiModelProperty("用户名")
    @TableField("username")
    private String username;

    @ApiModelProperty("密码")
    @TableField("`password`")
    private String password;

    @ApiModelProperty("生成配置")
    @TableField("gen_config")
    private String genConfig;

    @ApiModelProperty("手机号")
    @TableField("mobile")
    private String mobile;

    @ApiModelProperty("邮箱")
    @TableField("email")
    private String email;

    @ApiModelProperty("备注")
    @TableField("remark")
    private String remark;

    @Data
    @EqualsAndHashCode(callSuper = false)
    public static class GeneratorRuleArgs {
        private List<Integer> lengthRange;
        private List<String> includeType;
        private Integer defineLength;
        private String notIncludeSymbols;

        /**
         * 获取默认的规则
         *
         * @return /
         */
        public static GeneratorRuleArgs getDefaultRule() {
            GeneratorRuleArgs rules = new GeneratorRuleArgs();
            rules.setLengthRange(new ArrayList<>() {{
                add(8);
                add(50);
            }});
            rules.setIncludeType(new ArrayList<>() {{
                add("symbols");
                add("numbers");
                add("lower_letters");
                add("upper_letters");
            }});
            rules.setDefineLength(50);
            rules.setNotIncludeSymbols("()~{[}}!,");
            return rules;
        }
    }

    @Data
    @EqualsAndHashCode(callSuper = false)
    public static class PasswordContent extends MyObject {
        private String content;
        private String crackDuration;
    }

    @Data
    @EqualsAndHashCode(callSuper = false)
    public static class CreateArgs extends MyObject {
        @NotBlank(message = "url不能为空")
        private String url;
        @NotBlank(message = "用户名不能为空")
        private String username;
        private String password;
        private String genConfig;
        private String mobile;
        private String email;
        @NotBlank(message = "备注不能为空")
        private String remark;
    }
}
