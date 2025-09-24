# Ordered Task List

### 1. Project scaffolding & architecture

1. **M-ARCH-1** — Create hexagonal skeleton for each service (folder structure, clean separation).
2. **X-HOUSE-1** — Apply suggested folder structure in each repository.
3. **M-PORT-1** — Define ports (interfaces: repositories, token provider, config).
4. **M-INFRA-1** — Config & secrets via env loader + `.env.example`.

---

### 2. Domain models & persistence

5. **ID-OP-1** — Implement domain model & persistence for `User`.
6. **TS-OP-1** — Implement domain model & persistence for `Task`.
7. **M-ADAPTER-1** — Implement persistence adapters (Postgres + migrations).
8. **B-INTEG-2** — Make DB pluggable (config-driven).

---

### 3. Security foundation

9. **Q-SEC-1** — Implement password hashing using Argon2/Bcrypt.
10. **M-ADAPTER-2** — Implement token provider adapter(s) (RS256/HS256).
11. **Q-SEC-2** — Implement JWT best practices (claims, expiry, validation).

---

### 4. Identity Service functionality

12. **ID-OP-2** — Implement register flow (`POST /register`).
13. **ID-OP-3** — Implement login flow (`POST /login`) & JWT issuance.
14. **ID-OP-4** — Implement authenticated `GET /me`.

---

### 5. Task Service functionality

15. **TS-OP-2** — Implement JWT middleware & user extraction.
16. **TS-OP-3** — Implement Create Task (`POST /tasks`).
17. **TS-OP-4** — Implement List Tasks (`GET /tasks`).
18. **TS-OP-5** — Implement Update Task (`PUT /tasks/{id}`).
19. **TS-OP-6** — Implement Delete Task (`DELETE /tasks/{id}`).

---

### 6. Cross-cutting application logic

20. **CROSS-OP-1** — Centralized error handling & validation.
21. **M-RESIL-1** — HTTP client helpers with retry & timeout (for any future external calls).
22. **X-OPS-1** — Observability basics (logging, metrics, error tracing).

---

### 7. Testing

23. **Q-TEST-1** — Unit tests: domain & authentication logic.
24. **Q-TEST-3** — Test data & mocks (factories, stubs, test containers).
25. **Q-TEST-2** — Integration tests: endpoints + JWT validation across services.

---

### 8. Documentation

26. **C-DOC-1** — Generate OpenAPI/Swagger spec for both services.
27. **C-DOC-2** — Postman collection & README (setup, usage, docker-compose).
28. **B-PERS-1** — Document actors & responsibilities.
29. **B-SCOPE-1** — Document out-of-scope items explicitly.
30. **X-Security-1** — Security/secrets handling policy in repo.

---

### 9. Deployment & orchestration

31. **D-DEP-1** — Write Dockerfile for each service.
32. **D-DEP-2** — Finalize `.env.example` & config docs.
33. **B-INTEG-1** — Setup shared `docker-compose.yml` (Identity + Task + DB).
34. **D-DEP-4** — Add health checks & configurable ports.

---

### 10. CI/CD automation

35. **D-DEP-3** — CI job: run unit + integration tests, then build/push Docker images.
36. **X-DEL-1** — Final deliverables: OpenAPI, Postman, README, all artifacts ready.

---

✅ This order ensures:

* **Foundations first** (arch, env, domain, persistence).
* **Security primitives before endpoints** (hashing, JWT).
* **Identity before Task** (since Task depends on JWTs).
* **Testing & docs after functionality is in place**.
* **Deployment & CI last**, once services are working and tested.