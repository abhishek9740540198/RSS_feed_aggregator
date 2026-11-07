# RSS Server (Java / Spring Boot 3 port)

Converted from the original Go project (chi + sqlc + go-redis). Same REST API,
same behavior, rebuilt on:

- Java 21, Maven
- Spring Boot 3.3 (Spring Web, Spring Data JPA)
- PostgreSQL
- Redis (post-list caching)
- JJWT (JWT auth) + Spring Security Crypto (BCrypt)

## Structure

```
config/      Spring configuration (CORS, Redis, filter registration, app properties)
model/       JPA entities (User, Feed, FeedFollow, Post) + RSS XML model
dto/         Request/response bodies
repository/  Spring Data JPA repositories
service/     Password hashing, JWT, RSS fetching, background scraper
security/    API-key extraction, @AuthUser argument resolver
filter/       Request ID, security headers, rate limiting, API-key/JWT auth filters
controller/  REST controllers (one per original handler_*.go file)
exception/   Global exception handling -> uniform {"error": "..."} JSON
```

## Routes (unchanged from the Go version)

| Method | Path                        | Auth       |
|--------|-----------------------------|------------|
| GET    | /v1/health/live             | none       |
| GET    | /v1/health/ready            | none       |
| GET    | /v1/error                   | none       |
| POST   | /v1/users                   | none       |
| GET    | /v1/users                   | API key    |
| POST   | /v1/login                   | none       |
| POST   | /v1/feeds                   | API key    |
| GET    | /v1/feeds                   | none       |
| POST   | /v1/feedfollows             | API key    |
| GET    | /v1/feedfollows             | API key    |
| DELETE | /v1/feedfollows/{id}        | API key    |
| GET    | /v1/posts                   | JWT        |

API key auth: `Authorization: ApiKey <key>`
JWT auth: `Authorization: Bearer <token>`

## Running locally

```
cp .env.example .env      # then edit values
mvn spring-boot:run
```

Environment variables (same names as the Go project used, plus the datasource
credentials Spring needs): `PORT`, `DB_URL` (JDBC form, e.g.
`jdbc:postgresql://host:5432/dbname`), `SPRING_DATASOURCE_USERNAME`,
`SPRING_DATASOURCE_PASSWORD`, `JWT_SECRET`, `SCRAPE_INTERVAL` (e.g. `1m`,
`30s`), `MAX_CONCURRENCY`, `REDIS_ADDR` (`host:port`).

## Database schema

Spring Data JPA entities match the existing Postgres schema from
`sql/schema/*.sql` in the original project (users, feeds, feed_follows,
posts) — apply those same migrations (or point Flyway/Liquibase at them);
this port does not auto-generate DDL (`ddl-auto` left unset) so it won't
clobber an existing schema.

## Notes on intentional differences from the Go source

- The Go models had malformed `json` struct tags (an extra `"` in each tag),
  so `encoding/json` silently fell back to capitalized field names in
  responses. This port uses the clearly-intended snake_case names instead.
- The Go handlers returned the whole `User` struct including `PasswordHash`
  as JSON. This port excludes it via `@JsonIgnore` — a straightforward fix
  of what was a latent bug, not an intentional behavior change to preserve.
- Postgres's `encode(sha256(random()::text::bytea), 'hex')` default for
  `api_key` is replicated in `UserController` via `SecureRandom` + SHA-256.
