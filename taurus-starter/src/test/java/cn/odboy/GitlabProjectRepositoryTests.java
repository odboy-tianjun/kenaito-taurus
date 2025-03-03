package cn.odboy;

import cn.odboy.model.GitlabProject;
import cn.odboy.repository.GitlabProjectRepository;
import com.alibaba.fastjson.JSON;
import org.gitlab4j.api.models.Project;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class GitlabProjectRepositoryTests {
    @Autowired
    private GitlabProjectRepository repository;

    @Test
    public void create() {
        GitlabProject.CreateArgs args = new GitlabProject.CreateArgs();
        args.setAppName("test-02");
        args.setDescription("忙得一批, 没空写");
        GitlabProject.CreateResp createResp = repository.createProject(args);
        System.err.println(JSON.toJSONString(createResp));
    }

    @Test
    public void pageProjects() {
        List<Project> projects = repository.listProjects(1);
        System.err.println(JSON.toJSONString(projects));
    }

    @Test
    public void getByProjectId() {
        Project project = repository.describeProjectById(6L);
        System.err.println(JSON.toJSONString(project));
    }

    @Test
    public void getByAppName() {
        Project project = repository.describeProjectByProjectName("test-02");
        System.err.println(JSON.toJSONString(project));
    }
}

