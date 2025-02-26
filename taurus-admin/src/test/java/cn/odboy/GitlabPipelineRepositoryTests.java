package cn.odboy;

import cn.odboy.infra.gitlab.repository.GitlabProjectRepository;
import com.alibaba.fastjson.JSON;
import org.gitlab4j.api.models.Project;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class GitlabPipelineRepositoryTests {
    @Autowired
    private GitlabProjectRepository repository;

    @Test
    public void createPipeline() {
        Project project = repository.getByAppName("test-02");
        System.err.println(JSON.toJSONString(project));
    }
}

