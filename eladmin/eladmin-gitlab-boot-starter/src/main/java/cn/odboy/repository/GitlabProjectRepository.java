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
package cn.odboy.repository;

import cn.hutool.core.collection.CollUtil;
import cn.odboy.constant.EnvEnum;
import cn.odboy.constant.GitlabBizConst;
import cn.odboy.context.GitlabAuthAdmin;
import cn.odboy.context.GitlabCiFileAdmin;
import cn.odboy.context.GitlabIgnoreFileAdmin;
import cn.odboy.infra.exception.BadRequestException;
import cn.odboy.model.GitlabProject;
import cn.odboy.util.ValidationUtil;
import com.github.xiaoymin.knife4j.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.ProjectApi;
import org.gitlab4j.api.models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Gitlab项目
 *
 * @author odboy
 * @date 2024-09-09
 */
@Slf4j
@Component
public class GitlabProjectRepository {
    @Autowired
    private GitlabAuthAdmin gitlabAuthAdmin;
    @Autowired
    private GitlabIgnoreFileAdmin ignoreFileAdmin;
    @Autowired
    private GitlabCiFileAdmin ciFileAdmin;

    /**
     * 创建项目 -> ok
     *
     * @param args /
     * @return /
     */
    public GitlabProject.CreateResp createProject(GitlabProject.CreateArgs args) {
        ValidationUtil.validate(args);
        String appName = args.getAppName();
        String newProjectName = appName.trim();
//        字符串必须以小写字母开头。
//        字符串中间可以包含小写字母和数字。
//        - 符号可以出现多次，并且 - 的左右两边都必须是小写字母或数字
        if (!Pattern.compile(GitlabBizConst.REGEX_APP_NAME).matcher(newProjectName).matches()) {
            throw new BadRequestException("应用名称格式不正确, 只能由小写字母、数字与符号-组成");
        }
        try (GitLabApi client = gitlabAuthAdmin.auth()) {
            ProjectApi projectApi = client.getProjectApi();
            Optional<Project> loadProjectOptional = projectApi.getProjectsStream().filter(f -> f.getPath().equals(args.getAppName())).findFirst();
            if (loadProjectOptional.isPresent()) {
                throw new BadRequestException("应用名称已存在");
            }
            Project project = new Project();
            project.setName(StrUtil.isBlank(args.getName()) ? newProjectName : args.getName());
            project.setPath(newProjectName);
            project.setDescription(args.getDescription());
            project.setVisibility(Visibility.PRIVATE);
            project.setInitializeWithReadme(true);
            project.setDefaultBranch(GitlabBizConst.PROJECT_DEFAULT_BRANCH);
            // Groups Or Users -> namespace id
            Namespace namespace = client.getNamespaceApi().getNamespace(args.getGroupOrUserId());
            project.setNamespace(namespace);
            Project newProject = projectApi.createProject(args.getGroupOrUserId(), project);
            return transformCreateResp(args, newProject, newProjectName);
        } catch (Exception e) {
            log.error("创建应用失败", e);
            throw new BadRequestException("创建应用失败, " + e.getMessage());
        }
    }

    private GitlabProject.CreateResp transformCreateResp(GitlabProject.CreateArgs args, Project newProject, String newProjectName) {
        GitlabProject.CreateResp createResp = new GitlabProject.CreateResp();
        createResp.setCreatorId(newProject.getCreatorId());
        createResp.setCreatedAt(newProject.getCreatedAt());
        createResp.setDefaultBranch(newProject.getDefaultBranch());
        createResp.setHttpUrlToRepo(newProject.getHttpUrlToRepo());
        createResp.setProjectId(newProject.getId());
        createResp.setProjectName(newProject.getName());
        createResp.setVisibility(newProject.getVisibility().name());
        createResp.setHomeUrl(newProject.getWebUrl());
        createResp.setName(args.getName());
        createResp.setAppName(newProjectName);
        createResp.setDescription(args.getDescription());
        return createResp;
    }

    /**
     * 新增项目成员 -> ok
     *
     * @param projectId   项目id
     * @param userId      用户id
     * @param accessLevel 访问级别
     */
    public void addProjectMember(Long projectId, Long userId, AccessLevel accessLevel) {
        try (GitLabApi client = gitlabAuthAdmin.auth()) {
            ProjectApi projectApi = client.getProjectApi();
            projectApi.addMember(projectId, userId, accessLevel);
        } catch (Exception e) {
            log.error("新增应用成员失败", e);
            throw new BadRequestException("新增应用成员失败");
        }
    }

    /**
     * 批量新增项目成员 -> ok
     *
     * @param projectId   项目id
     * @param userIds     用户id列表
     * @param accessLevel 访问级别
     */
    public void addProjectMembers(Long projectId, List<Long> userIds, AccessLevel accessLevel) {
        if (CollUtil.isNotEmpty(userIds)) {
            for (Long userId : userIds.stream().filter(Objects::nonNull).distinct().collect(Collectors.toList())) {
                try {
                    this.addProjectMember(projectId, userId, accessLevel);
                } catch (Exception e) {
                    log.error("新增应用成员失败", e);
                }
            }
        }
    }

    /**
     * 移除项目成员 -> ok
     *
     * @param projectId 项目id
     * @param userId    用户id
     */
    public void deleteProjectMember(Long projectId, Long userId) {
        try (GitLabApi client = gitlabAuthAdmin.auth()) {
            ProjectApi projectApi = client.getProjectApi();
            projectApi.removeMember(projectId, userId);
        } catch (Exception e) {
            log.error("移除应用成员失败", e);
            throw new BadRequestException("移除应用成员失败");
        }
    }

    /**
     * 批量移除项目成员 -> ok
     *
     * @param projectId 项目id
     * @param userIds   用户id列表
     */
    public void deleteProjectMembers(Long projectId, List<Long> userIds) {
        if (CollUtil.isNotEmpty(userIds)) {
            for (Long userId : userIds.stream().filter(Objects::nonNull).distinct().collect(Collectors.toList())) {
                try {
                    deleteProjectMember(projectId, userId);
                } catch (Exception e) {
                    log.error("移除应用成员失败", e);
                }
            }
        }
    }

    /**
     * 通过projectId查项目 -> ok
     *
     * @param projectId /
     * @return /
     */
    public Project describeProjectById(Long projectId) {
        try (GitLabApi client = gitlabAuthAdmin.auth()) {
            ProjectApi projectApi = client.getProjectApi();
            return projectApi.getProjectsStream().filter(f -> f.getId().equals(projectId)).findFirst().orElse(null);
        } catch (Exception e) {
            log.error("通过projectId查应用失败", e);
            return null;
        }
    }

    /**
     * 通过appName查项目 -> ok
     *
     * @param appName /
     * @return /
     */
    public Project describeProjectByProjectName(String appName) {
        try (GitLabApi client = gitlabAuthAdmin.auth()) {
            ProjectApi projectApi = client.getProjectApi();
            return projectApi.getProjectsStream().filter(f -> f.getPath().equals(appName)).findFirst().orElse(null);
        } catch (Exception e) {
            log.error("通过appName查应用失败", e);
            return null;
        }
    }

    /**
     * 分页获取项目 -> ok
     *
     * @param page 当前页
     * @return /
     */
    public List<Project> listProjects(int page) {
        int newPage = page <= 0 ? 1 : page;
        List<Project> list = new ArrayList<>();
        try (GitLabApi client = gitlabAuthAdmin.auth()) {
            ProjectApi projectApi = client.getProjectApi();
            return projectApi.getProjects(newPage, 100);
        } catch (Exception e) {
            log.error("分页获取应用失败", e);
            return list;
        }
    }

    /**
     * 分页获取项目 -> ok
     *
     * @param appName 应用名称
     * @return /
     */
    public List<Project> listProjectsByAppName(String appName) {
        List<Project> list = new ArrayList<>();
        try (GitLabApi client = gitlabAuthAdmin.auth()) {
            ProjectApi projectApi = client.getProjectApi();
            return projectApi.getProjectsStream(appName).collect(Collectors.toList());
        } catch (Exception e) {
            log.error("分页获取应用失败", e);
            return list;
        }
    }

    /**
     * 根据projectId删除项目
     *
     * @param projectId /
     */
    public void deleteProjectById(Long projectId) {
        try (GitLabApi client = gitlabAuthAdmin.auth()) {
            ProjectApi projectApi = client.getProjectApi();
            projectApi.deleteProject(projectId);
        } catch (Exception e) {
            log.error("根据projectId删除应用失败", e);
            throw new BadRequestException("根据projectId删除应用失败");
        }
    }

    public void deleteProjectsById(List<Long> projectIds) {
        if (CollUtil.isNotEmpty(projectIds)) {
            for (Long projectId : projectIds) {
                try {
                    deleteProjectById(projectId);
                } catch (Exception e) {
                    log.error("根据projectId删除应用失败", e);
                }
            }
        }
    }

    /**
     * 根据appName删除项目
     *
     * @param appName /
     */
    public void deleteProjectByProjectName(String appName) {
        try (GitLabApi client = gitlabAuthAdmin.auth()) {
            Project localProject = describeProjectByProjectName(appName);
            if (localProject == null) {
                throw new BadRequestException("应用不存在");
            }
            ProjectApi projectApi = client.getProjectApi();
            projectApi.deleteProject(localProject.getId());
        } catch (Exception e) {
            log.error("根据appName删除应用失败", e);
            throw new BadRequestException("根据appName删除应用失败");
        }
    }

    public void deleteProjectsByProjectName(List<String> appNames) {
        if (CollUtil.isNotEmpty(appNames)) {
            for (String appName : appNames) {
                try {
                    deleteProjectByProjectName(appName);
                } catch (Exception e) {
                    log.error("根据appName删除应用失败", e);
                }
            }
        }
    }

    /**
     * 根据关键字分页获取项目 -> ok
     *
     * @param key 关键字
     * @return /
     */
    public List<Project> searchProjects(String key) {
        List<Project> list = new ArrayList<>();
        try (GitLabApi client = gitlabAuthAdmin.auth()) {
            ProjectApi projectApi = client.getProjectApi();
            return projectApi.getProjects(key, 1, 20);
        } catch (Exception e) {
            log.error("根据关键字分页获取项目败", e);
            return list;
        }
    }

    public void initGitIgnoreFile(Long projectId, String defaultBranchName, String language) {
        String fileContent = ignoreFileAdmin.getContent(language);
        if (cn.hutool.core.util.StrUtil.isBlank(language)) {
            log.error("不支持的语言, 跳过 .gitignore 文件初始化");
            return;
        }
        try (GitLabApi client = gitlabAuthAdmin.auth()) {
            RepositoryFile gitlabAuthAdminFile = new RepositoryFile();
            gitlabAuthAdminFile.setFilePath(".gitignore");
            gitlabAuthAdminFile.setContent(fileContent);
            client.getRepositoryFileApi().createFile(projectId, gitlabAuthAdminFile, defaultBranchName, "init .gitignore");
            log.info("初始化 .gitignore 文件成功");
        } catch (GitLabApiException e) {
            log.error("初始化 .gitignore 文件失败", e);
        }
    }

    public void initGitCiFile(Long projectId, String defaultBranch, String language, String appName) {
        if (cn.hutool.core.util.StrUtil.isBlank(language)) {
            log.error("不支持的语言, 跳过 .gitlab-ci.yml 文件初始化");
            return;
        }
        try (GitLabApi client = gitlabAuthAdmin.auth()) {
            String fileContent = ciFileAdmin.getCiFileContent(language);
            RepositoryFile gitlabAuthAdminFile = new RepositoryFile();
            gitlabAuthAdminFile.setFilePath(".gitlab-ci.yml");
            gitlabAuthAdminFile.setContent(fileContent);
            client.getRepositoryFileApi().createFile(projectId, gitlabAuthAdminFile, defaultBranch, "init .gitlab-ci.yml");
            log.info("初始化 .gitlab-ci.yml 文件成功");
        } catch (GitLabApiException e) {
            log.error("初始化 .gitlab-ci.yml 文件失败", e);
        }
        try (GitLabApi client = gitlabAuthAdmin.auth()) {
            EnvEnum[] allEnvList = new EnvEnum[]{EnvEnum.Daily, EnvEnum.Stage, EnvEnum.Online};
            for (EnvEnum envEnum : allEnvList) {
                String filePath = "Dockerfile_" + envEnum.getCode();
                try {
                    String dockerfileContent = ciFileAdmin.getDockerfileContent(language, envEnum);
                    dockerfileContent = dockerfileContent.replaceAll("#APP_NAME#", appName);
                    RepositoryFile gitlabAuthAdminFile = new RepositoryFile();
                    gitlabAuthAdminFile.setFilePath(filePath);
                    gitlabAuthAdminFile.setContent(dockerfileContent);
                    client.getRepositoryFileApi().createFile(projectId, gitlabAuthAdminFile, defaultBranch, "init " + filePath);
                    log.info("初始化 {} 文件成功", filePath);
                } catch (GitLabApiException e) {
                    log.error("初始化 {} 文件失败", filePath, e);
                }
            }
        }
        String releaseFileName = appName + ".release";
        try (GitLabApi client = gitlabAuthAdmin.auth()) {
            String dockerfileContent = ciFileAdmin.getReleaseFileContent(language);
            dockerfileContent = dockerfileContent.replaceAll("#APP_NAME#", appName);
            RepositoryFile gitlabAuthAdminFile = new RepositoryFile();
            gitlabAuthAdminFile.setFilePath(releaseFileName);
            gitlabAuthAdminFile.setContent(dockerfileContent);
            client.getRepositoryFileApi().createFile(projectId, gitlabAuthAdminFile, defaultBranch, "init release");
            log.info("初始化 release 文件成功");
        } catch (GitLabApiException e) {
            log.error("初始化 release 文件失败", e);
        }
    }
}
