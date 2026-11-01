# Quick Reference Card

## Start Services

```bash
# Start Docker services (PostgreSQL + MockServer)
docker-compose up -d

# Verify services
docker-compose ps

# Run Spring Boot application
./gradlew bootRun
```

## Run Tests

```bash
# All tests
./gradlew karateTest

# Smoke tests
./gradlew karateTest -Dkarate.options="--tags @smoke"

# User tests
./gradlew karateTest -Dkarate.options="--tags @users"

# External API tests
./gradlew karateTest -Dkarate.options="--tags @external"

# View report
open build/karate-reports/karate-summary.html
```

## API Endpoints

### User API

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | /api/users | Create user |
| GET | /api/users/{id} | Get user by ID |
| GET | /api/users/username/{username} | Get user by username |
| GET | /api/users | Get all users |
| GET | /api/users?active=true | Get active users |
| GET | /api/users?search=term | Search users |
| PUT | /api/users/{id} | Update user |
| DELETE | /api/users/{id} | Delete user |
| PATCH | /api/users/{id}/deactivate | Deactivate user |

### External API Integration

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | /api/external-integration/users/{userId} | Get external user |
| POST | /api/external-integration/validate | Validate data |

## Docker Commands

```bash
# Start services
docker-compose up -d

# Stop services
docker-compose down

# View logs
docker-compose logs -f [postgres|mockapi]

# Connect to PostgreSQL
docker exec -it karate-postgres psql -U karate_user -d karatedb

# Restart service
docker-compose restart [postgres|mockapi]
```

## Database Commands

```sql
-- View all users
SELECT * FROM users;

-- Search users
SELECT * FROM users WHERE username LIKE '%john%';

-- Get active users
SELECT * FROM users WHERE active = true;

-- Check schema
\d users

-- View migrations
SELECT * FROM flyway_schema_history;
```

## Code Quality

```bash
# Format code
./gradlew spotlessApply

# Static analysis
./gradlew pmdMain pmdTest

# Security scan
./gradlew spotbugsMain spotbugsTest

# All quality checks
./gradlew codeQuality

# Pre-commit checks
./gradlew preCommitCheck
```

## Configuration Files

| File | Purpose |
|------|---------|
| `application.yml` | Main application config (PostgreSQL, JPA, External API) |
| `application-test.yml` | Test config (H2 in-memory) |
| `karate-config.js` | Karate environment configuration |
| `docker-compose.yml` | Docker services definition |

## Test Tags

| Tag | Description |
|-----|-------------|
| @smoke | Quick smoke tests |
| @users | User API tests |
| @external | External API tests |
| @create | Create operations |
| @read | Read operations |
| @update | Update operations |
| @delete | Delete operations |
| @validation | Validation tests |

## Environment Variables

```bash
# NVD API Key (for OWASP Dependency Check)
export NVD_API_KEY="your-key"

# Database connection (if needed)
export DB_URL="jdbc:postgresql://localhost:5432/karatedb"
export DB_USER="karate_user"
export DB_PASS="karate_pass"

# External API URL (if needed)
export EXTERNAL_API_URL="http://localhost:1080"
```

## Ports

| Service | Port |
|---------|------|
| Spring Boot App | 8080 |
| PostgreSQL | 5432 |
| MockServer | 1080 |

## File Structure

```
src/
├── main/
│   ├── java/.../
│   │   ├── config/          # RestClient config
│   │   ├── controller/      # REST controllers
│   │   ├── dto/             # Data transfer objects
│   │   ├── model/           # JPA entities
│   │   ├── repository/      # Spring Data repositories
│   │   └── service/         # Business logic
│   └── resources/
│       ├── application.yml
│       └── db/migration/    # Flyway migrations
└── test/
    └── resources/
        ├── application-test.yml
        ├── karate-config.js
        └── karate/features/
            ├── users/       # User API tests
            └── external/    # External API tests
```

## Common Tasks

### Create User
```bash
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "username": "johndoe",
    "email": "john@example.com",
    "firstName": "John",
    "lastName": "Doe"
  }'
```

### Search Users
```bash
curl "http://localhost:8080/api/users?search=john"
```

### Get Active Users
```bash
curl "http://localhost:8080/api/users?active=true"
```

### Test External API
```bash
curl http://localhost:8080/api/external-integration/users/123
```

### Reset Database
```bash
docker-compose down -v
docker-compose up -d
```

### Build & Test
```bash
./gradlew clean build karateTest
```

## Troubleshooting

**Docker not running:**
- Start Docker Desktop first

**Port 5432 already in use:**
- Change port in docker-compose.yml: `"5433:5432"`
- Update application.yml: `jdbc:postgresql://localhost:5433/karatedb`

**Port 8080 already in use:**
- Change in application.yml: `server.port: 8081`

**Tests failing:**
- Ensure Docker services are running: `docker-compose ps`
- Check application is running: `curl http://localhost:8080/actuator/health`
- Check MockServer: `curl http://localhost:1080/mockserver/status`

**Database connection error:**
```bash
# Check PostgreSQL logs
docker-compose logs postgres

# Restart PostgreSQL
docker-compose restart postgres

# Test connection
docker exec karate-postgres pg_isready -U karate_user -d karatedb
```

## Documentation

- [SETUP_COMPLETE.md](SETUP_COMPLETE.md) - Complete setup documentation
- [DOCKER_SETUP.md](DOCKER_SETUP.md) - Docker setup guide
- [README.md](README.md) - Project overview
- [KARATE_LEARNING_GUIDE.md](KARATE_LEARNING_GUIDE.md) - Karate tutorial
- [SECURITY.md](SECURITY.md) - Security documentation
