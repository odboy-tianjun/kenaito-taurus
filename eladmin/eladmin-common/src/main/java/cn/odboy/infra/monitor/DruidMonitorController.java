/*
 *  Copyright 2022-2025 Tian Jun
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
package cn.odboy.infra.monitor;

import com.alibaba.druid.stat.DruidStatManagerFacade;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * Druid监控数据
 *
 * @author odboy
 * @date 2024-09-05
 */
@Api(tags = "系统：数据源监控")
@RestController
@RequestMapping(value = "/druid")
public class DruidMonitorController {
    @ApiOperation("获取数据源的监控数据")
    @GetMapping("/stat")
    public List<Map<String, Object>> druidStat() {
        return DruidStatManagerFacade.getInstance().getDataSourceStatDataList();
    }
}