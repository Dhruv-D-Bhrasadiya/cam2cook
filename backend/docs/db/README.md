# Cam2Cook Database

The initial database is PostgreSQL 13, started by `docker-compose.yml` with a persistent named volume.

## Schema

The API creates the `users` table on startup. It contains:

- UUID primary key
- Display name and unique, indexed lowercase email
- Argon2 password hash
- Active flag
- Creation and update timestamps

The current startup initializer is intentionally small for the project stage. Once schema evolution begins, replace `create_all` with Alembic migrations.

## Run and inspect

From `.\cam2cook\backend\docker`:

```powershell
docker compose up --build
docker compose exec db psql -U user -d cam2cook
```

Useful SQL:

```sql
\dt
SELECT id, name, email, is_active, created_at FROM users;
```

Stop the services while retaining data with `docker compose down`. Remove the database volume, including all users, with `docker compose down -v`.

To run in cmd:

```cmd
docker exec -it <container_name_or_ID> psql -U user -d cam2cook -c "SELECT id, name, email, is_active, created_at FROM users;"
```

## Seed user

The app inserts one initial user if the configured email does not already exist. Override `SEED_USER_NAME`, `SEED_USER_EMAIL`, and `SEED_USER_PASSWORD` before the first startup. The defaults are suitable only for local development.
