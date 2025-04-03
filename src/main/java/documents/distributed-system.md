The current project is designed to be scalable (running multiple nodes), but it's not there yet.

For the sake of the conversation, I will call each instance of the program a "node."

The scalability of the system depends on what we want to achieve. If we take this literally:

**Important! Ensure only one consumer within the group processes each message.**

This means that we want each node to receive and process each message.

To achieve that, we need to do a couple of things:

1. Each node needs to have a unique ID (UUID) generated at the beginning.
2. This node ID should be appended into:
   - The consumer IDs.
   - The lock mechanism key.
   - The processed messages count key.
3. Additionally, to keep the "consumer:ids" up-to-date, we can't just clean up the list every time a node is started (as it's implemented now). We need to implement a heartbeat mechanism that updates the table to indicate that the node is alive.
4. On top of that, we need a mechanism to regularly check if a node went offline and clean it from the "consumer:ids."

That way, each node will be responsible for its own messages.

---

However, if we want each message to be processed only once per all available consumers:

1. We need to implement the same heartbeat mechanism as above.
2. Instead of naming consumers "consumer-1," "consumer-2," etc., we will assign unique IDs to each of them.

This way, each message will be consumed only once, regardless of how many nodes and consumers we have.

