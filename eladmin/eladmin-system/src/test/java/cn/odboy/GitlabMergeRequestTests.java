package cn.odboy;

import cn.hutool.core.thread.ThreadUtil;
import cn.odboy.infra.exception.BadRequestException;
import cn.odboy.repository.GitlabProjectMergeRequestRepository;
import com.alibaba.fastjson.JSON;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.gitlab4j.api.models.MergeRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class GitlabMergeRequestTests {
    @Autowired
    private GitlabProjectMergeRequestRepository repository;

    /**
     * 获取开放状态的合并请求 ok
     */
    @Test
    @SneakyThrows
    public void testListOpenedMergeRequest() {
        List<MergeRequest> mergeRequests = repository.listOpenedMergeRequests("eladmin");
        System.err.println(JSON.toJSONString(mergeRequests));
    }

    /**
     * 创建合并请求 ok
     */
    @Test
    @SneakyThrows
    public void testCreateMergeRequest() {
        MergeRequest mergeRequest = repository.createMergeRequest("eladmin", "test001", "master", "合并请求", "合并请求测试");
        Long iid = mergeRequest.getIid();
        Long projectId = mergeRequest.getProjectId();
        // 2
        System.err.println("projectId=" + projectId);
        // 3
        System.err.println("iid=" + iid);
    }

    /**
     * 重复创建合并请求 ok
     */
    @Test
    @SneakyThrows
    public void testRetryCreateMergeRequest() {
        // 已存在一个opened状态的合并请求时，创建合并请求必报错
        MergeRequest mergeRequest = repository.createMergeRequest("eladmin", "test001", "master", "二次合并请求", "测试是否可以发起两次相同分支的合并请求");
        Long iid = mergeRequest.getIid();
        Long projectId = mergeRequest.getProjectId();
        System.err.println("projectId=" + projectId);
        System.err.println("iid=" + iid);
    }

    /**
     * 自动解决合并冲突 ok
     */
    @Test
    public void testAutoMergeRequest() {
        MergeRequest mergeRequest = repository.createMergeRequest("eladmin", "test001", "master", "合并请求", "合并请求测试");
        Long projectId = mergeRequest.getProjectId();
        Long iid = mergeRequest.getIid();
        while (true) {
            ThreadUtil.safeSleep(2000);
            mergeRequest = repository.describeMergeRequest(projectId, iid);
            boolean mergeRequestChecking = repository.isMergeRequestChecking(mergeRequest);
            if (mergeRequestChecking) {
                log.info("正在检查是否可以合并，是否有冲突...");
                continue;
            }
            boolean mergeRequestHasConflict = repository.isMergeRequestHasConflict(mergeRequest);
            if (mergeRequestHasConflict) {
                log.error("有冲突，等待冲突解决中...");
                continue;
            }
            boolean mergeRequestMerged = repository.isMergeRequestMerged(mergeRequest);
            if (mergeRequestMerged) {
                // 这里不排除人为的合并了
                log.info("卧槽，已经合并了，那还判断个毛线...");
                break;
            }
            boolean mergeRequestCanAccept = repository.isMergeRequestCanAccept(mergeRequest);
            if (!mergeRequestCanAccept) {
                log.error("还不能合并，那只能抛异常了...");
                try {
                    repository.closeMergeRequest(projectId, iid);
                } catch (Exception e) {
                    log.error(e.getMessage());
                }
                throw new BadRequestException("自动合并失败");
            }
            mergeRequest = repository.acceptMergeRequest(projectId, mergeRequest);
            mergeRequestMerged = repository.isMergeRequestMerged(mergeRequest);
            if (mergeRequestMerged) {
                log.info("合并请求已接受...");
                break;
            }
            log.error("还不能合并，那只能抛异常了...");
            try {
                repository.closeMergeRequest(projectId, iid);
            } catch (Exception e) {
                log.error(e.getMessage());
            }
            throw new BadRequestException("自动合并失败");
        }
    }
}
