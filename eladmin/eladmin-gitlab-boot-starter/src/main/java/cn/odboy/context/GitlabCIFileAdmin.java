package cn.odboy.context;

import cn.hutool.core.io.IoUtil;
import cn.odboy.constant.AppLanguageEnum;
import lombok.extern.slf4j.Slf4j;
import cn.odboy.constant.EnvEnum;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class GitlabCIFileAdmin implements InitializingBean {
    private final Map<String, String> innerCIFileMap = new HashMap<>();
    private final Map<String, String> innerDockerDailyFileMap = new HashMap<>();
    private final Map<String, String> innerDockerStageFileMap = new HashMap<>();
    private final Map<String, String> innerDockerOnlineFileMap = new HashMap<>();
    private final Map<String, String> innerReleaseFileMap = new HashMap<>();

    @Override
    public void afterPropertiesSet() throws Exception {
        try {
            innerCIFileMap.put(AppLanguageEnum.JAVA.getCode(), IoUtil.readUtf8(new ClassPathResource("cifile/java/.gitlab-ci.yml").getInputStream()));
            innerDockerDailyFileMap.put(AppLanguageEnum.JAVA.getCode(), IoUtil.readUtf8(new ClassPathResource("cifile/java/Dockerfile_daily").getInputStream()));
            innerDockerStageFileMap.put(AppLanguageEnum.JAVA.getCode(), IoUtil.readUtf8(new ClassPathResource("cifile/java/Dockerfile_stage").getInputStream()));
            innerDockerOnlineFileMap.put(AppLanguageEnum.JAVA.getCode(), IoUtil.readUtf8(new ClassPathResource("cifile/java/Dockerfile_online").getInputStream()));
            innerReleaseFileMap.put(AppLanguageEnum.JAVA.getCode(), IoUtil.readUtf8(new ClassPathResource("cifile/java/app.release").getInputStream()));
            log.info("初始化Gitlab CI文件成功");
        } catch (IOException e) {
            log.error("初始化Gitlab CI文件失败", e);
        }
    }

    public String getCIFileContent(String language) {
        return innerCIFileMap.getOrDefault(language, null);
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
