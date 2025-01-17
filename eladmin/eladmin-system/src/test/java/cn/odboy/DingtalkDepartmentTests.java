package cn.odboy;

import com.alibaba.fastjson.JSON;
import lombok.SneakyThrows;
import cn.odboy.repository.DingtalkDepartmentRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * 钉钉部门 测试
 *
 * @author odboy
 * @date 2025-01-16
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class DingtalkDepartmentTests {
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
