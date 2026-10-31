# Karate Test Automation - Step-by-Step Learning Guide

## Table of Contents
1. [Introduction to Karate](#1-introduction-to-karate)
2. [Project Architecture Overview](#2-project-architecture-overview)
3. [Understanding the Test Infrastructure](#3-understanding-the-test-infrastructure)
4. [Writing Your First Feature File](#4-writing-your-first-feature-file)
5. [Advanced Feature Writing](#5-advanced-feature-writing)
6. [Data-Driven Testing](#6-data-driven-testing)
7. [Reusable Features & Helpers](#7-reusable-features--helpers)
8. [Configuration & Environment Management](#8-configuration--environment-management)
9. [Running & Debugging Tests](#9-running--debugging-tests)
10. [Best Practices & Common Patterns](#10-best-practices--common-patterns)

---

## 1. Introduction to Karate

### What is Karate?
Karate is a **unified framework** for API testing that combines:
- API test automation
- Mocks and performance testing
- UI automation (via Karate's browser automation)

### Why Karate?
- **No programming knowledge required** for basic tests
- Tests are written in **Gherkin syntax** (human-readable)
- Built-in support for JSON/XML validation
- Parallel execution out of the box
- Comprehensive assertion library

### Key Concepts
```gherkin
Feature: Describes what you're testing

  Background: Runs before each scenario (like @Before)
    * url baseUrl  # Set the base URL

  Scenario: A single test case
    Given path '/api/users'      # HTTP path
    When method GET              # HTTP method
    Then status 200              # Assert HTTP status
    And match response == {...} # Assert response body
```

---

## 2. Project Architecture Overview

### Directory Structure Explained

```
src/test/
├── java/
│   └── com/example/karateframework/karate/
│       ├── KarateTestBase.java        # Base class for all tests
│       ├── TestMarkers.java           # Tag constants (@smoke, @regression)
│       └── runner/
│           ├── AllKarateTest.java     # Runs all features
│           └── SmokeKarateTest.java   # Runs only @smoke tests
│
└── resources/
    ├── karate-config.js               # Global configuration
    ├── logback-test.xml               # Logging configuration
    └── karate/
        ├── features/                  # Your test scenarios
        │   └── sample/
        │       ├── health-check.feature
        │       └── user-crud.feature
        │
        ├── common/                    # Reusable features & utilities
        │   ├── create-user.feature
        │   └── utils.js
        │
        └── data/                      # Test data
            ├── csv/
            ├── request/
            └── response/
```

### Component Responsibilities

| Component | Purpose | When to Modify |
|-----------|---------|----------------|
| `KarateTestBase.java` | Manages Spring Boot startup and test execution | Adding new runner methods |
| `TestMarkers.java` | Defines test tags | Adding new test categories |
| `*KarateTest.java` | JUnit test runners | Creating new test suites |
| `karate-config.js` | Environment setup | Changing environments or base URLs |
| `*.feature` files | Actual test scenarios | Writing new tests |
| `common/` | Reusable test logic | Creating helper features |
| `data/` | Test data files | Managing test data |

---

## 3. Understanding the Test Infrastructure

### 3.1 KarateTestBase.java - The Foundation

**Location**: `src/test/java/com/example/karateframework/karate/KarateTestBase.java`

#### What It Does
1. Starts Spring Boot application with a random port
2. Passes the port to Karate via system properties
3. Provides helper methods to run tests
4. Handles environment switching (local, dev, uat, prod)

#### Key Methods

```java
// Run tests with full configuration
protected Results runKarate(String path, String tags, int parallel, String outputDir)

// Run tests with defaults (5 threads, standard output)
protected Results runKarate(String path, String tags)

// Run all tests in a path
protected Results runKarate(String path)

// Fail JUnit test if Karate tests failed
protected void assertKaratePassed(Results results)
```

#### Example Usage in a Test Runner

```java
@Test
void runMyTests() {
    Results results = runKarate(
        "classpath:karate/features/myfeature",  // Path to features
        "@smoke",                                 // Tag filter
        3,                                        // Parallel threads
        "build/karate-reports"                   // Report directory
    );

    assertKaratePassed(results);  // Fail if any scenario failed
}
```

### 3.2 Test Runners - Executing Your Tests

#### AllKarateTest.java - Run Everything

```java
@Test
void runAllKarateTests() {
    Results results = runKarate(
        "classpath:karate/features",  // All features
        TestMarkers.IGNORE,           // Exclude @ignore tagged scenarios
        5,                            // 5 parallel threads
        "build/karate-reports"
    );
    assertKaratePassed(results);
}
```

**When to use**: CI/CD pipelines, nightly builds, comprehensive testing

#### SmokeKarateTest.java - Run Critical Tests Only

```java
@Test
void runSmokeTests() {
    Results results = runKarate(
        "classpath:karate/features",
        TestMarkers.SMOKE,  // Only @smoke tagged scenarios
        3,
        "build/karate-reports"
    );
    assertKaratePassed(results);
}
```

**When to use**: Pre-deployment checks, quick feedback, PR validation

### 3.3 TestMarkers.java - Organizing Tests with Tags

**Location**: `src/test/java/com/example/karateframework/karate/TestMarkers.java`

#### Available Tags

```java
public static final String SMOKE = "@smoke";           // Critical tests
public static final String REGRESSION = "@regression"; // Full suite
public static final String INTEGRATION = "@integration"; // External deps
public static final String IGNORE = "~@ignore";        // Skip these
public static final String PERFORMANCE = "@performance"; // Perf tests
public static final String SECURITY = "@security";     // Security tests
```

#### How to Use Tags

In your feature file:
```gherkin
@smoke @regression
Scenario: Critical user login test
  # This runs in both smoke and regression suites

@ignore
Scenario: Work in progress test
  # This is skipped in all test runs
```

In your test runner:
```java
// Run only smoke tests
runKarate("classpath:karate/features", "@smoke", 3, "build/reports")

// Run smoke OR regression
runKarate("classpath:karate/features", "@smoke,@regression", 3, "build/reports")

// Run smoke AND regression (both tags required)
runKarate("classpath:karate/features", "@smoke @regression", 3, "build/reports")

// Exclude ignored tests
runKarate("classpath:karate/features", "~@ignore", 3, "build/reports")
```

---

## 4. Writing Your First Feature File

### 4.1 Anatomy of a Feature File

Let's break down `health-check.feature`:

```gherkin
Feature: Health Check API                    # 1. Feature description
  Verify that the application is up and running  # 2. Optional detail

  Background:                                # 3. Runs before each scenario
    * url baseUrl                           # 4. Set base URL from config

  @smoke                                    # 5. Tag for categorization
  Scenario: Health endpoint returns UP status  # 6. Test description
    Given path '/actuator/health'          # 7. Set the path
    When method GET                        # 8. Execute HTTP GET
    Then status 200                        # 9. Assert status code
    And match response.status == 'UP'     # 10. Assert field value
    And match response contains {         # 11. Assert structure
      status: '#string',                  # 12. Schema validation
      components: '#object'
    }
```

### 4.2 Step-by-Step: Write a Simple GET Request Test

**Step 1: Create a new feature file**

Location: `src/test/resources/karate/features/sample/my-first-test.feature`

```gherkin
Feature: My First API Test
  Learning how to test a GET endpoint
```

**Step 2: Add a Background section**

```gherkin
Feature: My First API Test

  Background:
    * url baseUrl
    # baseUrl comes from karate-config.js
    # In local mode: http://localhost:{random-port}
```

**Step 3: Write your first scenario**

```gherkin
Feature: My First API Test

  Background:
    * url baseUrl

  @smoke
  Scenario: Get all users returns 200
    Given path '/api/users'
    When method GET
    Then status 200
    And match responseType == 'json'
```

**Step 4: Run it**

```bash
./gradlew karateTest
```

**Step 5: Add more assertions**

```gherkin
  @smoke
  Scenario: Get all users returns 200
    Given path '/api/users'
    When method GET
    Then status 200
    And match responseType == 'json'
    And match response == '#object'
    # '#object' means "any object"
```

### 4.3 HTTP Methods Reference

```gherkin
# GET
Given path '/api/users'
When method GET
Then status 200

# POST
Given path '/api/users'
And request { username: 'john', email: 'john@test.com' }
When method POST
Then status 201

# PUT
Given path '/api/users/123'
And request { username: 'john_updated' }
When method PUT
Then status 200

# DELETE
Given path '/api/users/123'
When method DELETE
Then status 204

# PATCH
Given path '/api/users/123'
And request { active: false }
When method PATCH
Then status 200
```

### 4.4 Common Karate Keywords

| Keyword | Purpose | Example |
|---------|---------|---------|
| `url` | Set base URL | `* url baseUrl` |
| `path` | Set endpoint path | `Given path '/api/users'` |
| `method` | Execute HTTP method | `When method POST` |
| `request` | Set request body | `And request { name: 'test' }` |
| `status` | Assert status code | `Then status 200` |
| `match` | Assert response | `And match response.id == '#uuid'` |
| `header` | Set request header | `And header Authorization = 'Bearer token'` |
| `def` | Define variable | `* def userId = response.id` |
| `print` | Debug output | `* print 'User ID:', userId` |

---

## 5. Advanced Feature Writing

### 5.1 Working with Variables

```gherkin
Scenario: Create and retrieve a user
  # Define variables
  * def username = 'testuser'
  * def email = 'test@example.com'

  # Use variables in request
  Given path '/api/users'
  And request
    """
    {
      "username": "#(username)",
      "email": "#(email)",
      "firstName": "Test",
      "lastName": "User"
    }
    """
  When method POST
  Then status 201

  # Store response values
  * def userId = response.id
  * def createdUser = response

  # Use stored values in next request
  Given path '/api/users/' + userId
  When method GET
  Then status 200
  And match response == createdUser
```

### 5.2 Match Assertions - Complete Guide

#### Exact Match
```gherkin
# Exact value
And match response.username == 'johndoe'

# Exact object
And match response == { id: '123', name: 'John' }
```

#### Schema Validation
```gherkin
# Built-in validators
And match response.id == '#uuid'        # Valid UUID
And match response.username == '#string' # Any string
And match response.age == '#number'     # Any number
And match response.active == '#boolean' # Boolean
And match response.email == '#string'   # Any string
And match response.optional == '##string' # Optional string (can be null)

# Array validators
And match response.tags == '#array'     # Any array
And match response.tags == '#[3]'       # Array with exactly 3 items
And match response.tags == '#[]'        # Non-empty array
```

#### Contains vs Equals
```gherkin
# Contains - actual can have more fields
And match response contains { status: 'UP' }

# Equals - exact match, no extra fields allowed
And match response == { status: 'UP', components: {} }
```

#### Array Matching
```gherkin
# Each item matches schema
And match each response.users == { id: '#uuid', name: '#string' }

# Array contains specific item
And match response.users contains { name: 'John' }

# Array size
And match response.users == '#[3]'  # Exactly 3 items
```

#### Regular Expressions
```gherkin
# Email pattern
And match response.email == '#regex ^[a-zA-Z0-9+_.-]+@[a-zA-Z0-9.-]+$'

# Phone pattern
And match response.phone == '#regex \\d{3}-\\d{3}-\\d{4}'
```

#### Complex Matching
```gherkin
# Nested objects
And match response ==
  """
  {
    id: '#uuid',
    profile: {
      name: '#string',
      email: '#regex .+@.+',
      age: '#number',
      address: {
        city: '#string',
        country: '#string'
      }
    },
    tags: '#[]',
    metadata: '##object'
  }
  """
```

### 5.3 Reading External Files

```gherkin
Scenario: Create user from JSON file
  # Read JSON file
  * def requestData = read('classpath:karate/data/request/create-user.json')

  Given path '/api/users'
  And request requestData
  When method POST
  Then status 201

  # Read expected response schema
  * def expectedSchema = read('classpath:karate/data/response/user-response-schema.json')
  And match response == expectedSchema
```

File: `karate/data/request/create-user.json`
```json
{
  "username": "johndoe",
  "email": "john.doe@example.com",
  "firstName": "John",
  "lastName": "Doe"
}
```

File: `karate/data/response/user-response-schema.json`
```json
{
  "id": "#uuid",
  "username": "#string",
  "email": "#string",
  "firstName": "#string",
  "lastName": "#string",
  "active": "#boolean"
}
```

### 5.4 Working with Headers

```gherkin
Scenario: API with custom headers
  # Single header
  Given path '/api/users'
  And header Authorization = 'Bearer ' + authToken
  When method GET
  Then status 200

  # Multiple headers
  Given path '/api/users'
  And header Accept = 'application/json'
  And header Content-Type = 'application/json'
  And header X-API-Key = apiKey
  When method POST

  # Configure headers (applies to all requests)
  * configure headers = { Authorization: 'Bearer token', Accept: 'application/json' }

  # Assert response headers
  Then match responseHeaders['Content-Type'][0] == 'application/json'
```

### 5.5 Path Parameters and Query Strings

```gherkin
Scenario: Path and query parameters
  # Path parameters
  * def userId = '12345'
  Given path '/api/users', userId
  # Results in: /api/users/12345

  # Or using concatenation
  Given path '/api/users/' + userId

  # Query parameters
  Given path '/api/users'
  And param status = 'active'
  And param limit = 10
  # Results in: /api/users?status=active&limit=10

  # Multiple values for same parameter
  And param tags = 'admin'
  And param tags = 'verified'
  # Results in: /api/users?tags=admin&tags=verified

  # From object
  * def filters = { status: 'active', limit: 10 }
  And params filters
```

---

## 6. Data-Driven Testing

### 6.1 Scenario Outline with Examples Table

```gherkin
@regression
Scenario Outline: Create multiple users
  Given path '/api/users'
  And request
    """
    {
      "username": "<username>",
      "email": "<email>",
      "firstName": "<firstName>",
      "lastName": "<lastName>"
    }
    """
  When method POST
  Then status 201
  And match response.username == '<username>'
  And match response.email == '<email>'

  Examples:
    | username    | email                  | firstName | lastName |
    | alice_smith | alice@example.com      | Alice     | Smith    |
    | bob_jones   | bob@example.com        | Bob       | Jones    |
    | charlie_b   | charlie@example.com    | Charlie   | Brown    |
```

**How it works:**
- Karate runs the scenario 3 times (one per row)
- Each `<placeholder>` is replaced with the corresponding column value
- Each execution is independent

### 6.2 CSV-Based Data-Driven Testing

**CSV File**: `karate/data/csv/users.csv`
```csv
username,email,firstName,lastName
alice_smith,alice.smith@example.com,Alice,Smith
bob_jones,bob.jones@example.com,Bob,Jones
charlie_brown,charlie.brown@example.com,Charlie,Brown
```

**Feature File**:
```gherkin
@regression
Scenario Outline: Create users from CSV
  # Read CSV file (converted to array of objects)
  * def userData = read('classpath:karate/data/csv/users.csv')

  # Access specific row
  * def userRow = userData[<rowIndex>]

  Given path '/api/users'
  And request
    """
    {
      "username": "#(userRow.username)",
      "email": "#(userRow.email)",
      "firstName": "#(userRow.firstName)",
      "lastName": "#(userRow.lastName)"
    }
    """
  When method POST
  Then status 201
  And match response.username == userRow.username

  Examples:
    | rowIndex |
    | 0        |
    | 1        |
    | 2        |
```

### 6.3 JSON Array Data-Driven Testing

**JSON File**: `karate/data/test-users.json`
```json
[
  {
    "username": "alice",
    "email": "alice@test.com",
    "expectedRole": "admin"
  },
  {
    "username": "bob",
    "email": "bob@test.com",
    "expectedRole": "user"
  }
]
```

**Feature File**:
```gherkin
Scenario: Test multiple users with different roles
  * def testUsers = read('classpath:karate/data/test-users.json')

  # Loop through array
  * def testUser = function(user) {
      karate.log('Testing user:', user.username);
      return karate.call('create-user.feature', user);
    }

  * def results = karate.map(testUsers, testUser)
```

---

## 7. Reusable Features & Helpers

### 7.1 Calling Another Feature

**Reusable Feature**: `karate/common/create-user.feature`
```gherkin
Feature: Reusable user creation

  Scenario: Create a user and return the response
    * url baseUrl
    * def utils = call read('classpath:karate/common/utils.js')

    # Use provided data or generate random
    * def username = karate.get('username', utils.randomUsername())
    * def email = karate.get('email', utils.randomEmail())
    * def firstName = karate.get('firstName', 'Test')
    * def lastName = karate.get('lastName', 'User')

    Given path '/api/users'
    And request
      """
      {
        "username": "#(username)",
        "email": "#(email)",
        "firstName": "#(firstName)",
        "lastName": "#(lastName)"
      }
      """
    When method POST
    Then status 201

    # Return the created user
    * def createdUser = response
```

**Using the Reusable Feature**:
```gherkin
Scenario: Test with a newly created user
  # Call with custom data
  * def result = call read('classpath:karate/common/create-user.feature')
    { username: 'testuser', email: 'test@example.com' }

  * def userId = result.createdUser.id

  # Use the created user
  Given path '/api/users/' + userId
  When method GET
  Then status 200
```

### 7.2 JavaScript Utilities

**Location**: `karate/common/utils.js`

```javascript
function fn() {
    var utils = {};

    // Generate random email
    utils.randomEmail = function() {
        var uuid = java.util.UUID.randomUUID() + '';
        return 'test-' + uuid.substring(0, 8) + '@example.com';
    };

    // Generate random username
    utils.randomUsername = function() {
        var uuid = java.util.UUID.randomUUID() + '';
        return 'user_' + uuid.substring(0, 8);
    };

    // Generate random string
    utils.randomString = function(length) {
        var chars = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789';
        var result = '';
        for (var i = 0; i < length; i++) {
            result += chars.charAt(Math.floor(Math.random() * chars.length));
        }
        return result;
    };

    // Get current timestamp
    utils.timestamp = function() {
        return java.lang.System.currentTimeMillis();
    };

    // Sleep
    utils.sleep = function(millis) {
        java.lang.Thread.sleep(millis);
    };

    return utils;
}
```

**Using JavaScript Utils**:
```gherkin
Scenario: Create user with random data
  * def utils = call read('classpath:karate/common/utils.js')

  * def randomEmail = utils.randomEmail()
  * def randomUsername = utils.randomUsername()

  Given path '/api/users'
  And request
    """
    {
      "username": "#(randomUsername)",
      "email": "#(randomEmail)",
      "firstName": "Test",
      "lastName": "User"
    }
    """
  When method POST
  Then status 201
```

### 7.3 callSingle for One-Time Setup

Use `callSingle` for operations that should run **only once** per test suite:

```javascript
// In karate-config.js
function fn() {
    var config = { /* ... */ };

    // Get auth token once for all tests
    var authResult = karate.callSingle('classpath:karate/common/auth/get-token.feature', config);
    config.authToken = authResult.token;
    config.headers['Authorization'] = 'Bearer ' + authResult.token;

    return config;
}
```

---

## 8. Configuration & Environment Management

### 8.1 Understanding karate-config.js

**Location**: `src/test/resources/karate-config.js`

This file runs **once before all tests** and returns a configuration object.

```javascript
function fn() {
    // 1. Read environment (default: local)
    var env = karate.env || 'local';
    karate.log('karate.env system property was:', env);

    // 2. Base configuration
    var config = {
        env: env,
        connectTimeout: 30000,
        readTimeout: 30000
    };

    // 3. Environment-specific settings
    if (env === 'local') {
        var port = karate.properties['local.server.port'] || '8080';
        config.baseUrl = 'http://localhost:' + port;
    } else if (env === 'dev') {
        config.baseUrl = 'https://api-dev.example.com';
    } else if (env === 'uat') {
        config.baseUrl = 'https://api-uat.example.com';
    } else if (env === 'prod') {
        config.baseUrl = 'https://api.example.com';
    }

    // 4. Common headers
    var uuid = java.util.UUID.randomUUID() + '';
    config.headers = {
        'Content-Type': 'application/json',
        'Accept': 'application/json',
        'X-Request-ID': uuid
    };

    // 5. Read secrets from environment variables
    config.apiKey = karate.properties['api.key'] || java.lang.System.getenv('API_KEY');

    // 6. Apply global settings
    karate.configure('connectTimeout', config.connectTimeout);
    karate.configure('readTimeout', config.readTimeout);
    karate.configure('ssl', true);

    return config;
}
```

### 8.2 Using Configuration in Features

```gherkin
Feature: Using configuration

  Background:
    * url baseUrl  # From config.baseUrl

  Scenario: Configuration values are available
    # Access config values
    * print 'Environment:', karate.env
    * print 'Base URL:', baseUrl

    # Access custom config properties
    * def apiKey = karate.properties['api.key']

    Given path '/api/protected'
    And header X-API-Key = apiKey
    When method GET
```

### 8.3 Environment Switching

```bash
# Local (default) - uses embedded Spring Boot
./gradlew karateTest

# Dev environment
./gradlew karateTest -Dkarate.env=dev

# UAT environment
./gradlew karateTest -Dkarate.env=uat

# Production environment (use with caution!)
./gradlew karateTest -Dkarate.env=prod
```

### 8.4 Passing Custom Properties

```bash
# Pass API key
./gradlew karateTest -Dapi.key=my-secret-key

# Multiple properties
./gradlew karateTest \
  -Dkarate.env=uat \
  -Dapi.key=secret \
  -Dtest.username=admin \
  -Dtest.password=password123
```

Access in karate-config.js:
```javascript
config.apiKey = karate.properties['api.key'];
config.testUser = {
    username: karate.properties['test.username'] || 'defaultuser',
    password: karate.properties['test.password'] || 'defaultpass'
};
```

---

## 9. Running & Debugging Tests

### 9.1 Running Tests

```bash
# All tests
./gradlew karateTest

# Specific test class
./gradlew karateTest --tests AllKarateTest
./gradlew karateTest --tests SmokeKarateTest

# With environment
./gradlew karateTest -Dkarate.env=dev

# With tag filtering
./gradlew karateTest -Dkarate.options="--tags @smoke"
./gradlew karateTest -Dkarate.options="--tags ~@ignore"
./gradlew karateTest -Dkarate.options="--tags @smoke,@regression"

# Clean and run
./gradlew clean karateTest

# Parallel execution (adjust thread count in runner)
# Edit AllKarateTest.java: runKarate(..., 10, ...) for 10 threads

# Continue on failure
./gradlew karateTest --continue
```

### 9.2 Debugging Techniques

#### Print Statements
```gherkin
Scenario: Debug with print
  Given path '/api/users'
  When method GET
  Then status 200

  # Print entire response
  * print response

  # Print specific field
  * print 'User count:', response.length

  # Print variable
  * def userId = response[0].id
  * print 'First user ID:', userId

  # Pretty print JSON
  * print 'Response:', karate.pretty(response)
```

#### karate.log vs print
```gherkin
# print - shows in console and report
* print 'This shows everywhere'

# karate.log - shows only in log file
* karate.log('This is for debugging')
```

#### Assertions for Debugging
```gherkin
# See what the actual value is when test fails
And match response.status == 'ACTIVE'
# If fails, shows: "expected: 'ACTIVE', actual: 'INACTIVE'"

# Use contains to see structure
And match response contains { status: '#string' }
```

#### Configure Logging
Edit `logback-test.xml`:
```xml
<!-- More verbose Karate logging -->
<logger name="com.intuit.karate" level="DEBUG"/>

<!-- See HTTP requests/responses -->
<logger name="com.intuit.karate.http" level="DEBUG"/>
```

### 9.3 Understanding Test Reports

After running tests, open: `build/karate-reports/karate-summary.html`

**Report Components:**

1. **Summary**
   - Features passed/failed
   - Scenarios passed/failed
   - Total time
   - Efficiency (actual test time vs wall clock time)

2. **Feature List**
   - Each feature with pass/fail status
   - Click to see detailed scenario results

3. **Scenario Details**
   - Each step with request/response
   - Assertions with actual vs expected
   - Timing information

4. **Timeline**
   - Visualize parallel execution
   - Identify slow tests

### 9.4 Common Issues & Solutions

#### Issue: "http call failed - Connection refused"
```
Error: Connect to localhost:8080 failed: Connection refused
```
**Solution**: Spring Boot app didn't start or wrong port
- Check if `@SpringBootTest` is on `KarateTestBase`
- Verify port is passed correctly
- Check application logs

#### Issue: "match failed: EQUALS"
```
match failed: EQUALS
  actual has 1 more key(s) than expected - {"extraField":"value"}
```
**Solution**: Use `contains` instead of `==`
```gherkin
# Before (exact match)
And match response == { status: 'UP' }

# After (partial match)
And match response contains { status: 'UP' }
```

#### Issue: "path not found"
```
Error: read('classpath:karate/data/users.csv') failed
```
**Solution**: Check file path
- Paths are relative to `src/test/resources/`
- Use `classpath:` prefix
- Check file extension spelling

#### Issue: Tests pass locally but fail in CI
**Common causes**:
1. Environment variables not set
2. Wrong `karate.env` value
3. Network/firewall blocking external URLs
4. Race conditions in parallel execution

**Debug**:
```bash
# Run sequentially
# Edit runner: runKarate(..., 1, ...)

# Enable debug logging
# Edit logback-test.xml: level="DEBUG"

# Print environment
* print 'Environment:', karate.env
* print 'Base URL:', baseUrl
```

---

## 10. Best Practices & Common Patterns

### 10.1 Test Organization

#### ✅ DO: Organize by domain
```
features/
├── auth/
│   ├── login.feature
│   └── logout.feature
├── users/
│   ├── create-user.feature
│   ├── update-user.feature
│   └── delete-user.feature
└── orders/
    └── place-order.feature
```

#### ❌ DON'T: Put everything in one file
```
features/
└── all-tests.feature  # 5000 lines - hard to maintain
```

### 10.2 Scenario Independence

#### ✅ DO: Each scenario is independent
```gherkin
Scenario: Create user test
  * def user = call read('classpath:karate/common/create-user.feature')
  * def userId = user.createdUser.id

  # Test with this user
  Given path '/api/users/' + userId
  When method GET
  Then status 200

  # Clean up
  Given path '/api/users/' + userId
  When method DELETE
  Then status 204
```

#### ❌ DON'T: Depend on other scenarios
```gherkin
Scenario: Create user
  * def userId = '123'  # Global state!

Scenario: Get user  # Depends on previous scenario
  Given path '/api/users/' + userId  # Will fail if run alone
```

### 10.3 Naming Conventions

#### Features
```gherkin
Feature: User Management API
  # Clear, descriptive, business-focused

Feature: CRUD operations  # ❌ Too vague
```

#### Scenarios
```gherkin
@smoke
Scenario: Successfully create a new active user
  # ✅ Describes the expected outcome

Scenario: Test 1  # ❌ Not descriptive
```

#### Tags
```gherkin
@smoke @regression  # ✅ Multiple relevant tags

@test @temp @asdf   # ❌ Meaningless tags
```

### 10.4 Data Management

#### ✅ DO: Use external files for large data
```gherkin
* def requestBody = read('classpath:karate/data/request/create-user.json')
And request requestBody
```

#### ❌ DON'T: Inline large JSON
```gherkin
And request { username: 'user', email: 'email', firstName: 'first', ... } # Hard to read
```

#### ✅ DO: Use schema validation
```gherkin
* def expectedSchema = read('classpath:karate/data/response/user-schema.json')
And match response == expectedSchema
```

#### ❌ DON'T: Hard-code all field assertions
```gherkin
And match response.id == '#uuid'
And match response.username == '#string'
And match response.email == '#string'
# ... 20 more lines
```

### 10.5 Reusability

#### ✅ DO: Create reusable features
```
common/
├── create-user.feature
├── get-auth-token.feature
└── utils.js
```

#### ✅ DO: Use utility functions
```javascript
// utils.js
utils.generateTestUser = function() {
    return {
        username: utils.randomUsername(),
        email: utils.randomEmail(),
        firstName: 'Test',
        lastName: 'User'
    };
};
```

```gherkin
* def utils = call read('classpath:karate/common/utils.js')
* def testUser = utils.generateTestUser()
```

### 10.6 Error Handling

#### ✅ DO: Test both success and failure cases
```gherkin
@regression
Scenario: Get non-existent user returns 404
  Given path '/api/users/non-existent-id-12345'
  When method GET
  Then status 404
  And match response.error == 'User not found'
```

#### ✅ DO: Test validation errors
```gherkin
@regression
Scenario: Create user with invalid email returns 400
  Given path '/api/users'
  And request { username: 'test', email: 'invalid-email' }
  When method POST
  Then status 400
  And match response.errors contains { field: 'email', message: '#string' }
```

### 10.7 Performance Considerations

#### Parallel Execution
```java
// For many independent tests
runKarate("classpath:karate/features", "@regression", 10, "build/reports")

// For tests with shared resources
runKarate("classpath:karate/features", "@integration", 1, "build/reports")
```

#### Use callSingle for Expensive Operations
```javascript
// In karate-config.js
// Run once for all tests
var authToken = karate.callSingle('classpath:karate/common/get-token.feature').token;
config.authToken = authToken;
```

### 10.8 Security Best Practices

#### ✅ DO: Use environment variables for secrets
```javascript
// karate-config.js
config.apiKey = java.lang.System.getenv('API_KEY');
config.dbPassword = java.lang.System.getenv('DB_PASSWORD');
```

#### ❌ DON'T: Hard-code credentials
```javascript
config.apiKey = 'sk-1234567890abcdef';  // ❌ Security risk!
```

#### ✅ DO: Use .gitignore for sensitive files
```
# .gitignore
*.env
**/secrets/
local-config.js
```

### 10.9 Tagging Strategy

```gherkin
@smoke                    # Critical path, run on every commit
@regression               # Full suite, run nightly
@integration              # Requires external systems
@slow                     # Long-running tests
@wip                      # Work in progress
@ignore                   # Skip temporarily
@bug:JIRA-123            # Reproduces specific bug
@security                 # Security-related tests
@performance              # Performance tests
```

### 10.10 Documentation

#### ✅ DO: Add descriptions
```gherkin
Feature: User Management API
  Comprehensive tests for user CRUD operations
  Covers creation, retrieval, updates, and deletion
  Tests both successful operations and error cases

  Background:
    * url baseUrl
    # All tests use the local Spring Boot instance

  @smoke
  Scenario: Create new user with valid data
    # Tests the happy path for user creation
    # Verifies that all fields are correctly stored
    # Ensures UUID is generated for new users
```

#### ✅ DO: Keep README updated
Create `karate/features/README.md`:
```markdown
# API Test Features

## Structure
- `auth/` - Authentication and authorization tests
- `users/` - User management CRUD operations
- `orders/` - Order processing workflows

## Running Tests
- Smoke: `./gradlew karateTest --tests SmokeKarateTest`
- Full: `./gradlew karateTest`
```

---

## Appendix: Quick Reference

### Karate Syntax Cheat Sheet

```gherkin
# Variables
* def name = 'value'
* def age = 25
* def data = { key: 'value' }

# HTTP
Given url 'https://api.example.com'
And path '/users'
And param status = 'active'
And header Authorization = 'Bearer token'
And request { name: 'test' }
When method POST
Then status 201

# Assertions
And match response.id == '#uuid'
And match response.name == 'John'
And match response contains { status: 'active' }
And match each response.items == { id: '#number' }

# Files
* def data = read('file.json')
* def data = read('file.csv')
* def data = read('file.xml')

# Calling features
* def result = call read('other.feature')
* def result = call read('other.feature') { param: 'value' }

# JavaScript
* def random = function() { return Math.random(); }
* def result = random()

# Print/Debug
* print 'message'
* print variable
* karate.log('debug message')

# Utilities
* def uuid = java.util.UUID.randomUUID()
* def currentTime = java.lang.System.currentTimeMillis()
```

### Common Validation Patterns

```gherkin
# UUID
And match response.id == '#uuid'

# Email
And match response.email == '#regex ^[a-zA-Z0-9+_.-]+@[a-zA-Z0-9.-]+$'

# Date ISO 8601
And match response.createdAt == '#regex \\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}'

# Phone (US)
And match response.phone == '#regex \\d{3}-\\d{3}-\\d{4}'

# Non-empty string
And match response.name == '#string'
And match response.name == '#notnull'

# Optional field
And match response.middleName == '##string'

# Array of specific size
And match response.items == '#[5]'

# Non-empty array
And match response.items == '#[]'

# Object structure
And match response == { id: '#number', name: '#string', active: '#boolean' }
```

---

## Next Steps

1. **Practice**: Start with simple GET requests and gradually add complexity
2. **Experiment**: Try different matchers and assertions
3. **Read Official Docs**: https://karatelabs.github.io/karate/
4. **Review Examples**: Study the provided feature files in `karate/features/sample/`
5. **Build Your Own**: Create test scenarios for your actual APIs

**Happy Testing! 🥋**
