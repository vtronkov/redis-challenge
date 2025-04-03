import static configs.ApplicationConfig.PROCESSED_MESSAGES_SET_KEY;
import static configs.ApplicationConfig.PUBLISHED_MESSAGES_KEY;
import static org.junit.jupiter.api.Assertions.assertTrue;

import configs.RedisConfig;
import java.util.HashSet;
import java.util.Random;
import java.util.UUID;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;
import redis.clients.jedis.Jedis;

class ConsumerGroupsProviderTest {

    private static final GenericContainer<?> redisContainer =
        new GenericContainer<>(DockerImageName.parse("redis:latest")).withExposedPorts(6379);
    
    private static Jedis jeids;

    @BeforeAll
    static void setUp() {
        redisContainer.start();
        String redisHost = redisContainer.getHost();
        Integer redisPort = redisContainer.getMappedPort(6379);
        RedisConfig.HOST = redisHost;
        RedisConfig.PORT = redisPort;
        jeids = new Jedis(redisHost, redisPort);
    }

    @AfterAll
    static void tearDown() {
        jeids.close();
        redisContainer.stop();
    }

    // Node that this is a long-running test and may take a while to complete.
    // It's starting a consumer group, sending 10,000 messages and checking if all of them have been processed.
    // We do this twice - once with 1 consumer and once with 10 consumers.
    @ParameterizedTest
    @ValueSource(ints = {1, 10})
    void testMessageProcessing(int numberOfConsumers) throws InterruptedException {
        ConsumerGroupsProvider.run(numberOfConsumers);
        
        // Wait for the consumers to start
        Thread.sleep(10000);

        HashSet<String> publishedMessageIds = new HashSet<>();
        
        for(int i = 0; i < 10_000; i++) {
            String messageId = UUID.randomUUID().toString();
            publishedMessageIds.add(messageId);
            String message = generateMockMessage(messageId);
            jeids.publish(PUBLISHED_MESSAGES_KEY, message);
            
            // Slow down the message publishing
            long sleepTime = new Random().nextLong(1, 50);
            Thread.sleep(sleepTime);
        }

        // Give some time for the messages to be processed
        Thread.sleep(5000);
        
        var processedMessagesSet = jeids.smembers(PROCESSED_MESSAGES_SET_KEY);

        // Check if all the messages have been processed with no loss
        publishedMessageIds.forEach(m -> assertTrue(processedMessagesSet.contains(m))   );
    }
    
    private String generateMockMessage(String uuid) {
        return String.format("{\"message_id\":\"%s\"}", uuid);
    }
}
