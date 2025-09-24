# Architecture Standards

Each service must be built using hexagonal architecture, structured as follows:

* Domain Layer: Business entities and logic
* Application Layer: Use case orchestration
* Ports (Interfaces): Abstractions for external dependencies (e.g., DB, HTTP)
* Adapters: Implementations of ports (e.g., REST controllers, repositories)
* Infrastructure Layer: Frameworks, libraries, and configurations

## Development Requirements

* Must support modular design
* JWT token signing/validation should follow industry standards (e.g., RS256/HS256)
* Passwords must never be stored in plain text
* Services must be documented with an OpenAPI/Swagger spec
* Use .env or equivalent for environment-specific config
* All external service calls should be retryable and timeout-controlled

## Testing Requirements

### Unit Tests

* Domain logic (business rules)
* Authentication logic

### Integration Tests

* HTTP endpoints
* JWT validation in Task Service

### Test Data

* Use mocks or test containers
* Must be automatable in CI pipelines

## Deployment Requirements

Each service must include:

* `Dockerfile`
* `.env.example` for environment variables
* Shared `docker-compose.yml` to orchestrate Identity and Task services for local development
* PostgreSQL or similar relational database can be used for persistence (must be pluggable)
* HTTP services must run on configurable ports

## Deliverables

Each microservice must provide:

* 📁 Clean folder structure (suggested structure for any language)
* 🔐 Secure auth flows and JWT handling
* 🧪 Unit & integration tests
* 🐳 Dockerfile
* ⚙️ .env.example
* 📄 API documentation via OpenAPI or Postman collection
* 🧰 README with setup and usage instructions

## Example Suggested Folder Structure

```
service-name/
├── domain/
│   ├── models/
│   └── services/
├── application/
│   └── use_cases/
├── adapters/
│   ├── http/
│   └── persistence/
├── tests/
│   ├── unit/
│   └── integration/
├── Dockerfile
├── .env.example
└── README.md
```