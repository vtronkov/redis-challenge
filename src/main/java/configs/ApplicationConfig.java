package configs;

// Disclaimer: I intentionally don't use .property file to keep the configuration in the code for simplicity sake.
// I will refactor it to use .property file if I have time.
public class ApplicationConfig {

    // Arbitrary number of max consumers. It should be based on the deployed server's capacity.
    public static final int MAX_CONSUMERS = 10;
    public static final int PROCESSED_MESSAGE_REPORT_INTERVAL = 3000; // 3 seconds
    public static final boolean ENABLE_MONITORING = true;

    public static final String PUBLISHED_MESSAGES_KEY = "messages:published";
    public static final String PROCESSED_MESSAGES_KEY = "messages:processed";
    public static final String PROCESSED_MESSAGES_BOOLEAN_KEY = "messages:processed:boolean";
    public static final String PROCESSED_MESSAGES_COUNT_KEY = "messages:processed:count";
    public static final String CONSUMER_IDS_KEY = "consumer:ids";
    
    
    private ApplicationConfig() {}
}
