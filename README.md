# ShowTalk Backend

ShowTalk is a platform where viewers can discuss TV programs, like comments, and participate in live polls. This repository contains the Spring Boot backend.

## Local database

Use Java 21 and Docker. Create a `.env` file in the project root with your local PostgreSQL credentials:

```dotenv
POSTGRES_USER=showtalk_user
POSTGRES_PASSWORD='replace-with-your-local-password'
```

Start PostgreSQL 16 and check its status:

```bash
docker compose up -d
docker compose ps
```

The `local` Spring profile connects to `jdbc:postgresql://localhost:5432/showtalk`. When running Spring from your terminal or IDE, activate the `local` profile and supply the same `POSTGRES_USER` and `POSTGRES_PASSWORD` values to the application process. Keep `.env` out of Git.
