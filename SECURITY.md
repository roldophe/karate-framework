# Security & Pre-Commit Hooks

Comprehensive security scanning and automated pre-commit checks for the Karate test automation framework.

---

## Table of Contents
1. [Overview](#overview)
2. [Pre-Commit Hook](#pre-commit-hook)
3. [Security Tools](#security-tools)
4. [Installation](#installation)
5. [Usage Commands](#usage-commands)
6. [Security Reports](#security-reports)
7. [Troubleshooting](#troubleshooting)
8. [CI/CD Integration](#cicd-integration)

---

## Overview

This project includes **four layers of security and quality protection**:

| Layer | Tool | Purpose | Speed |
|-------|------|---------|-------|
| 1️⃣ **Code Formatting** | Spotless | Consistent code style | ⚡ Fast (1-2s) |
| 2️⃣ **Static Analysis** | PMD | Code quality & best practices | ⚡ Fast (2-5s) |
| 3️⃣ **Bug Detection** | SpotBugs | Security vulnerabilities & bugs | 🔸 Medium (5-10s) |
| 4️⃣ **CVE Scanning** | OWASP Dependency-Check | Known vulnerabilities in dependencies | 🔻 Slow (30-60s) |

**Pre-commit hook** automatically runs layers 1-3 before each commit (< 10 seconds).

---

## Pre-Commit Hook

### What It Does

The pre-commit hook **automatically** runs before every `git commit`:

✅ **Auto-formats code** with Spotless (Google Java Style)
✅ **Analyzes code quality** with PMD
✅ **Scans for security bugs** with SpotBugs
✅ **Detects hardcoded secrets** (passwords, API keys, tokens)
✅ **Checks for common issues** (System.out, TODOs)
✅ **Stages formatted files** automatically

### Installation

```bash
# One-time setup
./install-hooks.sh
```

**Output:**
```
════════════════════════════════════════════════════
  Git Hooks Installed Successfully!
════════════════════════════════════════════════════

The following hooks are now active:

  📋 Pre-commit Hook:
     • Automatic code formatting (Spotless)
     • Static code analysis (PMD)
     • Security analysis (SpotBugs)
     • Hardcoded secrets detection
     • Common issue detection
```

### What Happens on Commit

```bash
git commit -m "Add new feature"
```

**Output:**
```
════════════════════════════════════════════════════
  PRE-COMMIT QUALITY & SECURITY CHECKS
════════════════════════════════════════════════════

ℹ Running Spotless code formatter...
✓ Code formatted successfully

ℹ Running PMD static code analysis...
✓ PMD analysis passed

ℹ Running SpotBugs security analysis...
✓ SpotBugs analysis passed

ℹ Checking for common issues...

════════════════════════════════════════════════════
  PRE-COMMIT CHECK SUMMARY
════════════════════════════════════════════════════

✓ All pre-commit checks passed!

Quality Reports:
  • PMD:      build/reports/pmd/main.html
  • SpotBugs: build/reports/spotbugs/main.html
```

### Skip Pre-Commit Checks

**Not recommended, but available for emergencies:**

```bash
# Skip all pre-commit checks
git commit --no-verify -m "Emergency fix"
```

---

## Security Tools

### 1. Spotless - Code Formatting

**Purpose**: Consistent code style across the team

**Configuration**: Google Java Format (AOSP variant)

**Command**:
```bash
./gradlew spotlessApply
```

**What it checks**:
- Indentation (4 spaces)
- Import organization
- Line wrapping
- Trailing whitespace
- Final newlines

### 2. PMD - Static Code Analysis

**Purpose**: Detect code smells and quality issues

**Configuration**: `config/pmd/ruleset.xml`

**Command**:
```bash
./gradlew pmdMain pmdTest
```

**What it checks**:
- Best practices violations
- Code style issues
- Design problems
- Error-prone patterns
- Performance issues

**Report**: `build/reports/pmd/main.html`

### 3. SpotBugs - Security Bug Detection

**Purpose**: Find security vulnerabilities and common bugs

**Configuration**: `config/spotbugs/exclude.xml`

**Command**:
```bash
./gradlew spotbugsMain spotbugsTest
```

**What it checks**:
- Null pointer dereferences
- Resource leaks
- SQL injection vulnerabilities
- XSS vulnerabilities
- Insecure random number generation
- Weak cryptography
- Path traversal issues
- Command injection risks

**Report**: `build/reports/spotbugs/main.html`

**Example Findings**:

```java
// ❌ HIGH: SQL Injection vulnerability
String query = "SELECT * FROM users WHERE id = " + userId;

// ✅ FIXED: Use prepared statements
PreparedStatement stmt = conn.prepareStatement("SELECT * FROM users WHERE id = ?");
stmt.setString(1, userId);
```

```java
// ❌ HIGH: Hardcoded cryptographic key
byte[] key = "1234567890123456".getBytes();

// ✅ FIXED: Load from secure configuration
byte[] key = getKeyFromSecureVault();
```

### 4. OWASP Dependency-Check - CVE Scanning

**Purpose**: Detect known vulnerabilities (CVEs) in dependencies

**Configuration**: `config/dependency-check/suppressions.xml`

**Command**:
```bash
./gradlew dependencyCheckAnalyze
```

**What it checks**:
- Scans all dependencies against NVD database
- Identifies known CVEs
- Reports CVSS severity scores
- Checks transitive dependencies

**Report**: `build/reports/dependency-check-report.html`

**Example Report**:

| Dependency | CVE | Severity | CVSS |
|-----------|-----|----------|------|
| spring-web:5.3.10 | CVE-2021-22060 | HIGH | 7.5 |
| jackson-databind:2.12.3 | CVE-2021-20190 | CRITICAL | 9.8 |

**Auto-fails build** if CVSS >= 7.0 (configurable in `build.gradle.kts`)

---

## Installation

### 1. Install Pre-Commit Hook

```bash
# Run installation script
./install-hooks.sh
```

### 2. Get NVD API Key (Optional - Recommended)

For faster CVE database updates:

1. Register at: https://nvd.nist.gov/developers/request-an-api-key
2. Set environment variable:

```bash
# Add to ~/.bashrc or ~/.zshrc
export NVD_API_KEY="your-api-key-here"
```

### 3. Verify Installation

```bash
# Check hook is installed
ls -la .git/hooks/pre-commit

# Should show:
# -rwxr-xr-x  1 user  staff  5432  pre-commit
```

---

## Usage Commands

### Quick Reference

```bash
# Pre-commit checks (fast - runs automatically)
./gradlew preCommitCheck

# Full security scan (slow - includes CVE scanning)
./gradlew fullSecurityCheck

# Individual tools
./gradlew spotlessApply          # Format code
./gradlew pmdMain                # Static analysis
./gradlew spotbugsMain           # Bug detection
./gradlew dependencyCheckAnalyze # CVE scan
```

### Daily Development Workflow

```bash
# 1. Write code
vim src/main/java/MyClass.java

# 2. Commit (pre-commit hook runs automatically)
git add .
git commit -m "Add new feature"

# Pre-commit hook will:
# - Format your code
# - Run PMD analysis
# - Run SpotBugs security scan
# - Check for hardcoded secrets
# - Stage formatted files
# - Block commit if issues found

# 3. Fix any issues if checks fail
# Open reports: build/reports/spotbugs/main.html

# 4. Retry commit
git commit -m "Add new feature"
```

### Weekly Security Scan

```bash
# Run comprehensive security check (includes CVE scan)
./gradlew fullSecurityCheck

# Check reports
open build/reports/dependency-check-report.html
open build/reports/spotbugs/main.html
```

### Before Production Deployment

```bash
# Run all checks
./gradlew clean fullSecurityCheck

# Verify all reports
open build/reports/dependency-check-report.html
open build/reports/spotbugs/main.html
open build/reports/pmd/main.html
```

---

## Security Reports

### SpotBugs Report

**Location**: `build/reports/spotbugs/main.html`

**Sample Output**:

```
Security Bugs: 0
Correctness Issues: 2
Performance Issues: 1
Dodgy Code: 3

Details:
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
Bug: NP_NULL_ON_SOME_PATH
Class: UserController.java:45
Priority: Medium
Message: Possible null pointer dereference
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
```

### OWASP Dependency-Check Report

**Location**: `build/reports/dependency-check-report.html`

**Sample Output**:

```
Summary
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
Total Dependencies: 45
Vulnerable Dependencies: 2
Critical: 1
High: 1
Medium: 0
Low: 0

Vulnerabilities
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
spring-web-5.3.10.jar

  CVE-2021-22060 (CRITICAL - CVSS: 9.8)
  Description: Remote Code Execution vulnerability
  Recommendation: Upgrade to 5.3.23 or later

  CVE-2021-22096 (HIGH - CVSS: 7.5)
  Description: Denial of Service vulnerability
  Recommendation: Upgrade to 5.3.18 or later
```

### Suppressing False Positives

**For SpotBugs**:

Edit `config/spotbugs/exclude.xml`:

```xml
<Match>
    <Class name="com.example.MyClass"/>
    <Bug pattern="NP_NULL_ON_SOME_PATH"/>
</Match>
```

**For OWASP**:

Edit `config/dependency-check/suppressions.xml`:

```xml
<suppress>
    <notes>False positive - different artifact</notes>
    <gav regex="true">com.example:my-lib:.*</gav>
    <cve>CVE-2021-12345</cve>
</suppress>
```

---

## Troubleshooting

### Pre-Commit Hook Not Running

**Problem**: Commits succeed without running checks

**Solution**:
```bash
# Reinstall hook
./install-hooks.sh

# Verify installation
ls -la .git/hooks/pre-commit

# Check it's executable
chmod +x .git/hooks/pre-commit
```

### SpotBugs Fails to Find Issues

**Problem**: SpotBugs reports errors about missing files

**Solution**:
```bash
# Clean and rebuild
./gradlew clean compileJava compileTestJava

# Run SpotBugs again
./gradlew spotbugsMain
```

### OWASP Scan is Very Slow

**Problem**: CVE scan takes 5+ minutes

**Solutions**:

1. **Get NVD API Key** (recommended):
```bash
export NVD_API_KEY="your-key"
./gradlew dependencyCheckAnalyze
```

2. **Use cached data**:
```bash
# First run downloads database (~60s)
# Subsequent runs use cache (~10s)
./gradlew dependencyCheckAnalyze
```

3. **Disable for pre-commit** (already disabled by default):
```bash
# CVE scan commented out in hooks/pre-commit
# Run manually when needed
./gradlew dependencyCheckAnalyze
```

### Hardcoded Secret Detection False Positives

**Problem**: Hook detects test data as secrets

**Solution**:

Edit `hooks/pre-commit` and adjust patterns:

```bash
# Current patterns
SECRETS_PATTERNS=(
    'password\s*=\s*["\047]'
    'api[_-]?key\s*=\s*["\047]'
)

# Add exclusions for test files
if echo "$file" | grep -q "test"; then
    continue  # Skip test files
fi
```

### Build Fails on CVE with Score < 7

**Problem**: Want stricter CVE threshold

**Solution**:

Edit `build.gradle.kts`:

```kotlin
dependencyCheck {
    // Change from 7.0 to 5.0 for stricter checks
    failBuildOnCVSS = 5.0f
}
```

---

## CI/CD Integration

### GitHub Actions

Create `.github/workflows/security.yml`:

```yaml
name: Security Scan

on:
  push:
    branches: [ main, develop ]
  pull_request:
    branches: [ main ]
  schedule:
    # Run weekly CVE scan
    - cron: '0 0 * * 0'

jobs:
  security:
    runs-on: ubuntu-latest

    steps:
      - uses: actions/checkout@v4

      - name: Set up JDK 21
        uses: actions/setup-java@v4
        with:
          java-version: '21'
          distribution: 'temurin'

      - name: Grant execute permission
        run: chmod +x gradlew

      - name: Run Security Checks
        env:
          NVD_API_KEY: ${{ secrets.NVD_API_KEY }}
        run: ./gradlew fullSecurityCheck

      - name: Upload SpotBugs Report
        if: always()
        uses: actions/upload-artifact@v4
        with:
          name: spotbugs-report
          path: build/reports/spotbugs/

      - name: Upload OWASP Report
        if: always()
        uses: actions/upload-artifact@v4
        with:
          name: dependency-check-report
          path: build/reports/dependency-check-report.html

      - name: Fail on High Severity
        if: failure()
        run: echo "Security vulnerabilities found! Check reports."
```

### GitLab CI

Add to `.gitlab-ci.yml`:

```yaml
security_scan:
  stage: test
  image: gradle:8-jdk21
  script:
    - ./gradlew fullSecurityCheck
  artifacts:
    when: always
    paths:
      - build/reports/spotbugs/
      - build/reports/dependency-check-report.html
    reports:
      junit: build/reports/spotbugs/*.xml
  only:
    - merge_requests
    - main
    - develop

weekly_cve_scan:
  stage: test
  image: gradle:8-jdk21
  script:
    - ./gradlew dependencyCheckAnalyze
  artifacts:
    paths:
      - build/reports/dependency-check-report.html
  only:
    - schedules
```

---

## Best Practices

### 1. Run Pre-Commit Checks

Always let the pre-commit hook run:
- ✅ Catches issues early
- ✅ Keeps code quality high
- ✅ Prevents security issues

### 2. Weekly CVE Scans

Schedule weekly dependency scans:
```bash
# Add to crontab
0 9 * * 1 cd /path/to/project && ./gradlew dependencyCheckAnalyze
```

### 3. Review Security Reports

Don't ignore warnings:
- **High/Critical**: Fix immediately
- **Medium**: Fix before merge
- **Low**: Fix during refactoring

### 4. Keep Tools Updated

Update security tools regularly:

```kotlin
// In build.gradle.kts
id("com.github.spotbugs") version "6.0.26"  // Check for latest
id("org.owasp.dependencycheck") version "11.1.1"  // Check for latest
```

### 5. Suppress Wisely

Only suppress false positives:
- Document why it's suppressed
- Add links to tickets/issues
- Review suppressions quarterly

---

## Summary

### Quick Commands

| Task | Command |
|------|---------|
| Install hooks | `./install-hooks.sh` |
| Pre-commit checks | `./gradlew preCommitCheck` |
| Full security scan | `./gradlew fullSecurityCheck` |
| Format code | `./gradlew spotlessApply` |
| Static analysis | `./gradlew pmdMain` |
| Bug detection | `./gradlew spotbugsMain` |
| CVE scan | `./gradlew dependencyCheckAnalyze` |
| Skip pre-commit | `git commit --no-verify` |

### Security Layers

1. ⚡ **Pre-commit** (auto): Format + PMD + SpotBugs (< 10s)
2. 🔍 **Daily** (manual): Same as pre-commit
3. 📊 **Weekly** (scheduled): Full security + CVE scan
4. 🚀 **Pre-deployment** (manual): Comprehensive checks

---

**Stay secure! 🔒**
