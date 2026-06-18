# AAUGP API

Spring Boot API for the Addis Ababa University Graduation Project Platform.

## Local API

```bash
./mvnw spring-boot:run
```

The API runs on `http://localhost:8080` by default.

Useful integration URLs:

- Health check: `GET http://localhost:8080/api/health`
- OpenAPI JSON: `GET http://localhost:8080/v3/api-docs`
- Swagger UI: `http://localhost:8080/swagger-ui.html`

## Environment

Required:

```bash
export SPRING_DATASOURCE_USERNAME="your-postgres-user"
export SPRING_DATASOURCE_PASSWORD="your-postgres-password"
```

Optional:

```bash
export PORT=8080
export SPRING_DATASOURCE_URL="jdbc:postgresql://host:5432/database?sslmode=require"
export CORS_ALLOWED_ORIGINS="http://localhost:3000,http://localhost:5173,http://localhost:4173"
export CORS_ALLOWED_ORIGIN_PATTERNS=""
export API_BASE_URL="http://localhost:8080"
export JWT_SECRET="replace-with-a-long-random-secret"
export JWT_ACCESS_TOKEN_EXPIRATION_SECONDS=900
export JWT_REFRESH_TOKEN_EXPIRATION_SECONDS=2592000
```

## Render Deployment

Set these Render environment variables for the API service:

```bash
SPRING_DATASOURCE_URL=jdbc:postgresql://host:5432/database?sslmode=require
SPRING_DATASOURCE_USERNAME=your-postgres-user
SPRING_DATASOURCE_PASSWORD=your-postgres-password
API_BASE_URL=https://your-api-name.onrender.com
CORS_ALLOWED_ORIGINS=https://your-tanstack-app-domain.com,http://localhost:3000,http://localhost:5173
CORS_ALLOWED_ORIGIN_PATTERNS=https://*.onrender.com
JWT_SECRET=replace-with-a-long-random-secret
JWT_ACCESS_TOKEN_EXPIRATION_SECONDS=900
JWT_REFRESH_TOKEN_EXPIRATION_SECONDS=2592000
```

Use your real Render API URL for `API_BASE_URL`. Use your real TanStack frontend URL for `CORS_ALLOWED_ORIGINS`; otherwise browser requests will be blocked by CORS.

If your frontend Render URL changes between previews, use `CORS_ALLOWED_ORIGIN_PATTERNS=https://*.onrender.com`. Keep exact production origins in `CORS_ALLOWED_ORIGINS` when possible.

Quick deployed API checks:

```bash
curl https://your-api-name.onrender.com/api/health
curl -i -X OPTIONS https://your-api-name.onrender.com/api/auth/register \
  -H "Origin: https://your-tanstack-app-domain.com" \
  -H "Access-Control-Request-Method: POST" \
  -H "Access-Control-Request-Headers: content-type"
```

## TanStack + Bun Integration

In your TanStack app, set the API base URL:

```bash
echo 'VITE_API_URL=https://your-api-name.onrender.com' > .env.local
```

Generate a TypeScript client from the API contract:

```bash
bunx openapi-typescript https://your-api-name.onrender.com/v3/api-docs -o src/lib/api/schema.ts
```

Install a small fetch client:

```bash
bun add openapi-fetch
```

Example TanStack-side client:

```ts
import createClient from "openapi-fetch";
import type { paths } from "./schema";

export const api = createClient<paths>({
  baseUrl: import.meta.env.VITE_API_URL,
});
```

After login or registration, send the returned JWT as a bearer token:

```ts
await api.GET("/api/project", {
  headers: {
    Authorization: `Bearer ${token}`,
  },
});
```

Login and registration return both tokens:

```json
{
  "accessToken": "jwt-access-token",
  "refreshToken": "opaque-refresh-token",
  "tokenType": "Bearer",
  "expiresIn": 900
}
```

Use `accessToken` for API requests. When it expires, rotate the refresh token:

```ts
const result = await api.POST("/api/auth/refresh", {
  body: {
    refreshToken,
  },
});
```

Persist the new `accessToken` and new `refreshToken` from that response. To sign out, revoke the current refresh token:

```ts
await api.POST("/api/auth/logout", {
  body: {
    refreshToken,
  },
});
```

Public endpoints:

- `POST /api/auth/register`
- `POST /api/auth/login`
- `POST /api/auth/refresh`
- `POST /api/auth/logout`
- `GET /api/health`

All other endpoints require `Authorization: Bearer <token>`.
