# Task Service

This microservice handles task management for authenticated users in the Task Management System.

## Features

- Create tasks
- List tasks for authenticated user
- Update tasks
- Delete tasks
- JWT-based authentication

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
- Identity Service (for authentication)

### Environment Setup

1. Copy the `.env.example` file to `.env` and adjust the values as needed:
   ```bash
   cp .env.example .env
   ```

2. Create a PostgreSQL database:
   ```sql
   CREATE DATABASE task_service;
   CREATE USER task_app WITH ENCRYPTED PASSWORD 'task_password';
   GRANT ALL PRIVILEGES ON DATABASE task_service TO task_app;
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
docker build -t task-service .
```

Run the container:

```bash
docker run -p 8081:8081 --env-file .env task-service
```

## API Documentation

Once the application is running, you can access the API documentation at:

- Swagger UI: http://localhost:8081/api/swagger-ui.html
- OpenAPI JSON: http://localhost:8081/api/v3/api-docs

## API Endpoints

| Method | Path | Description |
| - | - | - |
| POST | /tasks | Create a new task |
| GET | /tasks | List all tasks for user |
| PUT | /tasks/{id} | Update an existing task |
| DELETE | /tasks/{id} | Delete an existing task |

## Authentication

This service requires a valid JWT token issued by the Identity Service. The token must be included in the Authorization header of each request:

```
Authorization: Bearer <token>
```

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
2. Use a secure JWT secret (must match the Identity Service)
3. Configure proper database credentials
4. Set up proper logging

## License

This project is licensed under the MIT License - see the LICENSE file for details.