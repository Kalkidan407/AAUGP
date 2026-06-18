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
export API_BASE_URL="http://localhost:8080"
```

## Render Deployment

Set these Render environment variables for the API service:

```bash
SPRING_DATASOURCE_URL=jdbc:postgresql://host:5432/database?sslmode=require
SPRING_DATASOURCE_USERNAME=your-postgres-user
SPRING_DATASOURCE_PASSWORD=your-postgres-password
API_BASE_URL=https://your-api-name.onrender.com
CORS_ALLOWED_ORIGINS=https://your-tanstack-app-domain.com,http://localhost:3000,http://localhost:5173
```

Use your real Render API URL for `API_BASE_URL`. Use your real TanStack frontend URL for `CORS_ALLOWED_ORIGINS`; otherwise browser requests will be blocked by CORS.

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

Public endpoints:

- `POST /api/auth/register`
- `POST /api/auth/login`
- `GET /api/health`

All other endpoints require `Authorization: Bearer <token>`.
