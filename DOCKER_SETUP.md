# Docker Setup Guide

This guide explains how to run the Karate framework with PostgreSQL 17 and Mock External API using Docker Compose.

## Prerequisites

- Docker 20.10+
- Docker Compose 2.0+

## Services

The `docker-compose.yml` defines the following services:

### 1. PostgreSQL 17
- **Container**: `karate-postgres`
- **Port**: `5432:5432`
- **Database**: `karatedb`
- **Username**: `karate_user`
- **Password**: `karate_pass`
- **Health Check**: Enabled

### 2. MockServer (External API)
- **Container**: `karate-mockapi`
- **Port**: `1080:1080`
- **Purpose**: Simulates external API endpoints for testing

## Quick Start

### 1. Start All Services

```bash
docker-compose up -d
```

### 2. Verify Services are Running

```bash
docker-compose ps
```

Expected output:
```
NAME                  IMAGE                      STATUS          PORTS
karate-postgres       postgres:17-alpine         Up (healthy)    0.0.0.0:5432->5432/tcp
karate-mockapi        mockserver/mockserver      Up              0.0.0.0:1080->1080/tcp
```

### 3. Check Service Health

**PostgreSQL:**
```bash
docker exec karate-postgres pg_isready -U karate_user -d karatedb
```

**MockServer:**
```bash
curl http://localhost:1080/mockserver/status
```

### 4. Run the Application

```bash
./gradlew bootRun
```

The application will connect to PostgreSQL and be ready to handle requests.

### 5. Run Karate Tests

```bash
# All tests
./gradlew karateTest

# Smoke tests only
./gradlew karateTest -Dkarate.options="--tags @smoke"

# User tests only
./gradlew karateTest -Dkarate.options="--tags @users"

# External API tests only
./gradlew karateTest -Dkarate.options="--tags @external"
```

## Database Management

### Connect to PostgreSQL

```bash
docker exec -it karate-postgres psql -U karate_user -d karatedb
```

### View Database Logs

```bash
docker-compose logs -f postgres
```

### Reset Database

```bash
docker-compose down -v
docker-compose up -d
```

This will delete all data and recreate the database with initial schema.

### Manual Migration

Flyway will automatically run migrations on application startup. Migrations are in:
```
src/main/resources/db/migration/
```

### Check Migration Status

```bash
./gradlew flywayInfo
```

## MockServer Configuration

### Mock API Endpoints

The MockServer is configured with the following endpoints (see `docker/mockserver/initializerJson.json`):

**1. Get External User**
- **Method**: GET
- **Path**: `/api/external/users/{id}`
- **Response**:
  ```json
  {
    "id": "ext-123",
    "name": "External User",
    "email": "external@example.com",
    "source": "external-api"
  }
  ```

**2. Validate Data**
- **Method**: POST
- **Path**: `/api/external/validate`
- **Request**:
  ```json
  {
    "data": "test-data"
  }
  ```
- **Response**:
  ```json
  {
    "valid": true,
    "message": "Validation successful"
  }
  ```

### View MockServer Logs

```bash
docker-compose logs -f mockapi
```

### View MockServer Expectations

```bash
curl http://localhost:1080/mockserver/retrieve?type=EXPECTATIONS
```

### Add New Mock Endpoints

Edit `docker/mockserver/initializerJson.json` and restart:

```bash
docker-compose restart mockapi
```

## Troubleshooting

### PostgreSQL Connection Issues

**Problem**: Application can't connect to PostgreSQL

**Solutions**:
1. Check if PostgreSQL is running:
   ```bash
   docker-compose ps postgres
   ```

2. Check PostgreSQL logs:
   ```bash
   docker-compose logs postgres
   ```

3. Verify connection:
   ```bash
   docker exec karate-postgres pg_isready -U karate_user -d karatedb
   ```

4. Restart PostgreSQL:
   ```bash
   docker-compose restart postgres
   ```

### MockServer Not Responding

**Problem**: External API tests failing

**Solutions**:
1. Check MockServer status:
   ```bash
   curl http://localhost:1080/mockserver/status
   ```

2. Check MockServer logs:
   ```bash
   docker-compose logs mockapi
   ```

3. Restart MockServer:
   ```bash
   docker-compose restart mockapi
   ```

### Port Conflicts

**Problem**: Port 5432 or 1080 already in use

**Solution**: Change ports in `docker-compose.yml`:

```yaml
services:
  postgres:
    ports:
      - "5433:5432"  # Use 5433 instead

  mockapi:
    ports:
      - "1081:1080"  # Use 1081 instead
```

Don't forget to update `application.yml`:
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5433/karatedb

external:
  api:
    base-url: http://localhost:1081
```

### Clean Start

If you encounter persistent issues, perform a clean restart:

```bash
# Stop all services
docker-compose down

# Remove volumes (deletes all data)
docker-compose down -v

# Remove images (forces re-download)
docker-compose down --rmi all

# Start fresh
docker-compose up -d
```

## Docker Commands Reference

```bash
# Start services
docker-compose up -d

# Stop services
docker-compose down

# Stop and remove volumes
docker-compose down -v

# View logs
docker-compose logs -f [service-name]

# Restart specific service
docker-compose restart [service-name]

# Execute command in container
docker exec -it [container-name] [command]

# View resource usage
docker stats

# Remove unused volumes
docker volume prune
```

## Production Considerations

For production deployment:

1. **Use environment variables** for sensitive data:
   ```yaml
   environment:
     POSTGRES_PASSWORD: ${DB_PASSWORD}
   ```

2. **Enable persistence**:
   ```yaml
   volumes:
     - ./postgres-data:/var/lib/postgresql/data
   ```

3. **Configure backups**:
   ```bash
   docker exec karate-postgres pg_dump -U karate_user karatedb > backup.sql
   ```

4. **Use production-grade images**:
   - Consider using official PostgreSQL image (not alpine) for production
   - Configure connection pooling
   - Set resource limits

5. **Security**:
   - Change default passwords
   - Use secrets management
   - Enable SSL/TLS
   - Restrict network access

## Next Steps

1. Review the API endpoints: `src/main/java/com/example/karateframework/controller/`
2. Explore Karate tests: `src/test/resources/karate/features/`
3. Check application configuration: `src/main/resources/application.yml`
4. Run tests and view reports: `build/karate-reports/karate-summary.html`
