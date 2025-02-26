package cn.odboy;

import cn.odboy.domain.OpsConfig;
import cn.odboy.service.OpsConfigService;
import com.alibaba.fastjson.JSON;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class OpsConfigTests {
    @Autowired
    private OpsConfigService opsConfigService;

    @Test
    public void testNetBlockConfig() {
        OpsConfig.BlockNetwork blockNetworkConfig = opsConfigService.getBlockNetworkConfig();
        System.err.println(JSON.toJSONString(blockNetworkConfig));
    }
}

