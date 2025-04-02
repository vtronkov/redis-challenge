import static configs.ApplicationConfig.CONSUMER_IDS_KEY;
import static configs.RedisConfig.HOST;
import static configs.RedisConfig.PORT;

import redis.clients.jedis.Jedis;

// The main entry point for the program.
// It will start the consumers based on the number of consumers provided in the arguments.
// It will clean the consumer IDs and start the consumers.
public class ConsumerGroupsProvider {

    private ConsumerGroupsProvider() {}
    
    public static void run(int numberOfConsumers) {
        cleanConsumerIds();
        startConsumers(numberOfConsumers);
    }
    
    private static void cleanConsumerIds() {
        Jedis jedis = new Jedis(HOST, PORT);
        jedis.del(CONSUMER_IDS_KEY);
        jedis.close();
    }
    
    private static void startConsumers(int numberOfConsumers) {
        for(int i = 0; i < numberOfConsumers; i++) {
            String consumerId = "consumer-" + i;
            new Thread(() -> {
                MessageProcessor messageProcessor = new MessageProcessor();
                RedisConsumer consumer = new RedisConsumer(consumerId, messageProcessor);
                consumer.subscribe();
            }).start();
        }
    }
    
}
