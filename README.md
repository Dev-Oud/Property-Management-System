# Propex API

Spring Boot 3.4.5 / Java 17 backend using PostgreSQL, Flyway, Redis-backed request limits, JWT access tokens, rotating refresh tokens, Actuator, OpenAPI, and optional Sentry error reporting.

## Run locally with Docker

1. Copy `.env.example` to `.env`.
2. Set strong `DB_PASSWORD` and `REDIS_PASSWORD` values, and generate a new `JWT_SECRET` with `openssl rand -base64 32`.
3. Run `docker compose up --build` from this directory.
4. The API is available at `http://localhost:8081`; OpenAPI UI is at `http://localhost:8081/swagger-ui.html`.

For a domain with DNS pointed to the server, set `APP_DOMAIN` and run `docker compose --profile production up --build`; Caddy will terminate HTTPS and proxy requests to the app. On a server, remove the app's loopback port mapping from `docker-compose.yml` so only Caddy is reachable from outside the container network.

The app starts only when `JWT_SECRET` and `DB_PASSWORD` are set. Do not commit `.env`.

## Authentication

- `POST /api/auth/register` and `POST /api/auth/login` return a short-lived access token and a refresh token.
- Send the access token as `Authorization: Bearer <token>`.
- `POST /api/auth/refresh` accepts `{ "refreshToken": "..." }`. Each refresh token is single-use; a successful refresh returns a replacement pair.
- `POST /api/auth/logout` requires the current bearer access token and revokes that session immediately.

Only a SHA-256 hash of each refresh token is stored in PostgreSQL. Access-token revocation is checked against the session table on authenticated requests.

## Database migrations

Fresh databases receive the complete schema from `src/main/resources/db/migration`. Existing installations with tables but no Flyway history are baselined at version 1, then receive the indexes and auth-session table from later migrations. **Back up the existing database first** and check that its current table/column names match the entity mappings before deploying; Hibernate remains in `validate` mode.

For existing production data, set `SPRING_FLYWAY_BASELINE_ON_MIGRATE=false` only if your team has already created and verified a `flyway_schema_history` baseline. Do not run the initial migration manually against tables that already exist.

## Configuration

See `.env.example`. Production must set:

- `SPRING_DATASOURCE_URL`, `DB_USERNAME`, and `DB_PASSWORD`
- `JWT_SECRET` as a fresh Base64-encoded key of at least 32 random bytes
- `REDIS_HOST` / `REDIS_PORT` / `REDIS_PASSWORD`
- `APP_CORS_ALLOWED_ORIGINS` to the exact frontend origin(s)
- `SENTRY_DSN` to enable external error tracking

For a managed PostgreSQL instance, use the provider's TLS connection option in `SPRING_DATASOURCE_URL` (for PostgreSQL providers this is commonly an `sslmode=require` URL parameter).

The app expects a trusted reverse proxy to terminate HTTPS. Keep the app port private behind that proxy. The supplied Caddy config overwrites `X-Real-IP` with the connecting client address; only trust that header when requests can reach the app through this proxy. `deploy/Caddyfile` shows a Caddy TLS reverse-proxy configuration for a domain you control.

## Backups and monitoring

Actuator exposes health and metrics; only the health endpoint is public. Configure an external monitor to poll `/actuator/health` and alert on failures. To back up PostgreSQL, schedule `deploy/backup-postgres.sh` daily with `PGHOST`, `PGDATABASE`, `PGUSER`, `PGPASSWORD`, and a protected `BACKUP_DIR`; copy backups off the database host and periodically test restores. The script retains 14 days by default.

Set `SENTRY_DSN` from your Sentry project to send unexpected server exceptions to error tracking. The app does not contain a DSN.

## Secret exposure

The original uploaded Git history contained a JWT signing key. This returned source package omits `.git` and does not include that key, but it cannot remove the key from the public GitHub repository's history. Rotate the deployed key immediately. Before sharing/pushing a cleaned repository, coordinate a history rewrite with repository collaborators and force-push the rewritten history; invalidate all tokens signed with the old key.

## Tests

The context-load test uses an embedded H2 test profile and does not require a local PostgreSQL server. Tests were not run while preparing this archive.
