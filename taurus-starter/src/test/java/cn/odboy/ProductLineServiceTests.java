package cn.odboy;

import cn.odboy.common.model.MetaOptionModel;
import cn.odboy.service.ProductLineService;
import com.alibaba.fastjson.JSON;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ProductLineServiceTests {
    @Autowired
    private ProductLineService productLineService;

    @Test
    public void test1() {
        List<MetaOptionModel> selectOptions = productLineService.queryPathList();
        System.err.println(JSON.toJSONString(selectOptions));
    }
}

