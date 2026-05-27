# API Token Handler

A Maven-based Java service for handling API token retrieval using Apache HTTP Client.

## Project Overview

This project provides a `TokenHandlerService` class that:
- Makes HTTP POST requests to API endpoints
- Retrieves API tokens from JSON responses
- Handles multiple token field naming conventions (`token`, `access_token`, `apiToken`)
- Includes comprehensive error handling and logging

## Prerequisites

- Java 11 or higher
- Maven 3.6.0 or higher

## Building the Project

```bash
mvn clean install
```

## Running Tests

```bash
mvn test
```

## Project Structure

```
api-token-handler/
├── pom.xml
├── src/
│   ├── main/
│   │   └── java/com/api/token/handler/
│   │       └── TokenHandlerService.java
│   └── test/
│       └── java/com/api/token/handler/
│           └── TokenHandlerServiceTest.java
└── README.md
```

## Dependencies

- **Apache HTTP Client 4.5.14** - For making HTTP requests
- **SLF4J 1.7.36** - For logging
- **Jackson Databind** - For JSON processing
- **JUnit 4.13.2** - For unit testing

## Usage Example

```java
TokenHandlerService tokenService = new TokenHandlerService();
String token = tokenService.getApiToken(
    "https://api.example.com/token",
    "{\"username\":\"user\",\"password\":\"pass\"}"
);
```

## License

MIT License
