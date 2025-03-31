import static configs.ApplicationConfig.PROCESSED_MESSAGES_COUNT_KEY;
import static configs.RedisConfig.HOST;
import static configs.RedisConfig.PORT;

import redis.clients.jedis.Jedis;

public class MessageProcessor {

    private final Jedis jedis = new Jedis(HOST, PORT);
    
    public void process(String consumerId, String channel, String message) {
        String processedMessage = message.replace("}", ", \"processed_by\": \"" + consumerId + "\"}");

        // TODO: add the processed message to Redis Stream
        // TODO: update the processed message count in Redis

        incrementProcessedMessageCount(consumerId);
    }

    private void incrementProcessedMessageCount(String consumerId) {
        var currentCount = jedis.hget(PROCESSED_MESSAGES_COUNT_KEY, consumerId);
        
        if(currentCount == null) {
            jedis.hset(PROCESSED_MESSAGES_COUNT_KEY, consumerId, "1");
            jedis.close();
            return;
        }
        
        int count = Integer.parseInt(currentCount);
        jedis.hset(PROCESSED_MESSAGES_COUNT_KEY, consumerId, String.valueOf(++count));
        jedis.close();
    }

}
