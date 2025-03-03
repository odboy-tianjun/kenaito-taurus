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

import cn.odboy.mybatisplus.model.MyObject;
import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * <p>
 * 生日时间线
 * </p>
 *
 * @author odboy
 * @since 2021-12-15
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("birth_date_timeline")
@ApiModel(value = "BirthDateTimeline对象", description = "生日时间线")
public class BirthDateTimeline extends MyObject {
    /**
     * 自增ID
     */
    @TableId(type = IdType.AUTO)
    @ApiModelProperty(value = "主键")
    protected Long id;
    @ApiModelProperty(value = "生日id")
    private Long birthdayId;
    @ApiModelProperty(value = "统计年份(yyyy)")
    private Integer currentYear;
    @ApiModelProperty(value = "统计年生日日期")
    private Date currentBirthday;
    @ApiModelProperty(value = "联系人ID")
    private Long contactId;
    @ApiModelProperty(value = "联系人姓名")
    private String contactName;
    @ApiModelProperty(value = "周岁(定时任务统计)")
    private Integer oneYearOld;
    @ApiModelProperty(value = "虚岁(定时任务统计)")
    private Integer virtualYearOld;
    @ApiModelProperty(value = "星座")
    private String zodiac;
    @ApiModelProperty(value = "属相")
    private String chineseZodiac;
    @TableField(exist = false)
    private boolean inProgress;
}
