import static configs.ApplicationConfig.CONSUMER_IDS_KEY;
import static configs.ApplicationConfig.MAX_CONSUMERS;
import static configs.RedisConfig.HOST;
import static configs.RedisConfig.PORT;

import java.util.concurrent.CountDownLatch;
import redis.clients.jedis.Jedis;

public class Program {

    public static void main(String[] args) throws Exception {
        int numberOfConsumers = getNumberOfConsumers(args);
        cleanConsumerIds();
        startConsumers(numberOfConsumers);
        blockCurrentThread();
    }

    private static void cleanConsumerIds() {
        Jedis jedis = new Jedis(HOST, PORT);
        jedis.del(CONSUMER_IDS_KEY);
        jedis.close();
    }

    private static int getNumberOfConsumers(String[] args) {
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
                RedisConsumer consumer = new RedisConsumer(consumerId, HOST, PORT);
                try {
                    consumer.subscribe();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }).start();
        }
    }
    
    // Use CountDownLatch to block the main thread and keep the program running
    private static void blockCurrentThread() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        latch.await();
    }
    
    /*
    TODOs:
    - Use log4j for logging
    - The consumer log should be decoupled from the consumer
        - the consumer will count the processed messages and put them into redis
        - another thread will read all the consumers counts every N seconds, log them and clean them
    - Create a separate class for processing the message
     */

}
