import static configs.ApplicationConfig.CONSUMER_IDS_KEY;
import static configs.ApplicationConfig.MAX_CONSUMERS;
import static configs.RedisConfig.HOST;
import static configs.RedisConfig.PORT;

import redis.clients.jedis.Jedis;

// The main entry point for the program.
// It will start the consumers based on the number of consumers provided in the arguments.
// It will clean the consumer IDs and start the consumers.
public class ConsumerGroupsProvider {

    private ConsumerGroupsProvider() {}
    
    public static void run(String[] args) {
        int numberOfConsumers = getAndValidateNumberOfConsumers(args);
        cleanConsumerIds();
        startConsumers(numberOfConsumers);
    }

    private static int getAndValidateNumberOfConsumers(String[] args) {
        int numberOfConsumers = args.length > 0 ? Integer.parseInt(args[0]) : 1;
        if(numberOfConsumers <= 0) {
            throw new IllegalArgumentException("Number of consumers must be greater than 0");
        }
        if(numberOfConsumers > MAX_CONSUMERS) {
            throw new IllegalArgumentException("Number of consumers must be less than or equal to " + MAX_CONSUMERS);
        }
        return numberOfConsumers;
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
