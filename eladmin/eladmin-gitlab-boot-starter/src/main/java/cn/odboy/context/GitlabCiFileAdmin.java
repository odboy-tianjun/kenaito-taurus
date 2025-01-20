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
package cn.odboy.context;

import cn.hutool.core.io.IoUtil;
import cn.odboy.constant.GitlabProjectLanguageEnum;
import cn.odboy.constant.EnvEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Gitlab CI文件管理工具
 *
 * @author odboy
 * @date 2024-10-01
 */
@Slf4j
@Component
public class GitlabCiFileAdmin implements InitializingBean {
    private final Map<String, String> innerCiFileMap = new HashMap<>();
    private final Map<String, String> innerDockerDailyFileMap = new HashMap<>();
    private final Map<String, String> innerDockerStageFileMap = new HashMap<>();
    private final Map<String, String> innerDockerOnlineFileMap = new HashMap<>();
    private final Map<String, String> innerReleaseFileMap = new HashMap<>();

    @Autowired
    private GitlabProperties properties;

    @Override
    public void afterPropertiesSet() throws Exception {
        if (properties.getEnable() == null || !properties.getEnable()) {
            log.info("================== 未启用Gitlab功能，不初始化CI文件 ==================");
            return;
        }
        try {
            innerCiFileMap.put(GitlabProjectLanguageEnum.JAVA.getCode(), IoUtil.readUtf8(new ClassPathResource("cifile/java/.gitlab-ci.yml").getInputStream()));
            innerDockerDailyFileMap.put(GitlabProjectLanguageEnum.JAVA.getCode(), IoUtil.readUtf8(new ClassPathResource("cifile/java/Dockerfile_daily").getInputStream()));
            innerDockerStageFileMap.put(GitlabProjectLanguageEnum.JAVA.getCode(), IoUtil.readUtf8(new ClassPathResource("cifile/java/Dockerfile_stage").getInputStream()));
            innerDockerOnlineFileMap.put(GitlabProjectLanguageEnum.JAVA.getCode(), IoUtil.readUtf8(new ClassPathResource("cifile/java/Dockerfile_online").getInputStream()));
            innerReleaseFileMap.put(GitlabProjectLanguageEnum.JAVA.getCode(), IoUtil.readUtf8(new ClassPathResource("cifile/java/app.release").getInputStream()));
            log.info("初始化Gitlab CI文件成功");
        } catch (IOException e) {
            log.error("初始化Gitlab CI文件失败", e);
        }
    }

    public String getCiFileContent(String language) {
        return innerCiFileMap.getOrDefault(language, null);
    }

    public String getDockerfileContent(String language, EnvEnum envEnum) {
        switch (envEnum) {
            case Daily:
                return innerDockerDailyFileMap.getOrDefault(language, "");
            case Stage:
                return innerDockerStageFileMap.getOrDefault(language, "");
            case Online:
                return innerDockerOnlineFileMap.getOrDefault(language, "");
            default:
                return innerDockerDailyFileMap.getOrDefault(language, "");
        }
    }

    public String getReleaseFileContent(String language) {
        return innerReleaseFileMap.getOrDefault(language, "");
    }
}
