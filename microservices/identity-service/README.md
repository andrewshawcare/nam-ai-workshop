# Identity Service

This microservice handles user authentication and issues JWT tokens for the Task Management System.

## Features

- User registration
- User authentication (login)
- JWT token generation
- User profile retrieval

## Technology Stack

- Java 17
- Spring Boot 3.1.5
- Spring Security
- Spring Data JPA
- PostgreSQL
- Flyway for database migrations
- JWT for authentication
- Swagger/OpenAPI for API documentation

## Getting Started

### Prerequisites

- JDK 17 or higher
- Gradle 7.6 or higher
- PostgreSQL 15 or higher

### Environment Setup

1. Copy the `.env.example` file to `.env` and adjust the values as needed:
   ```bash
   cp .env.example .env
   ```

2. Create a PostgreSQL database:
   ```sql
   CREATE DATABASE identity_service;
   CREATE USER identity_app WITH ENCRYPTED PASSWORD 'identity_password';
   GRANT ALL PRIVILEGES ON DATABASE identity_service TO identity_app;
   ```

### Building the Application

```bash
./gradlew clean build
```

### Running the Application

```bash
./gradlew bootRun
```

Or with a specific profile:

```bash
./gradlew bootRun --args='--spring.profiles.active=dev'
```

### Running with Docker

Build the Docker image:

```bash
docker build -t identity-service .
```

Run the container:

```bash
docker run -p 8080:8080 --env-file .env identity-service
```

## API Documentation

Once the application is running, you can access the API documentation at:

- Swagger UI: http://localhost:8080/api/swagger-ui.html
- OpenAPI JSON: http://localhost:8080/api/v3/api-docs

## API Endpoints

| Method | Path | Description |
| - | - | - |
| POST | /register | Register a new user |
| POST | /login | Authenticate and return JWT |
| GET | /me | Return current authenticated user |

## Development

### Running Tests

```bash
./gradlew test
```

### Code Coverage

```bash
./gradlew jacocoTestReport
```

The report will be generated at `build/reports/jacoco/test/html/index.html`.

## Deployment

The service is containerized and can be deployed using Docker. See the Dockerfile for details.

For production deployment, make sure to:
1. Set appropriate environment variables
2. Use a secure JWT secret
3. Configure proper database credentials
4. Set up proper logging

## License

This project is licensed under the MIT License - see the LICENSE file for details.