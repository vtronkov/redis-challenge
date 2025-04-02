import static configs.ApplicationConfig.PROCESSED_MESSAGES_SET_KEY;
import static configs.ApplicationConfig.PROCESSED_MESSAGES_COUNT_KEY;
import static configs.ApplicationConfig.PROCESSED_MESSAGES_KEY;
import static configs.RedisConfig.HOST;
import static configs.RedisConfig.PORT;

import java.util.Map;
import org.json.JSONObject;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.StreamEntryID;

public class MessageProcessor {

    private final Jedis jedis = new Jedis(HOST, PORT);
    
    public void process(String consumerId, String message) {
        String messageId = getMessageId(message);
        
        // validate if the message has been processed
        if(jedis.sismember(PROCESSED_MESSAGES_SET_KEY, messageId)) {
            return;
        }

        String processedMessage = processMessage(message);
        addToRedisStream(messageId, processedMessage, consumerId);
        incrementProcessedMessageCount(consumerId);
        jedis.sadd(PROCESSED_MESSAGES_SET_KEY, messageId); // Mark as processed
    }

    private static String processMessage(String message) {
        return new JSONObject(message).accumulate("processed", "true").toString();
    }
    
    private void addToRedisStream(String messageId, String processedMessage, String consumerId) {
        Map<String, String> streamData =
            Map.of("message_id", messageId, "message", processedMessage, "processed_by", consumerId);
        jedis.xadd(PROCESSED_MESSAGES_KEY, StreamEntryID.NEW_ENTRY, streamData);
    }

    private static String getMessageId(String message) {
        return new JSONObject(message).getString("message_id");
    }

    private void incrementProcessedMessageCount(String consumerId) {
        var currentCount = jedis.hget(PROCESSED_MESSAGES_COUNT_KEY, consumerId);
        
        if(currentCount == null) {
            jedis.hset(PROCESSED_MESSAGES_COUNT_KEY, consumerId, "1");
            return;
        }
        
        int count = Integer.parseInt(currentCount);
        jedis.hset(PROCESSED_MESSAGES_COUNT_KEY, consumerId, String.valueOf(++count));
    }

}
