# Cam2Cook API

The API is a FastAPI service backed by PostgreSQL. It currently provides health checks and the first authentication slice.

## Run with Docker

From `.\cam2cook\backend\docker`:

```powershell
docker compose up --build
```

The service is available at `http://localhost:8000`. Interactive OpenAPI documentation is at `http://localhost:8000/docs`.

## Configuration

Compose supplies these environment variables. Set them in a `.env` file or in the shell before starting the stack:

| Variable             | Default                                               | Purpose                                                         |
| -------------------- | ----------------------------------------------------- | --------------------------------------------------------------- |
| `DATABASE_URL`       | `postgresql+asyncpg://user:password@db:5432/cam2cook` | Async PostgreSQL connection string                              |
| `JWT_SECRET`         | development placeholder                               | Secret used to sign access tokens; replace in real environments |
| `SEED_USER_NAME`     | `Cam2Cook Admin`                                      | Initial user's display name                                     |
| `SEED_USER_EMAIL`    | `admin@cam2cook.com`                                  | Initial user's login email                                      |
| `SEED_USER_PASSWORD` | `Admin123.`                                        | Initial user's password                                         |

The seed user is created on first startup only. Passwords are stored as Argon2 hashes and are never returned by the API.

## Routes

| Method | Path                    | Auth   | Description                                                          |
| ------ | ----------------------- | ------ | -------------------------------------------------------------------- |
| `GET`  | `/api/v1/health`        | No     | Verifies the API and database are available                          |
| `POST` | `/api/v1/auth/register` | No     | Creates a user from JSON `name`, `email`, and `password`             |
| `POST` | `/api/v1/auth/login`    | No     | Accepts form fields `username` (email) and `password`; returns a JWT |
| `GET`  | `/api/v1/auth/me`       | Bearer | Returns the authenticated user                                       |
| `POST` | `/api/v1/auth/logout`   | No     | Returns `204`; clients discard the stateless bearer token            |

Example login:

```powershell
Invoke-RestMethod -Method Post -Uri http://localhost:8000/api/v1/auth/login -ContentType 'application/x-www-form-urlencoded' -Body @{ username = 'admin@cam2cook.com'; password = 'ChangeMe123!' }
```

For production, use a strong `JWT_SECRET`, rotate the seeded password immediately, terminate TLS at the edge, and add token revocation or short-lived refresh-token support before exposing logout as a security boundary.
