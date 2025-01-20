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
package cn.odboy.modules.vital.rest;

import cn.odboy.model.MetaOption;
import cn.odboy.modules.vital.domain.BirthDate;
import cn.odboy.modules.vital.service.CalendarDictService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * 日历字典 前端控制器
 * </p>
 *
 * @author codegen
 * @since 2025-01-20
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/vital/calendarDict")
public class CalendarDictController {
    private final CalendarDictService calendarDictService;

    @PostMapping(value = "listMetaMonthsByLunarYear")
    public ResponseEntity<Object> listMetaMonthsByLunarYear(@Validated @RequestBody BirthDate.ListMetaMonthByLunarYear args) {
        return new ResponseEntity<>(calendarDictService.listMetaMonthsByLunarYear(args), HttpStatus.OK);
    }

    @PostMapping(value = "listMetaDaysByLunarYearMonth")
    public ResponseEntity<Object> listMetaDaysByLunarYearMonth(@Validated @RequestBody BirthDate.ListMetaDaysByLunarYearMonth args) {
        return new ResponseEntity<>(calendarDictService.listMetaDaysByLunarYearMonth(args), HttpStatus.OK);
    }

    @PostMapping(value = "describeLunarDateByLunarYmd")
    public ResponseEntity<Object> describeLunarDateByLunarYmd(@Validated @RequestBody BirthDate.DescribeLunarDateByLunarYmd args) {
        return new ResponseEntity<>(calendarDictService.describeLunarDateByLunarYmd(args), HttpStatus.OK);
    }
}
