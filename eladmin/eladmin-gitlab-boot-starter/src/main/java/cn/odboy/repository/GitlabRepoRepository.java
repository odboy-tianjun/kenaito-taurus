package cn.odboy.repository;

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import cn.odboy.constant.EnvEnum;
import cn.odboy.context.GitlabAuthAdmin;
import cn.odboy.context.GitlabCIFileAdmin;
import cn.odboy.context.GitlabIgnoreFileAdmin;
import cn.odboy.infra.exception.BadRequestException;
import org.gitlab4j.api.*;
import org.gitlab4j.api.models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Gitlab仓库
 *
 * @author odboy
 * @date 2024-09-09
 */
@Slf4j
@Component
public class GitlabRepoRepository {
    @Autowired
    private GitlabAuthAdmin repository;
    @Autowired
    private GitlabProjectRepository projectRepository;
    @Autowired
    private GitlabIgnoreFileAdmin ignoreFileAdmin;
    @Autowired
    private GitlabCIFileAdmin ciFileAdmin;

    /**
     * 根据projectId创建分支
     *
     * @param projectId  项目id
     * @param branchName 分支名称
     * @param ref        从什么分支创建
     * @return /
     */
    public Branch createRepoBranch(Long projectId, String branchName, String ref) {
        Project project = projectRepository.describeProjectById(projectId);
        if (project == null) {
            throw new BadRequestException("创建分支失败, 项目不存在");
        }
        try (GitLabApi client = repository.auth()) {
            RepositoryApi repositoryApi = client.getRepositoryApi();
            if (StrUtil.isBlank(ref)) {
                ref = project.getDefaultBranch();
            }
            return repositoryApi.createBranch(projectId, branchName, ref);
        } catch (Exception e) {
            log.error("根据projectId创建分支失败", e);
            throw new BadRequestException("根据projectId创建分支失败");
        }
    }

    /**
     * 根据appName创建分支
     *
     * @param appName    应用名称
     * @param branchName 分支名称
     * @param ref        从什么分支创建
     * @return /
     */
    public Branch createRepoBranchByAppName(String appName, String branchName, String ref) {
        Project project = projectRepository.describeProjectByAppName(appName);
        if (project == null) {
            throw new BadRequestException("创建分支失败, 项目不存在");
        }
        try (GitLabApi client = repository.auth()) {
            RepositoryApi repositoryApi = client.getRepositoryApi();
            if (StrUtil.isBlank(ref)) {
                ref = project.getDefaultBranch();
            }
            // 根据path创建分支有问题
            return repositoryApi.createBranch(project.getId(), branchName, ref);
        } catch (Exception e) {
            log.error("根据appName创建分支失败", e);
            throw new BadRequestException("根据appName创建分支失败");
        }
    }

    public Branch describeRepoBranchByProjectId(Long projectId, String branchName) {
        try (GitLabApi client = repository.auth()) {
            return client.getRepositoryApi().getBranch(projectId, branchName);
        } catch (Exception e) {
            log.error("根据projectId获取分支失败", e);
            throw new BadRequestException("根据projectId获取分支失败");
        }
    }

    public Branch describeRepoBranchByAppName(String appName, String branchName) {
        Project project = projectRepository.describeProjectByAppName(appName);
        if (project == null) {
            throw new BadRequestException("根据appName获取分支失败, 项目不存在");
        }
        try (GitLabApi client = repository.auth()) {
            return client.getRepositoryApi().getBranch(project.getId(), branchName);
        } catch (Exception e) {
            log.error("根据appName获取分支失败", e);
            if (e.getMessage().contains("Branch Not Found")) {
                throw new BadRequestException("分支不存在");
            }
            throw new BadRequestException("根据appName获取分支失败");
        }
    }

    public List<Branch> searchRepoBranchesByProjectId(Long projectId, String search) {
        return searchRepoBranches(projectId, search);
    }

    public List<Branch> searchRepoBranchesByAppName(String appName, String search) {
        Project project = projectRepository.describeProjectByAppName(appName);
        if (project == null) {
            throw new BadRequestException("根据appName获取分支失败, 项目不存在");
        }
        return searchRepoBranches(project.getId(), search);
    }

    private List<Branch> searchRepoBranches(Long projectId, String searchKey) {
        List<Branch> list = new ArrayList<>();
        try (GitLabApi client = repository.auth()) {
            Pager<Branch> pager = client.getRepositoryApi().getBranches(projectId, searchKey, 100);
            while (pager.hasNext()) {
                list.addAll(pager.next());
            }
        } catch (Exception e) {
            log.error("获取分支列表失败", e);
        }
        return list;
    }

    public void deleteRepoBranchByProjectId(Long projectId, String branchName) {
        try (GitLabApi client = repository.auth()) {
            client.getRepositoryApi().deleteBranch(projectId, branchName);
        } catch (Exception e) {
            log.error("根据projectId删除分支失败", e);
        }
    }

    public void deleteRepoBranchByAppName(String appName, String branchName) {
        Project project = projectRepository.describeProjectByAppName(appName);
        if (project == null) {
            throw new BadRequestException("根据appName删除分支失败, 项目不存在");
        }
        try (GitLabApi client = repository.auth()) {
            client.getRepositoryApi().deleteBranch(project.getId(), branchName);
        } catch (Exception e) {
            log.error("根据appName删除分支失败", e);
        }
    }

    /**
     * 根据projectId比较分支(合并分支之前, 展示改动的内容)
     *
     * @param projectId  项目id
     * @param fromBranch 分支1
     * @param toBranch   分支2
     * @return /
     */
    public CompareResults compareRepoBranchByProjectId(Long projectId, String fromBranch, String toBranch) {
        try (GitLabApi client = repository.auth()) {
            return client.getRepositoryApi().compare(projectId, fromBranch, toBranch);
        } catch (Exception e) {
            log.error("根据projectId比较分支差异失败", e);
            return null;
        }
    }

    /**
     * 根据appName比较分支(合并分支之前, 展示改动的内容)
     *
     * @param appName      应用名称
     * @param sourceBranch 源分支
     * @param targetBranch 目标分支
     * @return /
     */
    public CompareResults compareRepoBranchByAppName(String appName, String sourceBranch, String targetBranch) {
        Project project = projectRepository.describeProjectByAppName(appName);
        if (project == null) {
            throw new BadRequestException("根据projectId比较分支差异失败, 项目不存在");
        }
        try (GitLabApi client = repository.auth()) {
            // 比较两个分支
            CompareResults compareResults = client.getRepositoryApi().compare(project.getId(), sourceBranch, targetBranch);
            // 输出差异信息
            System.out.println("Commits:");
            for (Commit commit : compareResults.getCommits()) {
                System.out.println("  " + commit.getId() + " - " + commit.getMessage());
            }
            System.out.println("Diffs:");
            for (Diff diff : compareResults.getDiffs()) {
                System.out.println("  " + diff.getOldPath() + " -> " + diff.getNewPath());
            }
            return compareResults;
        } catch (Exception e) {
            log.error("根据projectId比较分支差异失败", e);
            return null;
        }
    }

    public MergeRequest createRepoMergeRequest(Long projectId, String sourceBranch, String targetBranch, String title, String description) {
        if (projectId == null) {
            throw new BadRequestException("参数 projectId 必填");
        }
        if (StrUtil.isBlank(sourceBranch)) {
            throw new BadRequestException("参数 sourceBranch 必填");
        }
        if (StrUtil.isBlank(targetBranch)) {
            throw new BadRequestException("参数 targetBranch 必填");
        }
        if (StrUtil.isBlank(title)) {
            title = String.format("%s 合并到 %s", sourceBranch, targetBranch);
        }
        try (GitLabApi client = repository.auth()) {
            MergeRequestParams params = new MergeRequestParams();
            params.withTargetProjectId(projectId);
            params.withSourceBranch(sourceBranch);
            params.withTargetBranch(targetBranch);
            params.withTitle(title);
            params.withDescription(description);
            MergeRequestApi mergeRequestApi = client.getMergeRequestApi();
            return mergeRequestApi.createMergeRequest(projectId, params);
        } catch (Exception e) {
            log.error("创建分支合并请求失败", e);
            throw new BadRequestException("创建分支合并请求失败");
        }
    }

    public MergeRequest describeRepoMergeRequestByProjectId(Long projectId, Long mergeRequestId) {
        try (GitLabApi client = repository.auth()) {
            return client.getMergeRequestApi().getMergeRequest(projectId, mergeRequestId);
        } catch (Exception e) {
            log.error("根据projectId获取分支详情", e);
            return null;
        }
    }

    public MergeRequest describeRepoMergeRequestByAppName(String appName, Long mergeRequestId) {
        Project project = projectRepository.describeProjectByAppName(appName);
        if (project == null) {
            throw new BadRequestException("根据appName获取分支详情失败, 项目不存在");
        }
        try (GitLabApi client = repository.auth()) {
            return client.getMergeRequestApi().getMergeRequest(project.getId(), mergeRequestId);
        } catch (Exception e) {
            log.error("根据appName获取分支详情失败", e);
            return null;
        }
    }

    public MergeRequest acceptRepoMergeRequest(Long projectId, Long mergeRequestIid) {
        try (GitLabApi client = repository.auth()) {
            MergeRequest mergeRequest = client.getMergeRequestApi().getMergeRequest(projectId, mergeRequestIid);
            AcceptMergeRequestParams params = new AcceptMergeRequestParams();
            params.withShouldRemoveSourceBranch(false);
            params.withMergeCommitMessage(mergeRequest.getTitle() == null ? mergeRequest.getDescription() : mergeRequest.getTitle());
            return client.getMergeRequestApi().acceptMergeRequest(projectId, mergeRequestIid, params);
        } catch (Exception e) {
            log.error("接受合并请求失败", e);
            throw new BadRequestException("接受合并请求失败");
        }
    }

    public void initGitIgnoreFile(Long projectId, String defaultBranchName, String language) {
        String fileContent = ignoreFileAdmin.getContent(language);
        if (StrUtil.isBlank(language)) {
            log.error("不支持的语言, 跳过 .gitignore 文件初始化");
            return;
        }

        try (GitLabApi client = repository.auth()) {
            RepositoryFile repositoryFile = new RepositoryFile();
            repositoryFile.setFilePath(".gitignore");
            repositoryFile.setContent(fileContent);
            client.getRepositoryFileApi().createFile(projectId, repositoryFile, defaultBranchName, "init .gitignore");
            log.info("初始化 .gitignore 文件成功");
        } catch (GitLabApiException e) {
            log.error("初始化 .gitignore 文件失败", e);
        }
    }

    public void initGitCiFile(Long projectId, String defaultBranch, String language, String appName) {
        if (StrUtil.isBlank(language)) {
            log.error("不支持的语言, 跳过 .gitlab-ci.yml 文件初始化");
            return;
        }

        try (GitLabApi client = repository.auth()) {
            String fileContent = ciFileAdmin.getCIFileContent(language);
            RepositoryFile repositoryFile = new RepositoryFile();
            repositoryFile.setFilePath("..gitlab-ci.yml");
            repositoryFile.setContent(fileContent);
            client.getRepositoryFileApi().createFile(projectId, repositoryFile, defaultBranch, "init ..gitlab-ci.yml");
            log.info("初始化 ..gitlab-ci.yml 文件成功");
        } catch (GitLabApiException e) {
            log.error("初始化 ..gitlab-ci.yml 文件失败", e);
        }

        try (GitLabApi client = repository.auth()) {
            EnvEnum[] allEnvList = new EnvEnum[]{EnvEnum.Daily, EnvEnum.Stage, EnvEnum.Online};
            for (EnvEnum envEnum : allEnvList) {
                String filePath = "Dockerfile_" + envEnum.getCode();
                try {
                    String dockerfileContent = ciFileAdmin.getDockerfileContent(language, envEnum);
                    dockerfileContent = dockerfileContent.replaceAll("#APP_NAME#", appName);
                    RepositoryFile repositoryFile = new RepositoryFile();

                    repositoryFile.setFilePath(filePath);
                    repositoryFile.setContent(dockerfileContent);
                    client.getRepositoryFileApi().createFile(projectId, repositoryFile, defaultBranch, "init " + filePath);
                    log.info("初始化 {} 文件成功", filePath);
                } catch (GitLabApiException e) {
                    log.error("初始化 {} 文件失败", filePath, e);
                }
            }
        }

        String releaseFileName = appName + ".release";
        try (GitLabApi client = repository.auth()) {
            String dockerfileContent = ciFileAdmin.getReleaseFileContent(language);
            dockerfileContent = dockerfileContent.replaceAll("#APP_NAME#", appName);
            RepositoryFile repositoryFile = new RepositoryFile();
            repositoryFile.setFilePath(releaseFileName);
            repositoryFile.setContent(dockerfileContent);
            client.getRepositoryFileApi().createFile(projectId, repositoryFile, defaultBranch, "init release");
            log.info("初始化 release 文件成功");
        } catch (GitLabApiException e) {
            log.error("初始化 release 文件失败", e);
        }
    }
}
