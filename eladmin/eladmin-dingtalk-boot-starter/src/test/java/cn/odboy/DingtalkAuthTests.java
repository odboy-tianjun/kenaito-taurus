package cn.odboy;

import cn.odboy.context.DingtalkAuthAdmin;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * 钉钉认证 测试
 *
 * @author odboy
 * @date 2025-01-16
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class DingtalkAuthTests {
    @Autowired
    private DingtalkAuthAdmin dingtalkAuthAdmin;

    @Test
    public void contextLoads() {
        String auth = dingtalkAuthAdmin.auth();
        System.err.println(auth);
    }

    public static void main(String[] args) {
    }
}
