package cn.odboy.context;

import cn.hutool.core.io.IoUtil;
import cn.odboy.constant.AppLanguageEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class GitlabIgnoreFileAdmin implements InitializingBean {
    private final Map<String, String> innerMap = new HashMap<>();

    @Override
    public void afterPropertiesSet() throws Exception {
        try {
            innerMap.put(AppLanguageEnum.JAVA.getCode(), IoUtil.readUtf8(new ClassPathResource("ignorefile/java.ignore").getInputStream()));
            innerMap.put(AppLanguageEnum.PYTHON.getCode(), IoUtil.readUtf8(new ClassPathResource("ignorefile/python.ignore").getInputStream()));
            innerMap.put(AppLanguageEnum.VUE.getCode(), IoUtil.readUtf8(new ClassPathResource("ignorefile/vuejs.ignore").getInputStream()));
            innerMap.put(AppLanguageEnum.GO.getCode(), IoUtil.readUtf8(new ClassPathResource("ignorefile/go.ignore").getInputStream()));
            log.info("初始化Gitlab Ignore文件成功");
        } catch (IOException e) {
            log.error("初始化Gitlab Ignore文件失败", e);
        }
    }

    public String getContent(String language) {
        return innerMap.getOrDefault(language, "");
    }
}
