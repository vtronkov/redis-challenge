import static configs.ApplicationConfig.PROCESSED_MESSAGES_COUNT_KEY;
import static configs.ApplicationConfig.PROCESSED_MESSAGE_REPORT_INTERVAL;
import static configs.RedisConfig.HOST;
import static configs.RedisConfig.PORT;

import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;

public class ProcessedMessagesReporter {
    
    private static final Jedis jedis = new Jedis(HOST, PORT);
    
    private static final Logger logger = LoggerFactory.getLogger(ProcessedMessagesReporter.class);
    
    public static void report() {
        if(!configs.ApplicationConfig.ENABLE_MONITORING) {
            return;
        }
        
        logger.info("Reporting processed messages count for the last {} seconds:", PROCESSED_MESSAGE_REPORT_INTERVAL / 1000);
        Map<String, String> processedMessagesCount = jedis.hgetAll(PROCESSED_MESSAGES_COUNT_KEY);
        if(processedMessagesCount.isEmpty()) {
            logger.info("No messages processed");
        }
        processedMessagesCount.forEach(ProcessedMessagesReporter::reportConsumer);
        logger.info("========= End report =========");
        jedis.del(PROCESSED_MESSAGES_COUNT_KEY);
    }
    
    private static void reportConsumer(String consumerId, String processedMessagesCount) {
        int roundedMsgPerSec = Integer.parseInt(processedMessagesCount) / (PROCESSED_MESSAGE_REPORT_INTERVAL / 1000);
        logger.info("Consumer: {} is processing {} m/s. Total: {}", consumerId, roundedMsgPerSec, processedMessagesCount);
    }
    
}
