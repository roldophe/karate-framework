# Karate Testing - Quick Start Guide

## 🚀 Get Started in 5 Minutes

### 1. Run Your First Test

```bash
# Run all tests
./gradlew karateTest

# Run only smoke tests
./gradlew karateTest --tests SmokeKarateTest
```

**Expected Output:**
```
BUILD SUCCESSFUL in 4s

Karate Test Report:
build/karate-reports/karate-summary.html
```

### 2. View Test Results

Open in browser: `build/karate-reports/karate-summary.html`

### 3. Write Your First Test

Create: `src/test/resources/karate/features/practice/my-test.feature`

```gherkin
Feature: My First Test

  Background:
    * url baseUrl

  @smoke
  Scenario: Get all users
    Given path '/api/users'
    When method GET
    Then status 200
    And match responseType == 'json'
```

Run it:
```bash
./gradlew karateTest
```

---

## 📚 Essential Commands

### Running Tests

| Command | Description |
|---------|-------------|
| `./gradlew karateTest` | Run all Karate tests |
| `./gradlew clean karateTest` | Clean and run tests |
| `./gradlew karateTest --tests SmokeKarateTest` | Run smoke tests only |
| `./gradlew karateTest -Dkarate.env=dev` | Run against dev environment |
| `./gradlew karateTest -Dkarate.options="--tags @smoke"` | Run tests with @smoke tag |

### Code Quality

| Command | Description |
|---------|-------------|
| `./gradlew spotlessApply` | Format all code (Google Java Style) |
| `./gradlew spotlessCheck` | Check if code is formatted |
| `./gradlew pmdMain pmdTest` | Run static code analysis |
| `./gradlew codeQuality` | Run all quality checks |
| `./gradlew formatAndCheck` | Format code and run checks |

### Environment Configuration

```bash
# Local (default) - embedded Spring Boot
./gradlew karateTest

# Dev environment
./gradlew karateTest -Dkarate.env=dev

# UAT environment
./gradlew karateTest -Dkarate.env=uat
```

---

## 🔧 Project Structure Quick Reference

```
src/test/
├── java/                              # Java test infrastructure
│   └── karate/
│       ├── KarateTestBase.java        # Base class (don't modify)
│       ├── TestMarkers.java           # Tag constants
│       └── runner/
│           ├── AllKarateTest.java     # Run all tests
│           └── SmokeKarateTest.java   # Run smoke tests
│
└── resources/
    ├── karate-config.js               # Global config (environments)
    ├── logback-test.xml               # Logging config
    └── karate/
        ├── features/                  # 👈 YOUR TESTS GO HERE
        │   └── sample/
        │       ├── health-check.feature
        │       └── user-crud.feature
        │
        ├── common/                    # Reusable features
        │   ├── create-user.feature
        │   └── utils.js
        │
        └── data/                      # Test data
            ├── csv/
            ├── request/
            └── response/
```

---

## 📝 Basic Feature File Template

```gherkin
Feature: [Feature Name]
  [Optional description]

  Background:
    * url baseUrl

  @smoke
  Scenario: [Test Description]
    Given path '/api/endpoint'
    When method GET
    Then status 200
    And match response contains { key: 'value' }
```

---

## 🎯 Common Test Patterns

### 1. Simple GET Request

```gherkin
Scenario: Get a resource
  Given path '/api/users/123'
  When method GET
  Then status 200
  And match response.id == '123'
  And match response.username == '#string'
```

### 2. POST Request with JSON Body

```gherkin
Scenario: Create a resource
  Given path '/api/users'
  And request
    """
    {
      "username": "testuser",
      "email": "test@example.com"
    }
    """
  When method POST
  Then status 201
  And match response.id == '#uuid'
```

### 3. Using Variables

```gherkin
Scenario: Create and retrieve
  # Create
  Given path '/api/users'
  And request { username: 'test', email: 'test@example.com' }
  When method POST
  Then status 201
  * def userId = response.id

  # Retrieve
  Given path '/api/users/' + userId
  When method GET
  Then status 200
  And match response.username == 'test'
```

### 4. Using External Data Files

```gherkin
Scenario: Create user from file
  * def userData = read('classpath:karate/data/request/create-user.json')

  Given path '/api/users'
  And request userData
  When method POST
  Then status 201
```

### 5. Testing Error Cases

```gherkin
Scenario: Resource not found
  Given path '/api/users/non-existent-id'
  When method GET
  Then status 404
```

---

## ✅ Validation Cheat Sheet

```gherkin
# String matching
And match response.name == 'John'           # Exact match
And match response.name == '#string'        # Any string
And match response.name == '##string'       # Optional string

# Number matching
And match response.age == 25                # Exact number
And match response.age == '#number'         # Any number

# Boolean
And match response.active == true           # Exact boolean
And match response.active == '#boolean'     # Any boolean

# UUID
And match response.id == '#uuid'            # Valid UUID

# Objects
And match response == { id: '#uuid', name: '#string' }
And match response contains { status: 'active' }

# Arrays
And match response.items == '#array'        # Any array
And match response.items == '#[5]'          # Array with 5 items
And match each response.items == { id: '#number' }  # Each item matches

# Null
And match response.optionalField == null    # Is null
And match response.optionalField == '#null' # Is null
And match response.optionalField == '##string'  # Null or string

# Regular expressions
And match response.email == '#regex ^[a-zA-Z0-9+_.-]+@[a-zA-Z0-9.-]+$'
```

---

## 🏷️ Using Tags

### In Feature Files

```gherkin
@smoke
Scenario: Critical test
  # This runs with smoke tests

@regression
Scenario: Full regression test
  # This runs with regression tests

@smoke @regression
Scenario: Both suites
  # This runs in both

@ignore
Scenario: Temporarily disabled
  # This is skipped
```

### Available Tags (TestMarkers.java)

- `@smoke` - Critical tests, run on every commit
- `@regression` - Full test suite
- `@integration` - Tests requiring external systems
- `@performance` - Performance tests
- `@security` - Security tests
- `@ignore` - Skip this test

### Run by Tag

```bash
# Run smoke tests
./gradlew karateTest -Dkarate.options="--tags @smoke"

# Run regression tests
./gradlew karateTest -Dkarate.options="--tags @regression"

# Exclude ignored tests
./gradlew karateTest -Dkarate.options="--tags ~@ignore"

# Multiple tags (OR)
./gradlew karateTest -Dkarate.options="--tags @smoke,@regression"
```

---

## 🐛 Debugging Tips

### 1. Print Statements

```gherkin
* print 'Response:', response
* print 'User ID:', response.id
* print 'Environment:', karate.env
```

### 2. Check Test Reports

Open: `build/karate-reports/karate-summary.html`
- See all requests/responses
- View assertion failures
- Check timing

### 3. Enable Debug Logging

Edit `src/test/resources/logback-test.xml`:
```xml
<logger name="com.intuit.karate" level="DEBUG"/>
```

### 4. Common Issues

**Connection Refused**
- Spring Boot didn't start
- Check `@SpringBootTest` is on KarateTestBase

**Path Not Found**
- Check file path: `classpath:karate/...`
- Files are relative to `src/test/resources/`

**Match Failed**
- Use `contains` instead of `==` for partial matches
- Check response in test report

---

## 📖 Learning Path

1. **Start Here** ⭐
   - Read: [QUICK_START.md](QUICK_START.md) (this file)
   - Run: `./gradlew karateTest`
   - Open: `build/karate-reports/karate-summary.html`

2. **Learn the Basics**
   - Read: [KARATE_LEARNING_GUIDE.md](KARATE_LEARNING_GUIDE.md) sections 1-4
   - Study: `karate/features/sample/health-check.feature`
   - Practice: Write your first GET request test

3. **Intermediate Topics**
   - Read: [KARATE_LEARNING_GUIDE.md](KARATE_LEARNING_GUIDE.md) sections 5-7
   - Study: `karate/features/sample/user-crud.feature`
   - Practice: Write POST, PUT, DELETE tests

4. **Advanced Features**
   - Read: [KARATE_LEARNING_GUIDE.md](KARATE_LEARNING_GUIDE.md) sections 8-10
   - Study: `karate/common/create-user.feature`
   - Practice: Create reusable features

---

## 🔗 Useful Resources

- **Official Documentation**: https://karatelabs.github.io/karate/
- **GitHub**: https://github.com/karatelabs/karate
- **Examples**: Check `src/test/resources/karate/features/sample/`

---

## 💡 Quick Tips

1. **Start Simple**: Begin with GET requests, then add complexity
2. **Use Tags**: Organize tests with `@smoke`, `@regression` tags
3. **One Scenario = One Test**: Keep scenarios independent
4. **Print Debug Info**: Use `* print` liberally while developing
5. **Check Reports**: Always review HTML reports after failures
6. **Parallel Execution**: Tests run in parallel by default (5 threads)
7. **Environment Variables**: Never hard-code secrets

---

## 🎓 Example Workflow

### Step 1: Write a Feature
```gherkin
# src/test/resources/karate/features/practice/my-api.feature
Feature: My API Tests

  Background:
    * url baseUrl

  @smoke
  Scenario: Test health endpoint
    Given path '/actuator/health'
    When method GET
    Then status 200
    And match response.status == 'UP'
```

### Step 2: Run It
```bash
./gradlew karateTest
```

### Step 3: Check Results
```bash
# Open in browser
open build/karate-reports/karate-summary.html
```

### Step 4: Add More Tests
```gherkin
  @smoke
  Scenario: Create a user
    Given path '/api/users'
    And request { username: 'test', email: 'test@example.com' }
    When method POST
    Then status 201
    And match response.id == '#uuid'
```

### Step 5: Run Again
```bash
./gradlew karateTest
```

---

## 🚨 Need Help?

1. Check the detailed guide: [KARATE_LEARNING_GUIDE.md](KARATE_LEARNING_GUIDE.md)
2. Review sample tests: `karate/features/sample/`
3. Read official docs: https://karatelabs.github.io/karate/
4. Check test reports: `build/karate-reports/karate-summary.html`

---

**Happy Testing! 🥋**
