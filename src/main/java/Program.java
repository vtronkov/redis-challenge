import static configs.ApplicationConfig.MAX_CONSUMERS;

public class Program {

    public static void main(String[] args) {
        int numberOfConsumers = getAndValidateNumberOfConsumers(args);
        ConsumerGroupsProvider.run(numberOfConsumers);
        ProcessedMessagesReporter.constantReport();
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
}
