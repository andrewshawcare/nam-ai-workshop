# Tasks

## Action → Operation (implement the behavior / endpoints / business rules)

### ID-OP-1 — Identity: Implement domain model & persistence for `User`

* **Work**: Define `User` model (id UUID, email, password\_hash, created\_at, updated\_at). Create repository interface (port) and a Postgres adapter implementation.
* **Acceptance**: DB schema/migration created; repository implements create/find\_by\_email/get\_by\_id; tests for repository behavior.
* **Deliverable**: Domain model + persistence adapter, DB migration scripts.

### ID-OP-2 — Identity: Implement register flow (`POST /register`)

* **Work**: API controller, input validation, password hashing (Argon2/Bcrypt), persist user, return 201 with safe representation (no hash).
* **Acceptance**: New user stored with hashed password; `POST /register` returns 201 + user id/email; unit tests for hashing and business rules; integration test hitting endpoint with DB (test container or mock).
* **Deliverable**: Endpoint + tests.

### ID-OP-3 — Identity: Implement login flow (`POST /login`) & JWT issuance

* **Work**: Validate credentials, call token provider port to sign JWT (RS256/HS256 support), return token and expiry.
* **Acceptance**: Successful login returns token and expiry; invalid credentials return 401; unit tests for token creation logic; integration test for the endpoint.
* **Deliverable**: Endpoint + token provider.

### ID-OP-4 — Identity: Implement authenticated `GET /me`

* **Work**: Implement middleware that validates token, extracts user id, and returns user metadata.
* **Acceptance**: `GET /me` with valid token returns 200 + user metadata; invalid token returns 401.

### TS-OP-1 — Task: Implement domain model & persistence for `Task`

* **Work**: Define `Task` model (id UUID,user\_id,title,description,due\_date,is\_completed,created\_at,updated\_at). Create repository port and Postgres adapter.
* **Acceptance**: DB schema/migration for Task; repository supports CRUD and listing by user\_id; unit tests for repository.
* **Deliverable**: Task model + migrations + persistence adapter.

### TS-OP-2 — Task: Implement JWT middleware & user extraction

* **Work**: Implement reusable JWT middleware that verifies token, enforces expiry, extracts `user_id` into request context.
* **Acceptance**: Middleware validated with valid/invalid/expired tokens; used by all Task endpoints; unit tests for middleware logic.

### TS-OP-3 — Task: Implement Create Task (`POST /tasks`)

* **Work**: Controller + use case: create task associated with current user (user\_id from token).
* **Acceptance**: Task created and stored with user\_id; response returns task id; integration test showing only authenticated user can create.

### TS-OP-4 — Task: Implement List Tasks (`GET /tasks`)

* **Work**: Return list of tasks for authenticated user (paging optional).
* **Acceptance**: Only tasks belonging to user returned; integration test confirms isolation.

### TS-OP-5 — Task: Implement Update Task (`PUT /tasks/{id}`)

* **Work**: Validate ownership; apply updates; prevent cross-user updates.
* **Acceptance**: Non-owner update returns 403; owner update persists changes; tests to cover ownership enforcement.

### TS-OP-6 — Task: Implement Delete Task (`DELETE /tasks/{id}`)

* **Work**: Validate ownership; delete row; soft delete optional (decide in scope).
* **Acceptance**: Only owner can delete; deleted task not returned in list.

### CROSS-OP-1 — Error handling, validation, and common HTTP semantics

* **Work**: Centralize error schema, input validation, consistent HTTP statuses, JSON error format.
* **Acceptance**: Endpoints return documented statuses & error payloads; integration tests assert error shapes.

---

## Action → Method (architecture & adapters)

### M-ARCH-1 — Create hexagonal skeleton for each service

* **Work**: Folder structure: `domain/`, `application/`, `adapters/http/`, `adapters/persistence/`, `infrastructure/`, `tests/`. Implement module boundaries and ports (interfaces).
* **Acceptance**: Each codebase compiles and runs; unit tests can target domain and application layers without HTTP or DB.

### M-PORT-1 — Define ports (interfaces)

* **Work**: For each external dependency: UserRepository, TaskRepository, TokenProvider, ConfigProvider, EmailClient? (if future). Document interfaces.
* **Acceptance**: Adapters implement these ports; unit tests can mock ports.

### M-ADAPTER-1 — Implement persistence adapters (Postgres)

* **Work**: SQL migrations; repository implementations using chosen ORM/SQL client; connection pooling & configurable connection string via env.
* **Acceptance**: Integration tests run against Postgres test container. DB must be pluggable by changing connection string.

### M-ADAPTER-2 — Implement token provider adapter(s)

* **Work**: Implement sign/verify functions for HS256 and RS256 (private key env / secret). Expose a TokenProvider port used by services.
* **Acceptance**: Unit tests verify token signature & claims; integration test verifying token issued by Identity can be verified by Task.

### M-INFRA-1 — Config & secrets via env

* **Work**: Implement config loader (from env and `.env` for local dev). Provide `.env.example`. Support configurable ports and DB connection.
* **Acceptance**: Services run with config from `.env` in local dev; secrets are not committed.

### M-RESIL-1 — Implement HTTP client helpers for external calls with retry & timeout

* **Work**: Generic HTTP client wrapper with configurable retries and timeouts (used anywhere external calls required).
* **Acceptance**: Backoff/retry logic covered by tests and config.

---

## Action → Communication (docs, API contracts, README)

### C-DOC-1 — Generate OpenAPI/Swagger spec for each service

* **Work**: Document all endpoints with request/response schemas, error responses, auth/security schemes for JWT.
* **Acceptance**: OpenAPI JSON/YAML generated; can be used to generate clients; included in repo.

### C-DOC-2 — Provide Postman collection (optional) and README

* **Work**: Postman collection or equivalent; README containing setup, run, test, docker-compose instructions.
* **Acceptance**: README covers local dev using docker-compose, env instructions, example API calls.

---

## Boundaries → Perspective / Integration / Scope

### B-PERS-1 — Document actors and responsibilities

* **Work**: Document who owns tokens, where auth checks happen (Task Service), and client responsibilities (store token, refresh if implemented).
* **Acceptance**: README/architecture doc lists actors and boundaries.

### B-INTEG-1 — Setup local orchestration (`docker-compose.yml`)

* **Work**: Compose file to bring up Identity, Task, Postgres, and any test dependencies for local development.
* **Acceptance**: `docker-compose up` boots both services and DB; health checks pass.

### B-INTEG-2 — Make DB pluggable

* **Work**: Use connection string pattern in config; document how to swap Postgres for other RDBMS.
* **Acceptance**: Switching DB requires only config changes and possibly migration adjustments.

### B-SCOPE-1 — Explicitly record out-of-scope items

* **Work**: Add to repo a “scope & exclusions” doc: refresh tokens, password resets, admin UIs, rate limiting (unless requested).
* **Acceptance**: Clear boundary doc in repo.

---

## Quality → Precision & Depth (security & testing)

### Q-SEC-1 — Implement password hashing using Argon2/Bcrypt

* **Work**: Select default (Argon2 recommended); parameters configurable via env; no plaintext storage.
* **Acceptance**: Unit tests assert hashed password verification; algorithms & params documented.

### Q-SEC-2 — Implement JWT best practices

* **Work**: Support RS256 (public/private key) or HS256 (HMAC secret); configurable expiry; safe defaults; minimal claims (sub=user\_id, iat, exp).
* **Acceptance**: Tokens have `exp`; token verification rejects expired tokens; keys are read from env.

### Q-TEST-1 — Unit tests: domain & authentication logic

* **Work**: Write focused tests for domain rules (user creation rules, password hashing, task ownership), token provider unit tests.
* **Acceptance**: Coverage for domain logic; all unit tests run locally and in CI.

### Q-TEST-2 — Integration tests: HTTP endpoints + JWT validation in Task Service

* **Work**: Integration tests that spin up service(s) (or use testcontainers), ensure Identity-issued token works against Task endpoints for ownership enforcement.
* **Acceptance**: CI profile runs integration tests with containers or mock infra; tests assert isolation and auth.

### Q-TEST-3 — Test data & mocks

* **Work**: Provide test factories, mocks for repositories and token provider for unit tests; use test containers for integration.
* **Acceptance**: Tests fast for unit, reliable for integration.

---

## Conditions → Time / State / Maturity (CI & deployment)

### D-DEP-1 — Dockerfile for each service

* **Work**: Multi-stage build, small runtime image, env-configurable startup, health checks.
* **Acceptance**: Image builds and runs; `docker run` accepts env variables and starts service.

### D-DEP-2 — `.env.example` and config docs

* **Work**: Provide `.env.example` showing required keys (DB, JWT keys, ports, hash params).
* **Acceptance**: New developer can run services locally with `docker-compose` after copying `.env.example`.

### D-DEP-3 — CI job to run unit & integration tests + build image

* **Work**: CI file (GitHub Actions / GitLab CI) that runs unit tests, then spins up test DB and runs integration tests, then builds Docker image artifacts.
* **Acceptance**: Passing pipeline on repo push; artifacts can be pushed to registry if desired.

### D-DEP-4 — Health checks and configurable ports

* **Work**: Implement `/health` or readiness endpoints; ports via env.
* **Acceptance**: Health endpoints return OK when DB connectivity and key access valid.

---

## Cross-cutting: deliverables and housekeeping tasks

### X-DEL-1 — Provide OpenAPI + Postman + README (see C-DOC-1 / C-DOC-2)

* **Acceptance**: Single source-of-truth OpenAPI files in each repo.

### X-HOUSE-1 — Folder structure & examples

* **Work**: Apply suggested folder structure in each repository (domain/, application/, adapters/, tests/, Dockerfile, .env.example, README).
* **Acceptance**: Repo layout matches suggested example.

### X-Security-1 — Secret handling policy

* **Work**: Add `SECURITY.md` or `README` section advising how to manage JWT private keys & secrets in production (e.g., secret manager).
* **Acceptance**: Documented policy in repo.

### X-OPS-1 — Observability basics

* **Work**: Add basic request logging, structured errors, and metrics stubs (e.g., request counts, auth failures).
* **Acceptance**: Logs contain request id, status codes; metrics endpoints defined (Prometheus compatible optional).

---

## Assumptions & open decisions (recorded as tasks if you want them implemented)

* **A1**: Refresh tokens — **not** in spec. If required, add tasks for refresh token flow + revocation store.
* **A2**: Password policy (min length, complexity) — unspecified; add requirement if needed.
* **A3**: Rate limiting & brute force protection for login — recommended but out of scope unless requested.
* **A4**: Token revocation / logout — not specified and requires design (blacklist vs short JWT + refresh tokens).

(These should be resolved before final production rollout; I included them so scope is explicit.)

---

# How I ensured MECE

* Tasks are grouped by decomposition categories from the guidance (Operation, Method, Communication, Boundaries, Quality, Conditions). Each task is either:

  * **service-specific** (Identity vs Task), or
  * **cross-cutting** (CI, Docker, docs), and I avoided splitting single responsibilities across multiple tasks (e.g., token provider is its own task distinct from middleware).
* The sum of tasks covers: domain models, persistence, controller/use cases, auth issuance & verification, middleware, tests, docs, containerization, orchestration, CI — which together satisfy all specification requirements without overlap.

---

# Next steps (practical)

If you want, I can:

1. Convert the MECE task list into a GitHub project with cards (one card per task, with acceptance criteria).
2. Generate starter skeleton code (one language of your choice) for the Identity Service with hexagonal layout, one sample endpoint, Dockerfile, `.env.example`, and OpenAPI stub.
3. Generate concrete OpenAPI YAML stubs for both services referencing the endpoints above.

Tell me which you prefer and which language / framework you want for the starter code (Node.js+TypeScript, Go, Python/Flask or FastAPI, Java/Kotlin, etc.), and I’ll generate the scaffolding and tests next. (No background waiting — I’ll produce it immediately when you pick.)

---

If you want to double-check any mapping I made from the spec to a specific decomposition category, say which item and I’ll point to the sentence in the spec and the guidance page that motivated the assignment. ([andrewshawcare.com][1])

[1]: https://andrewshawcare.com/requests/context/ "Andrew Shaw Care"
[2]: https://andrewshawcare.com/requests/context/boundaries.html "Andrew Shaw Care"
[3]: https://andrewshawcare.com/requests/decomposition/ "Andrew Shaw Care"
