import static configs.ApplicationConfig.CONSUMER_IDS_KEY;
import static configs.ApplicationConfig.PROCESSED_MESSAGES_COUNT_KEY;
import static configs.ApplicationConfig.PUBLISHED_MESSAGES_KEY;
import static configs.RedisConfig.HOST;
import static configs.RedisConfig.PORT;
import static configs.RedisConfig.PROCESS_MESSAGES_LOCK;
import static configs.RedisConfig.REDIS_ADDRESS;
import static configs.RedisConfig.REDIS_LOCK_LEASE_TIME_MS;
import static configs.RedisConfig.REDIS_LOCK_WAIT_TIME_MS;

import java.util.concurrent.TimeUnit;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

public class RedisConsumer {

    private static final Logger logger = LoggerFactory.getLogger(RedisConsumer.class);

    private final String id;
    private final Jedis jedis = new Jedis(HOST, PORT);
    private final MessageProcessor messageProcessor;

    public RedisConsumer(String id, MessageProcessor messageProcessor) {
        this.id = id;
        this.messageProcessor = messageProcessor;
        
        jedis.lpush(CONSUMER_IDS_KEY, id);
        jedis.hset(PROCESSED_MESSAGES_COUNT_KEY, id, "0");
        logger.info("Consumer started: {}", id);
    }

    public void subscribe()  {
        Config config = new Config();
        config.useSingleServer().setAddress(REDIS_ADDRESS);

        RedissonClient redisson = Redisson.create(config);
        RLock lock = redisson.getLock(PROCESS_MESSAGES_LOCK);

        jedis.subscribe(new JedisPubSub() {
            @Override
            public void onMessage(String channel, String message) {
                try {
                    if (lock.tryLock(REDIS_LOCK_WAIT_TIME_MS, REDIS_LOCK_LEASE_TIME_MS, TimeUnit.MILLISECONDS)) {
                        messageProcessor.process(id, channel, message);
                        // TODO: should I add lock.unlock(); - test it but it seems like it
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

            }
        }, PUBLISHED_MESSAGES_KEY);
    }

}
