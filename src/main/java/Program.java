public class Program {

    public static void main(String[] args) {
        ConsumerGroupsProvider.run(args);
        ProcessedMessagesReporter.constantReport();
    }
}
