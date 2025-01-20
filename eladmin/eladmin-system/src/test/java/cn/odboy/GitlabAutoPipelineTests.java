package cn.odboy;

import cn.odboy.repository.GitlabPipelineRepository;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONWriter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
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
import java.util.Map;

@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class GitlabAutoPipelineTests {
    @Autowired
    private GitlabPipelineRepository gitlabPipelineRepository;

    @Test
    @SneakyThrows
    public void testRunPipelineFailed() {
        String projectName = "eladmin";
        String ref = "master";
        Map<String, String> variables = new HashMap<>();
        // 这里传入一个不存在的项目目录名称
        variables.put("appname", "eladmin");
        variables.put("versioncode", "202501172000");
        variables.put("envcode", "online");
        gitlabPipelineRepository.executePipelineByProjectName(projectName, ref, variables, (info) -> {
            System.err.println(JSON.toJSONString(info, JSONWriter.Feature.PrettyFormat));
        });
    }

    @Test
    @SneakyThrows
    public void testRunPipelineSuccess() {
        String projectName = "eladmin";
        String ref = "master";
        Map<String, String> variables = new HashMap<>();
        variables.put("appname", "eladmin-system");
        variables.put("versioncode", "202501172000");
        variables.put("envcode", "online");
        gitlabPipelineRepository.executePipelineByProjectName(projectName, ref, variables, (info) -> {
            System.err.println(JSON.toJSONString(info, JSONWriter.Feature.PrettyFormat));
        });
    }
}
