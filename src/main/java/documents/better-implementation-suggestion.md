# Message Deduplication Issue

Currently, all the consumers receive each message, and we need a way to deduplicate it. I am using a locking mechanism to achieve that. This complicates the code and slows it down. As far as I can tell, we can use Redis **consumer groups** for the subscribers. The Redis consumer groups will ensure that only one message is sent to each consumer.