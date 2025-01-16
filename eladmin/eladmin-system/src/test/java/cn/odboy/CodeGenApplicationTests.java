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
package cn.odboy;

import cn.odboy.util.CmdGenHelper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

/**
 * 代码生成 测试
 *
 * @author odboy
 * @date 2025-01-11
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CodeGenApplicationTests {
    @Value("${spring.datasource.druid.url}")
    private String databaseUrl;
    @Value("${spring.datasource.druid.username}")
    private String databaseUsername;
    @Value("${spring.datasource.druid.password}")
    private String databasePassword;
    @Value("${spring.datasource.druid.driverClassName}")
    private String driverClassName;

    @Test
    public void contextLoads() {
        CmdGenHelper generator = new CmdGenHelper();
        generator.setDatabaseUrl(databaseUrl);
        generator.setDatabaseUsername(databaseUsername);
        generator.setDatabasePassword(databasePassword);
        generator.setDriverClassName(driverClassName);
        generator.gen("mnt_", List.of(
                "mnt_app",
                "mnt_deploy"
        ));
    }

    public static void main(String[] args) {
    }
}
