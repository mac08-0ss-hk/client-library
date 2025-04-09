# Process API REST Client Library

A robust REST client library for Java applications with built-in support for:
- Circuit breaking
- Rate limiting
- Retry mechanisms
- Request/Response logging
- Timeout management

## Features

- **Circuit Breaking**: Prevents cascading failures by opening the circuit when failures exceed a threshold
- **Rate Limiting**: Controls the rate of requests to prevent overwhelming services
- **Retry Mechanism**: Automatically retries failed requests with configurable backoff
- **Logging**: Comprehensive request and response logging
- **Timeout Management**: Configurable connection and read timeouts
- **Spring Boot Integration**: Seamless integration with Spring Boot applications

## Installation

Add the dependency to your `pom.xml`:

```xml
<dependency>
    <groupId>com.processapi</groupId>
    <artifactId>papi-commons-rest</artifactId>
    <version>${version}</version>
</dependency>
```

## Configuration

Configure your REST clients in `application.yml`:

```yaml
rest-client:
  clients:
    service1:
      base-url: http://service1.example.com
      connect-timeout: 5000
      read-timeout: 10000
      max-connections: 100
      max-connections-per-route: 50
    service2:
      base-url: http://service2.example.com
      connect-timeout: 3000
      read-timeout: 5000
      max-connections: 50
      max-connections-per-route: 25
```

## Usage

Inject the `RestClientFactory` and create clients:

```java
@Autowired
private RestClientFactory restClientFactory;

public void makeRequest() {
    RestClientBase client = restClientFactory.createClient("service1");
    
    // Make HTTP requests
    String response = client.get("/api/resource", String.class);
    
    // Or with request body
    String requestBody = "{\"key\":\"value\"}";
    String response = client.post("/api/resource", requestBody, String.class);
}
```

## Configuration Properties

| Property | Description | Default |
|----------|-------------|---------|
| `rest-client.clients` | Map of client configurations | - |
| `rest-client.circuit-breaker.failure-threshold` | Number of failures before opening circuit | 5 |
| `rest-client.circuit-breaker.reset-timeout` | Time to wait before resetting circuit (ms) | 60000 |
| `rest-client.circuit-breaker.half-open-timeout` | Time in half-open state (ms) | 30000 |
| `rest-client.retry.max-attempts` | Maximum retry attempts | 3 |
| `rest-client.retry.initial-interval` | Initial retry interval (ms) | 1000 |
| `rest-client.retry.multiplier` | Exponential backoff multiplier | 2.0 |
| `rest-client.retry.max-interval` | Maximum retry interval (ms) | 10000 |
| `rest-client.rate-limit.permits-per-second` | Requests allowed per second | 10 |
| `rest-client.rate-limit.max-burst-seconds` | Maximum burst time (s) | 1 |
| `rest-client.rate-limit.enabled` | Whether rate limiting is enabled | true |

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Support

For support, please contact the Process API team or create an issue in the repository.

## Changelog

### 1.0.0-SNAPSHOT
- Initial release
- Support for multiple REST endpoints
- Configurable connection pooling
- SSL/TLS support
- Comprehensive error handling
- Request/Response interceptors
- Retry mechanism with exponential backoff