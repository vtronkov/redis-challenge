package configs;

// Disclaimer: I intentionally don't use .property file to keep the configuration in the code for simplicity sake.
// I will refactor it to use .property file if I have time.
public class RedisConfig {

    public static final String HOST = "localhost";
    public static final int PORT = 6379;
    public static final String REDIS_ADDRESS = String.format("redis://%s:%s", HOST, PORT);
    
    public static final String PROCESS_MESSAGES_LOCK = "process-messages-lock";
    
    // Caution: These settings will vary based on the server's capacity and the message processing time.
    public static final int REDIS_LOCK_WAIT_TIME_MS = 0;
    public static final int REDIS_LOCK_LEASE_TIME_MS = 4;
    
    private RedisConfig() {}
    
}
