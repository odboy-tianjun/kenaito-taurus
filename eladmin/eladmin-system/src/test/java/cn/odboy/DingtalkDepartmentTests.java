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
package cn.odboy;

import com.alibaba.fastjson2.JSON;
import lombok.SneakyThrows;
import cn.odboy.repository.DingtalkDepartmentRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * 钉钉部门 测试
 *
 * @author odboy
 * @date 2025-01-16
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class DingtalkDepartmentTests {
    @Autowired
    private DingtalkDepartmentRepository dingtalkDepartmentRepository;

    @Test
    @SneakyThrows
    public void testDingtalkDepartmentRepository() {
        dingtalkDepartmentRepository.listAllSubDepartments(1, (list) -> {
            System.err.println(JSON.toJSONString(list));
        });
    }
}
