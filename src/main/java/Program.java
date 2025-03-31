import static configs.ApplicationConfig.CONSUMER_IDS_KEY;
import static configs.ApplicationConfig.MAX_CONSUMERS;
import static configs.ApplicationConfig.PROCESSED_MESSAGE_REPORT_INTERVAL;
import static configs.RedisConfig.HOST;
import static configs.RedisConfig.PORT;

import redis.clients.jedis.Jedis;

public class Program {

    public static void main(String[] args) {
        int numberOfConsumers = getAndValidateNumberOfConsumers(args);
        cleanConsumerIds();
        startConsumers(numberOfConsumers);
        processedMessageReport();
    }

    private static void cleanConsumerIds() {
        Jedis jedis = new Jedis(HOST, PORT);
        jedis.del(CONSUMER_IDS_KEY);
        jedis.close();
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

    private static void startConsumers(int numberOfConsumers) {
        for(int i = 0; i < numberOfConsumers; i++) {
            String consumerId = "consumer-" + i;
            new Thread(() -> {
                MessageProcessor messageProcessor = new MessageProcessor();
                RedisConsumer consumer = new RedisConsumer(consumerId, messageProcessor);
                try {
                    consumer.subscribe();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }).start();
        }
    }
    
    private static void processedMessageReport() {
        while (true) {
            try {
                Thread.sleep(PROCESSED_MESSAGE_REPORT_INTERVAL);
                ProcessedMessagesReporter.report();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
