package me.zhengjie;

import me.zhengjie.context.DingtalkAuthAdmin;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

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

