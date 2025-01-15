package cn.odboy;

import org.junit.jupiter.api.Test;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RedissonTests {
    @Autowired
    private RedissonClient redissonClient;

    @Test
    public void contextLoads() {
        RLock testLock = redissonClient.getLock("testLock");
        testLock.lock();
        try {
            System.err.println("Lock acquired and critical section executed.");
        } finally {
            testLock.unlock();
        }
    }

    public static void main(String[] args) {
    }
}

