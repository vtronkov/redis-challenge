import static configs.ApplicationConfig.PROCESSED_MESSAGES_COUNT_KEY;
import static configs.ApplicationConfig.PROCESSED_MESSAGE_REPORT_INTERVAL;
import static configs.RedisConfig.HOST;
import static configs.RedisConfig.PORT;

import configs.ApplicationConfig;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;

// The class to report the processed messages count for the last interval.
// Each time a message is processed, the count will be incremented. 
// Every interval the count will be reported and the count will be reset.
public class ProcessedMessagesReporter {
    
    private static final Jedis jedis = new Jedis(HOST, PORT);
    
    private static final Logger logger = LoggerFactory.getLogger(ProcessedMessagesReporter.class);
    
    private ProcessedMessagesReporter() {}

    // Clogs the current thread to report the processed messages count for the last interval.
    // Executes the report every PROCESSED_MESSAGE_REPORT_INTERVAL milliseconds.
    public static void constantReport() {
        while (true) {
            try {
                Thread.sleep(PROCESSED_MESSAGE_REPORT_INTERVAL);
                report();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
    
    public static void report() {
        if(!ApplicationConfig.ENABLE_MONITORING) {
            return;
        }
        
        logger.info("Reporting processed messages count for the last {} seconds:", PROCESSED_MESSAGE_REPORT_INTERVAL / 1000);
        Map<String, String> processedMessagesCount = jedis.hgetAll(PROCESSED_MESSAGES_COUNT_KEY);
        if(processedMessagesCount.isEmpty()) {
            logger.info("No messages processed");
        }
        processedMessagesCount.forEach(ProcessedMessagesReporter::reportConsumer);
        logger.info("========= End of the report =========");
        jedis.del(PROCESSED_MESSAGES_COUNT_KEY); // clear the processed messages count for the current interval
    }
    
    private static void reportConsumer(String consumerId, String processedMessagesCount) {
        int roundedMsgPerSec = Integer.parseInt(processedMessagesCount) / (PROCESSED_MESSAGE_REPORT_INTERVAL / 1000);
        logger.info("Consumer: {} is processing {} m/s. Total: {}", consumerId, roundedMsgPerSec, processedMessagesCount);
    }
    
}
