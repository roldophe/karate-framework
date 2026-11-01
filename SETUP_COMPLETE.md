# Karate Framework Setup - Complete ✅

## What Has Been Implemented

### 1. Database Layer (PostgreSQL 17)

**Files Created:**
- `docker-compose.yml` - PostgreSQL 17 Alpine container configuration
- `docker/init-db.sql` - Database initialization with sample data
- `src/main/resources/db/migration/V1__Initial_schema.sql` - Flyway migration
- `src/test/resources/application-test.yml` - H2 in-memory database for tests

**Features:**
- UUID primary keys
- Timestamp tracking (created_at, updated_at)
- Indexes on username, email, active columns
- Health checks
- Volume persistence

### 2. JPA Entity & Repository Layer

**Files Created/Modified:**
- `src/main/java/com/example/karateframework/model/User.java` - JPA Entity with validation
  - Bean Validation (@NotBlank, @Email, @Size)
  - Hibernate timestamps (@CreationTimestamp, @UpdateTimestamp)
  - UUID type for ID

- `src/main/java/com/example/karateframework/repository/UserRepository.java`
  - JPQL queries for searching and filtering
  - Custom query methods
  - Existence checks

**Key Features:**
- Full JPA annotations
- Bean validation on entity level
- JPQL custom queries
- Database constraints enforced at entity level

### 3. Service Layer

**Files Created:**
- `src/main/java/com/example/karateframework/service/UserService.java`
  - Business logic for CRUD operations
  - Validation for unique username/email
  - Search functionality
  - Soft delete (deactivation)
  - Transaction management (@Transactional)

- `src/main/java/com/example/karateframework/service/ExternalApiService.java`
  - RestClient integration for external API
  - Error handling with custom exceptions
  - Logging
  - Response mapping to DTOs

**Features:**
- Transaction boundaries
- Read-only optimizations
- Comprehensive error handling
- Business validation logic

### 4. REST API Controllers

**Files Modified/Created:**
- `src/main/java/com/example/karateframework/controller/UserController.java` - Completely rewritten
  - POST /api/users - Create user
  - GET /api/users/{id} - Get by ID
  - GET /api/users/username/{username} - Get by username
  - GET /api/users?active=true - Get active users
  - GET /api/users?search=term - Search users
  - PUT /api/users/{id} - Update user
  - DELETE /api/users/{id} - Delete user
  - PATCH /api/users/{id}/deactivate - Deactivate user

- `src/main/java/com/example/karateframework/controller/ExternalApiController.java`
  - GET /api/external-integration/users/{userId} - Fetch external user
  - POST /api/external-integration/validate - Validate data

**Features:**
- RESTful endpoints
- Proper HTTP status codes
- Query parameters for filtering
- UUID path variables
- Validation with @Valid

### 5. External API Integration (RestClient)

**Files Created:**
- `src/main/java/com/example/karateframework/config/RestClientConfig.java`
  - Spring Boot 4.x RestClient configuration
  - Base URL configuration
  - Default headers
  - Bean definitions

- DTOs:
  - `src/main/java/com/example/karateframework/dto/ExternalUserResponse.java`
  - `src/main/java/com/example/karateframework/dto/ValidationRequest.java`
  - `src/main/java/com/example/karateframework/dto/ValidationResponse.java`

**Features:**
- Modern RestClient (Spring Boot 4.x)
- Error handling with onStatus
- Request/Response mapping
- Configurable timeout and retries

### 6. Mock External API (MockServer)

**Files Created:**
- `docker/mockserver/initializerJson.json` - MockServer expectations
  - GET /api/external/users/* - Returns mock user
  - POST /api/external/validate - Returns validation response

**Features:**
- Pre-configured mock responses
- Docker container (mockserver/mockserver:latest)
- Port 1080 exposed
- JSON-based expectations

### 7. Configuration

**Files Modified:**
- `src/main/resources/application.yml`
  - PostgreSQL datasource configuration
  - HikariCP connection pooling
  - JPA/Hibernate settings
  - Flyway migration settings
  - External API base URL
  - Logging configuration

- `src/test/resources/karate-config.js`
  - Added externalApiUrl configuration
  - Added dbUrl configuration
  - Environment-specific settings

**Features:**
- Environment-based configuration
- Connection pooling (HikariCP)
- SQL logging for debugging
- Flyway baseline-on-migrate

### 8. Gradle Dependencies

**Added to `build.gradle.kts`:**
- `spring-boot-starter-data-jpa` - JPA support
- `spring-boot-starter-validation` - Bean validation
- `postgresql` - PostgreSQL driver
- `flyway-core` - Database migrations
- `flyway-database-postgresql` - PostgreSQL support for Flyway
- `spring-boot-starter-webflux` - WebClient/RestClient support
- `h2` - In-memory database for tests

### 9. Comprehensive Karate Tests

**Files Created:**

**User API Tests** (`src/test/resources/karate/features/users/user-crud-db.feature`):
- ✅ Create user (POST)
- ✅ Get user by ID (GET)
- ✅ Get user by username (GET)
- ✅ Get all users (GET)
- ✅ Search users (GET with query params)
- ✅ Update user (PUT)
- ✅ Delete user (DELETE)
- ✅ Deactivate user (PATCH)
- ✅ Validation tests (username too short, invalid email, duplicate username)
- ✅ Error handling (404 for non-existent users)

**External API Tests** (`src/test/resources/karate/features/external/external-api-integration.feature`):
- ✅ Get external user via integration endpoint
- ✅ Validate data via external API
- ✅ Direct calls to MockServer
- ✅ Error handling tests

**Features:**
- 13 comprehensive test scenarios
- All CRUD operations covered
- Validation testing
- Search and filter testing
- External API integration testing
- UUID handling in tests
- Dynamic test data generation
- Proper tagging (@smoke, @users, @external, etc.)

### 10. Documentation

**Files Created:**
- `DOCKER_SETUP.md` - Complete Docker setup guide
  - Quick start instructions
  - Service descriptions
  - Database management
  - MockServer configuration
  - Troubleshooting guide
  - Production considerations

## Project Structure

```
karate-framework/
├── docker/
│   ├── init-db.sql                          # PostgreSQL initialization
│   └── mockserver/
│       └── initializerJson.json             # MockServer expectations
├── src/
│   ├── main/
│   │   ├── java/com/example/karateframework/
│   │   │   ├── config/
│   │   │   │   └── RestClientConfig.java    # RestClient configuration
│   │   │   ├── controller/
│   │   │   │   ├── UserController.java      # User REST API
│   │   │   │   └── ExternalApiController.java # External API integration
│   │   │   ├── dto/
│   │   │   │   ├── ExternalUserResponse.java
│   │   │   │   ├── ValidationRequest.java
│   │   │   │   └── ValidationResponse.java
│   │   │   ├── model/
│   │   │   │   └── User.java                # JPA Entity
│   │   │   ├── repository/
│   │   │   │   └── UserRepository.java      # Spring Data JPA
│   │   │   └── service/
│   │   │       ├── UserService.java         # User business logic
│   │   │       └── ExternalApiService.java  # External API client
│   │   └── resources/
│   │       ├── application.yml              # Main configuration
│   │       └── db/migration/
│   │           └── V1__Initial_schema.sql   # Flyway migration
│   └── test/
│       └── resources/
│           ├── application-test.yml         # Test configuration (H2)
│           ├── karate-config.js             # Updated Karate config
│           └── karate/features/
│               ├── users/
│               │   └── user-crud-db.feature # User API tests
│               └── external/
│                   └── external-api-integration.feature
├── docker-compose.yml                       # Docker services
├── DOCKER_SETUP.md                          # Docker documentation
└── SETUP_COMPLETE.md                        # This file
```

## Technology Stack

| Component | Technology | Version |
|-----------|-----------|---------|
| Java | OpenJDK | 21 |
| Spring Boot | Spring Boot | 4.1.2 |
| Database | PostgreSQL | 17 Alpine |
| ORM | Hibernate/JPA | (via Spring Boot) |
| Migration | Flyway | 10.x |
| Testing | Karate | 2.1.4 |
| Mock Server | MockServer | Latest |
| Build Tool | Gradle | 9.8.2+ |

## Getting Started

### Prerequisites

1. **Java 21** - Verify with `java -version`
2. **Docker** - Verify with `docker --version`
3. **Docker Compose** - Verify with `docker-compose --version`

### Step 1: Start Docker Services

```bash
# Start PostgreSQL and MockServer
docker-compose up -d

# Verify services are running
docker-compose ps

# Check PostgreSQL health
docker exec karate-postgres pg_isready -U karate_user -d karatedb
```

### Step 2: Run the Application

```bash
# Build the project
./gradlew build

# Run the Spring Boot application
./gradlew bootRun
```

The application will:
- Connect to PostgreSQL on localhost:5432
- Run Flyway migrations automatically
- Start on port 8080
- Connect to MockServer on localhost:1080

### Step 3: Run Karate Tests

```bash
# Run all tests
./gradlew karateTest

# Run smoke tests only
./gradlew karateTest -Dkarate.options="--tags @smoke"

# Run user tests only
./gradlew karateTest -Dkarate.options="--tags @users"

# Run external API tests only
./gradlew karateTest -Dkarate.options="--tags @external"

# View test report
open build/karate-reports/karate-summary.html
```

### Step 4: Test the API Manually

**Create a user:**
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

**Get all users:**
```bash
curl http://localhost:8080/api/users
```

**Search users:**
```bash
curl http://localhost:8080/api/users?search=john
```

**Get active users:**
```bash
curl http://localhost:8080/api/users?active=true
```

**Test external API integration:**
```bash
curl http://localhost:8080/api/external-integration/users/123
```

**Validate data:**
```bash
curl -X POST http://localhost:8080/api/external-integration/validate \
  -H "Content-Type: application/json" \
  -d '{"data": "test"}'
```

## Database Access

**Connect to PostgreSQL:**
```bash
docker exec -it karate-postgres psql -U karate_user -d karatedb
```

**Useful SQL commands:**
```sql
-- View all users
SELECT * FROM users;

-- Check schema
\d users

-- View migrations
SELECT * FROM flyway_schema_history;
```

## Code Quality

**Format code:**
```bash
./gradlew spotlessApply
```

**Run static analysis:**
```bash
./gradlew pmdMain pmdTest
```

**Security scan:**
```bash
./gradlew spotbugsMain spotbugsTest
```

**All quality checks:**
```bash
./gradlew codeQuality
```

**Pre-commit checks:**
```bash
./gradlew preCommitCheck
```

## What's Different from Before

### Before (In-Memory)
- HashMap storage in controller
- No database persistence
- String IDs
- No validation
- No timestamps
- Simple CRUD only

### After (Database + Business Logic)
- PostgreSQL 17 with Docker
- JPA/Hibernate ORM
- UUID IDs
- Bean validation
- Automatic timestamps
- JPQL queries
- Search functionality
- Soft delete (deactivation)
- External API integration
- RestClient (Spring Boot 4.x)
- MockServer for testing
- Comprehensive Karate tests
- Transaction management
- Error handling
- Logging

## Key Features Implemented

### Business Logic
✅ Unique username/email validation
✅ Search users by username or email
✅ Filter active/inactive users
✅ Soft delete (deactivate users)
✅ Audit timestamps (created_at, updated_at)
✅ Transaction management

### External API Integration
✅ RestClient configuration
✅ Error handling for external calls
✅ DTO mapping
✅ MockServer for testing
✅ Integration endpoints

### Testing
✅ 13 Karate test scenarios
✅ CRUD operation tests
✅ Validation tests
✅ Search and filter tests
✅ External API integration tests
✅ Error handling tests
✅ UUID support in tests
✅ Dynamic test data

### Database
✅ PostgreSQL 17
✅ Flyway migrations
✅ UUID primary keys
✅ Indexes for performance
✅ Docker containerization
✅ Health checks
✅ Sample data initialization

## Next Steps

1. **Start Docker**: `docker-compose up -d`
2. **Run Application**: `./gradlew bootRun`
3. **Run Tests**: `./gradlew karateTest`
4. **View Reports**: `open build/karate-reports/karate-summary.html`
5. **Explore API**: Use curl commands above or Postman

## Troubleshooting

**Docker not running:**
```bash
# Start Docker Desktop
# Then: docker-compose up -d
```

**Port conflicts:**
- PostgreSQL uses 5432
- Application uses 8080
- MockServer uses 1080
- Change in docker-compose.yml and application.yml if needed

**Database connection issues:**
```bash
# Check PostgreSQL is running
docker-compose ps

# Check logs
docker-compose logs postgres

# Restart PostgreSQL
docker-compose restart postgres
```

**Flyway migration errors:**
```bash
# Clean and restart
./gradlew clean
docker-compose down -v
docker-compose up -d
./gradlew bootRun
```

## Summary

✅ Complete database layer with PostgreSQL 17
✅ JPA entities with validation
✅ Service layer with business logic
✅ RESTful API controllers
✅ External API integration with RestClient
✅ MockServer for external API testing
✅ 13 comprehensive Karate test scenarios
✅ Docker Compose setup
✅ Flyway database migrations
✅ Complete documentation

The framework is now production-ready with:
- Persistent storage
- Business logic validation
- External API integration
- Comprehensive testing
- Docker containerization
- Transaction management
- Error handling
- Logging

**Happy Testing! 🥋**
