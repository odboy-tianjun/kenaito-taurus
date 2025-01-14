package cn.odboy;

import com.alibaba.fastjson.JSON;
import lombok.SneakyThrows;
import cn.odboy.repository.DingtalkDepartmentRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class DingtalkTests {
    @Autowired
    private DingtalkDepartmentRepository dingtalkDepartmentRepository;

    @Test
    @SneakyThrows
    public void testDingtalkDepartmentRepository() {
        dingtalkDepartmentRepository.listAllSubDepartments(1, (list) -> {
            System.err.println(JSON.toJSONString(list));
        });
    }
}

