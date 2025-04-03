## Scaling Message Processing in Redis Consumers

The current message processing implementation is very fast, making it difficult to observe the scalability of the program. To slow it down, we will add `Thread.sleep(1000)` in `MessageProcessor.processMessage` to simulate workload. Additionally, we will increase the report interval from 3 to 10 seconds.

### **Single Consumer Performance**
Let's run the program with a single consumer, start producing messages, and observe the logs:

```
2025-04-03 08:22:35 [main] INFO  ProcessedMessagesReporter - Reporting processed messages count for the last 10 seconds:
2025-04-03 08:22:35 [main] INFO  ProcessedMessagesReporter - Consumer: consumer-0 is processing 1 m/s. Total: 10
2025-04-03 08:22:35 [main] INFO  ProcessedMessagesReporter - ========= End of the report =========
```

As expected, we are processing 1 message per second, totaling 10 messages in 10 seconds.

### **Scaling to 10 Consumers**
Now, let's scale the consumers to 10:

```
2025-04-03 08:24:08 [main] INFO  ProcessedMessagesReporter - Reporting processed messages count for the last 10 seconds:
2025-04-03 08:24:08 [main] INFO  ProcessedMessagesReporter - Consumer: consumer-9 is processing 1 m/s. Total: 10
2025-04-03 08:24:08 [main] INFO  ProcessedMessagesReporter - Consumer: consumer-8 is processing 1 m/s. Total: 10
2025-04-03 08:24:08 [main] INFO  ProcessedMessagesReporter - Consumer: consumer-7 is processing 1 m/s. Total: 10
2025-04-03 08:24:08 [main] INFO  ProcessedMessagesReporter - Consumer: consumer-6 is processing 1 m/s. Total: 10
2025-04-03 08:24:08 [main] INFO  ProcessedMessagesReporter - Consumer: consumer-5 is processing 1 m/s. Total: 10
2025-04-03 08:24:08 [main] INFO  ProcessedMessagesReporter - Consumer: consumer-4 is processing 1 m/s. Total: 10
2025-04-03 08:24:08 [main] INFO  ProcessedMessagesReporter - Consumer: consumer-3 is processing 1 m/s. Total: 10
2025-04-03 08:24:08 [main] INFO  ProcessedMessagesReporter - Consumer: consumer-2 is processing 1 m/s. Total: 10
2025-04-03 08:24:08 [main] INFO  ProcessedMessagesReporter - Consumer: consumer-1 is processing 1 m/s. Total: 10
2025-04-03 08:24:08 [main] INFO  ProcessedMessagesReporter - Consumer: consumer-0 is processing 1 m/s. Total: 10
2025-04-03 08:24:08 [main] INFO  ProcessedMessagesReporter - ========= End of the report =========
```

We can see that throughput has increased.

### **Reducing Sleep Time to 100ms**
Let's repeat the experiment with `Thread.sleep(100)` instead of `Thread.sleep(1000)`.

#### **Single Consumer Performance**
```
2025-04-03 08:26:38 [main] INFO  ProcessedMessagesReporter - Reporting processed messages count for the last 10 seconds:
2025-04-03 08:26:38 [main] INFO  ProcessedMessagesReporter - Consumer: consumer-0 is processing 9 m/s. Total: 93
2025-04-03 08:26:38 [main] INFO  ProcessedMessagesReporter - ========= End of the report =========
```

#### **10 Consumers Performance**
```
2025-04-03 08:25:24 [main] INFO  ProcessedMessagesReporter - Reporting processed messages count for the last 10 seconds:
2025-04-03 08:25:24 [main] INFO  ProcessedMessagesReporter - Consumer: consumer-9 is processing 9 m/s. Total: 95
2025-04-03 08:25:24 [main] INFO  ProcessedMessagesReporter - Consumer: consumer-8 is processing 9 m/s. Total: 95
2025-04-03 08:25:24 [main] INFO  ProcessedMessagesReporter - Consumer: consumer-7 is processing 9 m/s. Total: 96
2025-04-03 08:25:24 [main] INFO  ProcessedMessagesReporter - Consumer: consumer-6 is processing 9 m/s. Total: 95
2025-04-03 08:25:24 [main] INFO  ProcessedMessagesReporter - Consumer: consumer-5 is processing 9 m/s. Total: 95
2025-04-03 08:25:24 [main] INFO  ProcessedMessagesReporter - Consumer: consumer-4 is processing 9 m/s. Total: 95
2025-04-03 08:25:24 [main] INFO  ProcessedMessagesReporter - Consumer: consumer-3 is processing 9 m/s. Total: 95
2025-04-03 08:25:24 [main] INFO  ProcessedMessagesReporter - Consumer: consumer-2 is processing 9 m/s. Total: 95
2025-04-03 08:25:24 [main] INFO  ProcessedMessagesReporter - Consumer: consumer-1 is processing 9 m/s. Total: 95
2025-04-03 08:25:24 [main] INFO  ProcessedMessagesReporter - Consumer: consumer-0 is processing 9 m/s. Total: 95
2025-04-03 08:25:24 [main] INFO  ProcessedMessagesReporter - ========= End of the report =========
```