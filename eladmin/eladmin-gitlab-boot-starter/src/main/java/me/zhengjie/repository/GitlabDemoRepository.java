package me.zhengjie.repository;

import lombok.extern.slf4j.Slf4j;
import me.zhengjie.context.GitlabAuthRepository;
import me.zhengjie.infra.exception.BadRequestException;
import org.gitlab4j.api.GitLabApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
/**
 * gitlab Demo
 *
 * @author odboy
 * @date 2025-01-12
 */
@Slf4j
@Component
public class GitlabDemoRepository {
    @Autowired
    private GitlabAuthRepository repository;

    public void test() {
        try (GitLabApi client = repository.auth()) {
            client.getJobApi().cancelJob(null, null);
        } catch (Exception e) {
            log.error("xxx失败", e);
            throw new BadRequestException("xxx失败");
        }
    }
}
