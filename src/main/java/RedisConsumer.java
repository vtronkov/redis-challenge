import static configs.ApplicationConfig.CONSUMER_IDS_KEY;
import static configs.ApplicationConfig.PUBLISHED_MESSAGES_KEY;
import static configs.RedisConfig.PROCESS_MESSAGES_LOCK;
import static configs.RedisConfig.REDIS_ADDRESS;

import java.util.concurrent.TimeUnit;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

public class RedisConsumer {

    private final String id;
    private final Jedis jedis;

    public RedisConsumer(String id, String redisHost, int redisPort) {
        this.jedis = new Jedis(redisHost, redisPort);
        jedis.lpush(CONSUMER_IDS_KEY, id);
        this.id = id;
        System.out.println("Consumer started: " + id);
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
                    // TODO: play with the wait and lease time. These are most-probably not optimal.
                    if (lock.tryLock(5, 10, TimeUnit.MILLISECONDS)) {
                        System.out.println("Processing message: " + message + " by " + id);
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

            }
        }, PUBLISHED_MESSAGES_KEY);
    }

    // TODO: move out into a separate class
    private void processMessage(String message) {
        String processedMessage = message.replace("}", ", \"processed_by\": \"" + id + "\"}");
        //jedis.xadd();...
        System.out.println("Processed: " + processedMessage);
    }

}
