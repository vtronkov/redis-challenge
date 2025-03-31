import static configs.ApplicationConfig.PROCESSED_MESSAGES_COUNT_KEY;
import static configs.ApplicationConfig.PROCESSED_MESSAGES_KEY;
import static configs.RedisConfig.HOST;
import static configs.RedisConfig.PORT;

import java.util.Map;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.StreamEntryID;

public class MessageProcessor {

    private final Jedis jedis = new Jedis(HOST, PORT);
    
    public void process(String consumerId, String message) {
        String processedMessage = message.replace("}", ", \"processed\": \"true\"}");
        Map<String, String> streamData = Map.of("message", processedMessage, "processed_by", consumerId);
        jedis.xadd(PROCESSED_MESSAGES_KEY, StreamEntryID.NEW_ENTRY, streamData);
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
