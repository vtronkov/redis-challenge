# Redis Message Processing System

This project implements a distributed message processing system using Redis as the message broker. It consists of multiple consumers that process messages published to Redis and ensures message deduplication and processing guarantees.

## Prerequisites

- Java 21 or higher
- Redis server running locally or accessible
- Docker (for running functional tests)

## Configuration

The application uses the following default Redis configuration (configurable in `RedisConfig.java`):
- Host: localhost
- Port: 6379
- Lock wait time: 2ms
- Lock lease time: 5ms

Additional application configurations (configurable in `ApplicationConfig.java`):
- Maximum number of consumers: 10
- Monitoring enabled: true
- Processed message report interval: 3000ms (3 seconds)
- All the Redis keys

## Running the Application

1. Ensure Redis is running and accessible
2. Build the application:
   ```bash
   ./gradlew clean shadowJar
   ```
3. Run the application with the desired number of consumers (default is 1):
   ```bash
   java -jar build/libs/redis-challenge.jar 5
   ```

## Running Tests

The project includes functional tests that use TestContainers.

**Node** that these are long-running tests and may take a while to complete.

To run the tests:

1. Ensure Docker is running
2. Run the tests:
   ```bash
   ./gradlew test
   ```

## Monitoring

The application provides real-time monitoring of processed messages:
- Each consumer's processing rate is reported every 3 seconds
- Total processed messages count is maintained per consumer
- Processing statistics are logged to the console

## Future Improvements

The following improvements are planned for future development:

### Configuration Management
- [ ] Migrate from hardcoded constants to a `.properties` file

### Security Enhancements
- [ ] Implement secure Redis connection instead of connecting without a password

### Testing
- [ ] Add comprehensive unit tests

### Architecture Improvements
- [ ] Replace direct Jedis instance creation with DI
- [ ] Create Redis abstraction layer instead of directly using Jedis