# Backend

Design two cleanly separated microservices:

* Identity Service: Handles user authentication and issues JWT tokens.
* Task Service: Provides task management features, protected by JWT-based authentication.

Each service must:

* Be independently deployable.
* Follow hexagonal architecture (ports and adapters).
* Include unit and integration tests.
* Be containerized (Docker).

## Services Overview

### Identity Service

#### Purpose

Manage users and authenticate via JSON Web Tokens (JWT).

#### Functional Requirements

* Register User: Accepts email & password, stores securely.
* Login: Validates credentials and returns signed JWT.
* Get Authenticated User Info: Verifies JWT and returns user metadata.

#### Endpoints

| Method | Path | Description |
| - | - | - |
| POST | /register | Register a new user |
| POST | /login | Authenticate and return JWT |
| GET | /me | Return current authenticated user |

#### Security

* Passwords must be securely hashed (e.g., using Argon2, Bcrypt).
* JWT must be signed and have configurable expiration.
* Secrets/keys must be injected via environment/config file.

#### User Data Model

```json
{
    "$schema": "http://json-schema.org/draft-07/schema#",
    "title": "User",
    "type": "object",
    "required": ["id", "email", "password_hash", "created_at", "updated_at"],
    "properties": {
        "id": {
            "type": "string",
            "format": "uuid"
        },
        "email": {
            "type": "string",
            "format": "email"
        },
        "password_hash": {
            "type": "string",
            "minLength": 1
        },
        "created_at": {
            "type": "string",
            "format": "date-time"
        },
        "updated_at": {
            "type": "string",
            "format": "date-time"
        }
    }
}
```

### Task Service

#### Purpose

Authenticated users can manage their personal tasks.

#### Functional Requirements

* Create Task: Add a task for the current user.
* Get Tasks: Fetch all tasks for the user.
* Update Task: Modify existing task.
* Delete Task: Remove existing task.

#### Endpoints

| Method | Path | Description |
| - | - | - |
| POST | /tasks | Create a new task |
| GET | /tasks | List all tasks for user |
| PUT | /tasks/{id} | Update an existing task |
| DELETE | /tasks/{id} | Delete an existing task |

#### Security

* JWT validation middleware to extract and verify user ID.
* Multi-tenancy: ensure tasks are owned by authenticated users.
* Reject unauthorized/expired token requests.

#### Task Data Model

```json
{
    "$schema": "http://json-schema.org/draft-07/schema#",
    "title": "Task",
    "type": "object",
    "required": ["id", "user_id", "title", "is_completed", "created_at", "updated_at"],
    "properties": {
        "id": {
            "type": "string",
            "format": "uuid"
        },
        "user_id": {
            "type": "string",
            "format": "uuid"
        },
        "title": {
            "type": "string",
            "minLength": 1
        },
        "description": {
            "type": "string"
        },
        "due_date": {
            "type": "string",
            "format": "date-time"
        },
        "is_completed": {
            "type": "boolean"
        },
        "created_at": {
            "type": "string",
            "format": "date-time"
        },
        "updated_at": {
            "type": "string",
            "format": "date-time"
        }
    }
}
```
