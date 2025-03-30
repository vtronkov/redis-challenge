package configs;

// Disclaimer: I intentionally don't use .property file to keep the configuration in the code for simplicity sake.
// I will refactor it to use .property file if I have time.
public class RedisConfig {

    public static final String HOST = "localhost";
    public static final int PORT = 6379;
    public static final String REDIS_ADDRESS = String.format("redis://%s:%s", HOST, PORT);
    public static final String PROCESS_MESSAGES_LOCK = "process-messages-lock";
    
    private RedisConfig() {}
    
}
