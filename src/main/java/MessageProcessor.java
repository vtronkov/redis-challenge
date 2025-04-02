import static configs.ApplicationConfig.PROCESSED_MESSAGES_BOOLEAN_KEY;
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

        if(jedis.hget(PROCESSED_MESSAGES_BOOLEAN_KEY, messageId) != null) {
            return;
        }
        jedis.hset(PROCESSED_MESSAGES_BOOLEAN_KEY, messageId, "true");
        
        String processedMessage = new JSONObject(message).accumulate("processed", "true").toString();
        Map<String, String> streamData = 
            Map.of("message_id", messageId, "message", processedMessage, "processed_by", consumerId);
        jedis.xadd(PROCESSED_MESSAGES_KEY, StreamEntryID.NEW_ENTRY, streamData);
        incrementProcessedMessageCount(consumerId);
    }

    private static String getMessageId(String message) {
        return new JSONObject(message).getString("message_id");
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
