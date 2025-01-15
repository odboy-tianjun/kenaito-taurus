package cn.odboy.infra;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import ch.qos.logback.core.encoder.Encoder;
import lombok.Data;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.nio.charset.StandardCharsets;

/**
 * 日志推送到redis队列
 *
 * @author odboy
 * @date 2025-01-15
 */
@Data
public class LogPushAppender extends AppenderBase<ILoggingEvent> {
    private String host = "localhost";
    private int port = 6379;
    private String password = null;
    private String queueName = "LoggingQueue";
    private JedisPool jedisPool;
    private Encoder<ILoggingEvent> encoder;

    @Override
    public void start() {
        super.start();
        jedisPool = new JedisPool(new JedisPoolConfig(), host, port, 2000, password);
    }

    @Override
    public void stop() {
        super.stop();
        if (jedisPool != null) {
            jedisPool.close();
        }
    }

    @Override
    protected void append(ILoggingEvent eventObject) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.lpush(queueName, new String(encoder.encode(eventObject), StandardCharsets.UTF_8));
        } catch (Exception e) {
            addError("Failed to send log to Redis", e);
        }
    }
}
