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

import cn.odboy.service.ResourceQueryService;
import com.alibaba.fastjson2.JSON;
import io.fabric8.kubernetes.api.model.Node;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

/**
 * k8s Listener 测试
 *
 * @author odboy
 * @date 2025-01-18
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class K8sListenerTests {
    @Autowired
    private ResourceQueryService<Node> nodeResourceQueryService;

    @Test
    @SneakyThrows
    public void test() {
        List<Node> nodeList = nodeResourceQueryService.list("local", null);
        System.err.println(JSON.toJSONString(nodeList));
    }
}
