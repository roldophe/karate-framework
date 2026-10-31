# Code Quality Tools - Spotless & PMD

This project uses **Spotless** for code formatting and **PMD** for static code analysis to maintain consistent, high-quality code.

---

## Table of Contents
1. [Overview](#overview)
2. [Spotless - Code Formatting](#spotless---code-formatting)
3. [PMD - Static Code Analysis](#pmd---static-code-analysis)
4. [EditorConfig](#editorconfig)
5. [Usage Commands](#usage-commands)
6. [IDE Integration](#ide-integration)
7. [CI/CD Integration](#cicd-integration)
8. [Troubleshooting](#troubleshooting)

---

## Overview

### What These Tools Do

| Tool | Purpose | When It Runs |
|------|---------|--------------|
| **Spotless** | Automatic code formatting (Google Java Style) | On demand or pre-commit |
| **PMD** | Static code analysis (bug detection, best practices) | During build or on demand |
| **EditorConfig** | IDE/editor configuration for consistent formatting | Automatically in supported editors |

### Why We Use Them

- **Consistency**: Same code style across the entire team
- **Quality**: Catch common bugs and code smells early
- **Automation**: No manual formatting or style debates
- **CI/CD**: Enforce standards in build pipelines

---

## Spotless - Code Formatting

### What Spotless Does

Spotless automatically formats your code according to **Google Java Format (AOSP variant)**:

- Consistent indentation (4 spaces for Java)
- Import organization
- Line wrapping
- Trailing whitespace removal
- Newline at end of file

### Configuration

Location: `build.gradle.kts` lines 118-151

```kotlin
spotless {
    java {
        googleJavaFormat("1.19.2").aosp().reflowLongStrings()
        importOrder("java", "javax", "org", "com", "")
        removeUnusedImports()
        trimTrailingWhitespace()
        endWithNewline()
    }

    format("misc") {
        target("**/*.md", "**/*.yaml", "**/*.yml", "**/*.json")
        trimTrailingWhitespace()
        indentWithSpaces(2)
        endWithNewline()
    }
}
```

### Commands

```bash
# Check if code is formatted correctly (fails if not)
./gradlew spotlessCheck

# Automatically format all code
./gradlew spotlessApply

# Format only Java files
./gradlew spotlessJavaApply

# Format only JSON/YAML/Markdown
./gradlew spotlessMiscApply
```

### What Gets Formatted

- **Java files**: `src/**/*.java`
- **Markdown files**: `**/*.md`
- **YAML files**: `**/*.{yml,yaml}`
- **JSON files**: `**/*.json`
- **Excluded**: `build/**`, `.gradle/**`

### Before/After Example

**Before Spotless:**
```java
package com.example;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;import java.util.Map;
public class UserController{
private final Map<String,User> userStore=new HashMap<>();
    @PostMapping public ResponseEntity<User> createUser(@RequestBody User user){
        return ResponseEntity.ok(user);
}}
```

**After Spotless:**
```java
package com.example;

import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.*;

public class UserController {

    private final Map<String, User> userStore = new HashMap<>();

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        return ResponseEntity.ok(user);
    }
}
```

---

## PMD - Static Code Analysis

### What PMD Does

PMD analyzes Java source code to find:

- **Bugs**: Potential runtime errors
- **Code smells**: Poor design choices
- **Best practices violations**: Un-idiomatic Java
- **Performance issues**: Inefficient code
- **Security vulnerabilities**: Common security mistakes

### Configuration

Location: `config/pmd/ruleset.xml`

#### Enabled Rule Categories

1. **Best Practices**: JUnit assertions, proper use of APIs
2. **Code Style**: Naming conventions, formatting
3. **Design**: Complexity, coupling, cohesion
4. **Error Prone**: Common mistakes, null handling
5. **Performance**: String concatenation, loop optimization
6. **Security**: All security rules enabled

#### Customizations for This Project

We've **disabled** certain overly strict rules:

```xml
<!-- Allow test methods without assertion messages -->
<exclude name="JUnitAssertionsShouldIncludeMessage"/>

<!-- Allow short variable names in tests -->
<exclude name="ShortVariable"/>

<!-- Allow System.out in test logging -->
<exclude name="SystemPrintln"/>

<!-- Allow high complexity in test methods -->
<exclude name="CyclomaticComplexity"/>
```

See `config/pmd/ruleset.xml` for the complete configuration.

###Commands

```bash
# Run PMD on main code
./gradlew pmdMain

# Run PMD on test code
./gradlew pmdTest

# Run PMD on both
./gradlew pmdMain pmdTest

# Run all code quality checks (Spotless + PMD)
./gradlew codeQuality
```

### Reports

PMD generates reports in two formats:

1. **HTML Report** (human-readable)
   - Main: `build/reports/pmd/main.html`
   - Test: `build/reports/pmd/test.html`

2. **XML Report** (machine-readable for CI/CD)
   - Main: `build/reports/pmd/main.xml`
   - Test: `build/reports/pmd/test.xml`

### Example Violations

#### High Priority (Must Fix)

```java
// ❌ BAD: Null pointer exception risk
public void process(User user) {
    System.out.println(user.getName());  // NPE if user is null
}

// ✅ GOOD: Null check
public void process(User user) {
    if (user != null) {
        System.out.println(user.getName());
    }
}
```

```java
// ❌ BAD: Catching Throwable
try {
    riskyOperation();
} catch (Throwable t) {  // Too broad!
    log.error("Error", t);
}

// ✅ GOOD: Catch specific exceptions
try {
    riskyOperation();
} catch (IOException | SQLException e) {
    log.error("Error", e);
}
```

#### Medium Priority (Should Fix)

```java
// ❌ BAD: Unused variable
public void createUser(String username, String email) {
    String id = UUID.randomUUID().toString();  // Never used!
    User user = new User(username, email);
    save(user);
}

// ✅ GOOD: Remove unused variable
public void createUser(String username, String email) {
    User user = new User(username, email);
    save(user);
}
```

---

## EditorConfig

### What EditorConfig Does

EditorConfig helps maintain consistent coding styles between different editors and IDEs:

- Character encoding (UTF-8)
- Line endings (LF)
- Indentation (spaces vs tabs, size)
- Trailing whitespace
- Final newline

### Configuration

Location: `.editorconfig`

```ini
[*.java]
indent_style = space
indent_size = 4
max_line_length = 100

[*.{yml,yaml,json}]
indent_style = space
indent_size = 2

[*.feature]
indent_style = space
indent_size = 2
```

### Supported Editors

EditorConfig works automatically in:

- IntelliJ IDEA (built-in support)
- VS Code (with EditorConfig extension)
- Eclipse (with EditorConfig plugin)
- Vim/Neovim (with editorconfig-vim)
- Sublime Text (with EditorConfig package)

---

## Usage Commands

### Daily Development

```bash
# Before committing code - format everything
./gradlew spotlessApply

# Check formatting without changing files
./gradlew spotlessCheck

# Run static analysis
./gradlew pmdMain pmdTest

# Run all code quality checks
./gradlew codeQuality
```

### CI/CD Pipeline

```bash
# Check formatting (fail build if not formatted)
./gradlew spotlessCheck

# Run PMD analysis
./gradlew pmdMain pmdTest

# Or use combined task
./gradlew codeQuality
```

### Custom Tasks

We've created convenience tasks:

```bash
# Run all code quality checks (Spotless + PMD)
./gradlew codeQuality

# Format code and then run checks
./gradlew formatAndCheck
```

### Pre-Commit Hook (Optional)

Create `.git/hooks/pre-commit`:

```bash
#!/bin/sh
echo "Running Spotless..."
./gradlew spotlessApply --quiet

if [ $? -ne 0 ]; then
  echo "Spotless failed. Please fix formatting issues."
  exit 1
fi

echo "Code formatted successfully!"
exit 0
```

Make it executable:
```bash
chmod +x .git/hooks/pre-commit
```

---

## IDE Integration

### IntelliJ IDEA

#### 1. Import Google Java Format

**File** → **Settings** → **Editor** → **Code Style** → **Java**

Click **Scheme** gear icon → **Import Scheme** → **IntelliJ IDEA code style XML**

Or install the **Google Java Format** plugin:

**File** → **Settings** → **Plugins** → Search "google-java-format" → Install

#### 2. Enable EditorConfig

IntelliJ automatically detects `.editorconfig` - no configuration needed!

Verify: **File** → **Settings** → **Editor** → **Code Style**
Check: "Enable EditorConfig support" (should be enabled)

#### 3. PMD Plugin (Optional)

**File** → **Settings** → **Plugins** → Search "PMD" → Install **PMDPlugin**

Configure to use custom ruleset:
**File** → **Settings** → **PMD** → Add Rule → Browse to `config/pmd/ruleset.xml`

#### 4. Save Actions (Auto-format on save)

Install **Save Actions** plugin:

**File** → **Settings** → **Plugins** → Search "Save Actions" → Install

Configure:
- ✅ Optimize imports
- ✅ Reformat file
- ✅ Rearrange code

### VS Code

#### 1. Install Extensions

```bash
# Language Support for Java
ext install vscjava.vscode-java-pack

# EditorConfig
ext install editorconfig.editorconfig

# Spotless Gradle
ext install richardwillis.vscode-spotless-gradle
```

#### 2. Configure Settings

Add to `.vscode/settings.json`:

```json
{
  "java.format.enabled": true,
  "editor.formatOnSave": true,
  "java.saveActions.organizeImports": true,
  "files.trimTrailingWhitespace": true,
  "files.insertFinalNewline": true
}
```

#### 3. Spotless Integration

Create task in `.vscode/tasks.json`:

```json
{
  "version": "2.0.0",
  "tasks": [
    {
      "label": "Spotless Apply",
      "type": "shell",
      "command": "./gradlew spotlessApply",
      "group": "build"
    }
  ]
}
```

Run: **Terminal** → **Run Task** → **Spotless Apply**

---

## CI/CD Integration

### GitHub Actions

Add to `.github/workflows/code-quality.yml`:

```yaml
name: Code Quality

on:
  pull_request:
    branches: [ main, develop ]
  push:
    branches: [ main, develop ]

jobs:
  code-quality:
    runs-on: ubuntu-latest

    steps:
      - uses: actions/checkout@v4

      - name: Set up JDK 21
        uses: actions/setup-java@v4
        with:
          java-version: '21'
          distribution: 'temurin'

      - name: Grant execute permission for gradlew
        run: chmod +x gradlew

      - name: Check code formatting
        run: ./gradlew spotlessCheck

      - name: Run PMD analysis
        run: ./gradlew pmdMain pmdTest

      - name: Upload PMD reports
        if: always()
        uses: actions/upload-artifact@v4
        with:
          name: pmd-reports
          path: build/reports/pmd/
```

### GitLab CI

Add to `.gitlab-ci.yml`:

```yaml
code_quality:
  stage: test
  image: gradle:8-jdk21
  script:
    - ./gradlew spotlessCheck
    - ./gradlew pmdMain pmdTest
  artifacts:
    paths:
      - build/reports/pmd/
    reports:
      junit: build/reports/pmd/*.xml
  only:
    - merge_requests
    - main
    - develop
```

### Jenkins

Add to `Jenkinsfile`:

```groovy
stage('Code Quality') {
    steps {
        sh './gradlew spotlessCheck'
        sh './gradlew pmdMain pmdTest'
    }
    post {
        always {
            publishHTML([
                reportDir: 'build/reports/pmd',
                reportFiles: 'main.html,test.html',
                reportName: 'PMD Report'
            ])
        }
    }
}
```

---

## Troubleshooting

### Spotless Issues

#### Issue: "File is not formatted correctly"

```
> Task :spotlessJavaCheck FAILED
The following files had format violations:
    src/main/java/com/example/UserController.java
```

**Solution**: Run `./gradlew spotlessApply` to fix automatically

#### Issue: "Class not found" errors

Usually means Google Java Format dependency issue.

**Solution**: Clear Gradle cache
```bash
./gradlew clean --refresh-dependencies
rm -rf ~/.gradle/caches/
./gradlew spotlessApply
```

### PMD Issues

#### Issue: "Ruleset not found"

```
ERROR: Could not find ruleset file: config/pmd/ruleset.xml
```

**Solution**: Ensure `config/pmd/ruleset.xml` exists

```bash
# Check if file exists
ls -la config/pmd/ruleset.xml

# Recreate if missing
mkdir -p config/pmd
# (Copy ruleset.xml content from this repo)
```

#### Issue: "Too many violations"

PMD finds hundreds of issues in legacy code.

**Solution**: Fix incrementally

```bash
# Run PMD only on changed files
git diff --name-only | grep '\.java$' | xargs -I {} ./gradlew pmdMain -x test

# Or temporarily disable strict mode
# Edit build.gradle.kts: isIgnoreFailures = true
```

#### Issue: "Rule XYZ doesn't exist"

PMD version mismatch.

**Solution**: Check PMD version compatibility

```bash
# Current version in build.gradle.kts
toolVersion = "7.0.0"

# Update to latest
toolVersion = "7.6.0"  # or latest stable
```

### EditorConfig Issues

#### Issue: EditorConfig not working in IDE

**IntelliJ**: File → Settings → Editor → Code Style → Enable EditorConfig

**VS Code**: Install EditorConfig extension

**Eclipse**: Install EditorConfig plugin from marketplace

---

## Best Practices

### 1. Run Spotless Before Committing

```bash
# Make it a habit
./gradlew spotlessApply && git add .
```

### 2. Fix PMD Violations Immediately

Don't let violations accumulate. Fix them as they appear:

```bash
# Check for violations
./gradlew pmdMain

# Open report
open build/reports/pmd/main.html

# Fix issues
# Re-run to verify
./gradlew pmdMain
```

### 3. Use IDE Formatting

Configure your IDE to use the same style:

- IntelliJ: Use Google Java Format plugin
- VS Code: Use Java formatting extension
- Both: Respect `.editorconfig`

### 4. Don't Fight the Formatter

If Spotless formats something you don't like:

- **DO**: Accept it (consistency is more important than personal preference)
- **DON'T**: Manually reformat and commit

### 5. Review PMD Reports

PMD finds real bugs! Don't ignore warnings:

- **High priority**: Fix immediately
- **Medium priority**: Fix before merge
- **Low priority**: Fix during refactoring

### 6. Configure Pre-Commit Hooks

Automate quality checks:

```bash
# .git/hooks/pre-commit
#!/bin/sh
./gradlew spotlessCheck spotlessApply
./gradlew pmdMain --continue
```

---

## Summary

| Task | Command | When |
|------|---------|------|
| Format code | `./gradlew spotlessApply` | Before commit |
| Check formatting | `./gradlew spotlessCheck` | In CI/CD |
| Analyze code | `./gradlew pmdMain pmdTest` | Before merge |
| All checks | `./gradlew codeQuality` | Pre-commit, CI/CD |
| Format & analyze | `./gradlew formatAndCheck` | Development |

### Quick Reference

```bash
# Daily workflow
./gradlew spotlessApply  # Format code
./gradlew pmdMain         # Check for issues
git add .
git commit -m "Feature X"

# Before pushing
./gradlew codeQuality     # Run all checks

# CI/CD
./gradlew spotlessCheck pmdMain pmdTest
```

---

**Maintain high code quality! 🎯**
