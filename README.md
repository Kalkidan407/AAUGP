# AAUGP API

Backend API for the Addis Ababa University Graduation Project Platform.

## Stack

- Java 21
- Spring Boot
- Spring Security with JWT access tokens
- Rotating opaque refresh tokens
- PostgreSQL
- OpenAPI / Swagger UI

## Running Locally

```bash
export SPRING_DATASOURCE_URL="jdbc:postgresql://host:5432/database?sslmode=require"
export SPRING_DATASOURCE_USERNAME="postgres-user"
export SPRING_DATASOURCE_PASSWORD="postgres-password"
export JWT_SECRET="replace-with-a-long-random-secret"

./mvnw spring-boot:run
```

The API runs on `http://localhost:8080` by default.

## API Documentation

Swagger UI is available at:

```text
/swagger-ui.html
```

The OpenAPI document is available at:

```text
/v3/api-docs
```

Swagger uses the current API host, so it works correctly on Render and locally without changing an API base URL setting.

## Authentication

Register and login return a short-lived JWT access token and a long-lived refresh token:

```json
{
  "accessToken": "jwt-access-token",
  "refreshToken": "opaque-refresh-token",
  "tokenType": "Bearer",
  "expiresIn": 900
}
```

Use the access token for protected requests:

```http
Authorization: Bearer <accessToken>
```

Refresh tokens are opaque random tokens. The API stores only their SHA-256 hash, rotates them on every refresh, and revokes them on logout.

Auth endpoints:

- `POST /api/auth/register`
- `POST /api/auth/login`
- `POST /api/auth/refresh`
- `POST /api/auth/logout`

## Render Deployment

Set these environment variables on the Render API service:

```bash
SPRING_DATASOURCE_URL=jdbc:postgresql://host:5432/database?sslmode=require
SPRING_DATASOURCE_USERNAME=postgres-user
SPRING_DATASOURCE_PASSWORD=postgres-password
JWT_SECRET=replace-with-a-long-random-secret
```

Optional production settings:

```bash
CORS_ALLOWED_ORIGINS=https://your-frontend-domain.com
CORS_ALLOWED_ORIGIN_PATTERNS=https://*.onrender.com
JWT_ACCESS_TOKEN_EXPIRATION_SECONDS=900
JWT_REFRESH_TOKEN_EXPIRATION_SECONDS=2592000
```

`CORS_ALLOWED_ORIGIN_PATTERNS` defaults to `https://*.onrender.com`, which supports Render-hosted frontend previews. For a stable production frontend, prefer setting the exact frontend origin in `CORS_ALLOWED_ORIGINS`.

## TanStack / Bun Client

Point the frontend to the deployed API:

```bash
VITE_API_URL=https://your-api-name.onrender.com
```

Generate TypeScript types from the deployed OpenAPI document:

```bash
bunx openapi-typescript https://your-api-name.onrender.com/v3/api-docs -o src/lib/api/schema.ts
```

Install the typed fetch client:

```bash
bun add openapi-fetch
```

Example client:

```ts
import createClient from "openapi-fetch";
import type { paths } from "./schema";

export const api = createClient<paths>({
  baseUrl: import.meta.env.VITE_API_URL,
});
```

## Health Check

```text
GET /api/health
```
