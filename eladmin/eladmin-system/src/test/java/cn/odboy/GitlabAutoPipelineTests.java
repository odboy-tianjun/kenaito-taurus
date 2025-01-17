package cn.odboy;

import cn.odboy.repository.GitlabPipelineRepository;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
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
        variables.put("appname", "eladmin");
        variables.put("versioncode", "202501172000");
        variables.put("envcode", "online");
        gitlabPipelineRepository.executePipelineByProjectName(projectName, ref, variables, (info) -> {
            System.err.println(JSON.toJSONString(info, SerializerFeature.PrettyFormat));
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
            System.err.println(JSON.toJSONString(info, SerializerFeature.PrettyFormat));
        });
    }
}
