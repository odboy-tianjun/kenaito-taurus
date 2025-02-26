package cn.odboy;

import cn.odboy.infra.gitlab.repository.GitlabRepoRepository;
import com.alibaba.fastjson.JSON;
import org.gitlab4j.api.models.Branch;
import org.gitlab4j.api.models.MergeRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class GitlabRespRepositoryTests {
    @Autowired
    private GitlabRepoRepository repository;

    @Test
    public void create() {
        Branch branch = repository.createBranchByAppName("demo", "test001", "master");
        System.err.println(JSON.toJSONString(branch));
    }

    @Test
    public void delete() {
        repository.deleteBranchByAppName("test-02", "test001");
    }

    @Test
    public void listBranch() {
        List<Branch> branches = repository.queryBranchByAppName("test-02", "test");
        System.err.println(branches);
    }

    @Test
    public void getMergeRequestByAppName() {
        // 具体状态参考文件： gitlab-merge-request-open.txt和gitlab-merge-request-冲突.txt
        MergeRequest mergeRequest = repository.getMergeRequestByAppName("test-02", 1L);
        System.err.println(JSON.toJSONString(mergeRequest));
    }
}

