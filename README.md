# Karate API Testing Framework

A production-ready API testing framework built with **Karate 2.1.4**, **Spring Boot 4.1.0**, and **Java 21**.

## 🚀 Quick Start

```bash
# Run all tests
./gradlew karateTest

# Run smoke tests only
./gradlew karateTest --tests SmokeKarateTest

# View test report
open build/karate-reports/karate-summary.html
```

## 📋 Features

- ✅ **Karate 2.1.4** - Latest Karate with JUnit 6 support
- ✅ **Spring Boot 4.1.0** - Modern Spring Boot integration
- ✅ **Java 21** - Latest LTS Java version
- ✅ **Code Quality** - Spotless, PMD, SpotBugs, OWASP Dependency-Check
- ✅ **Multiple Environments** - Local, Dev, UAT, Prod configurations
- ✅ **Parallel Execution** - Fast test execution
- ✅ **Comprehensive Reporting** - Detailed HTML test reports

## 📚 Documentation

- **[Quick Start Guide](QUICK_START.md)** - Get started in 5 minutes
- **[Karate Learning Guide](KARATE_LEARNING_GUIDE.md)** - Comprehensive tutorial
- **[Code Quality](CODE_QUALITY.md)** - Formatting and static analysis
- **[Security](SECURITY.md)** - Security scanning and pre-commit hooks

## 🛠️ Tech Stack

| Technology | Version | Purpose |
|------------|---------|---------|
| Karate | 2.1.4 | API testing framework |
| Spring Boot | 4.1.2 | Application framework |
| Java | 21 | Programming language |
| Gradle | 9.8.2+ | Build tool |
| JUnit | 6 | Test framework |
| Spotless | 8.10.4 | Code formatting |
| PMD | 7.22.0 | Static code analysis |
| SpotBugs | 4.9.8 | Security analysis |
| OWASP Dependency-Check | 13.0.0 | CVE scanning |

## 🏗️ Project Structure

```
karate-framework/
├── src/
│   ├── main/java/                      # Application code
│   └── test/
│       ├── java/                       # Test infrastructure
│       │   └── karate/
│       │       ├── KarateTestBase.java
│       │       ├── TestMarkers.java
│       │       └── runner/
│       │           ├── AllKarateTest.java
│       │           └── SmokeKarateTest.java
│       └── resources/
│           ├── karate-config.js        # Environment config
│           ├── logback-test.xml        # Logging config
│           └── karate/
│               ├── features/           # Test scenarios
│               ├── common/             # Reusable features
│               └── data/               # Test data
├── config/
│   ├── pmd/ruleset.xml                # PMD rules
│   ├── spotbugs/exclude.xml           # SpotBugs exclusions
│   └── dependency-check/suppressions.xml
├── build.gradle.kts                   # Build configuration
└── README.md
```

## 🧪 Running Tests

### All Tests
```bash
./gradlew karateTest
```

### Smoke Tests Only
```bash
./gradlew karateTest --tests SmokeKarateTest
```

### Specific Tag
```bash
./gradlew karateTest -Dkarate.options="--tags @smoke"
```

### Different Environment
```bash
./gradlew karateTest -Dkarate.env=dev
```

## 🎯 Code Quality

### Format Code
```bash
./gradlew spotlessApply
```

### Run Static Analysis
```bash
./gradlew pmdMain pmdTest
```

### Security Scan
```bash
./gradlew spotbugsMain spotbugsTest
```

### CVE Scan
```bash
./gradlew dependencyCheckAnalyze
```

### All Quality Checks
```bash
./gradlew codeQuality
```

## 🔧 Development Workflow

1. **Write your test** in `src/test/resources/karate/features/`
2. **Run locally**: `./gradlew karateTest`
3. **Format code**: `./gradlew spotlessApply`
4. **Check quality**: `./gradlew codeQuality`
5. **Commit changes**

## 📦 Gradle Tasks

| Task | Description |
|------|-------------|
| `karateTest` | Run all Karate tests |
| `test` | Run unit tests (excludes Karate) |
| `spotlessApply` | Format all code |
| `spotlessCheck` | Check code formatting |
| `pmdMain` | Run PMD on main code |
| `pmdTest` | Run PMD on test code |
| `spotbugsMain` | Run SpotBugs security analysis |
| `dependencyCheckAnalyze` | Scan for CVE vulnerabilities |
| `codeQuality` | Run all quality checks |
| `preCommitCheck` | Fast pre-commit checks |
| `fullSecurityCheck` | Comprehensive security scan |

## 🌍 Environment Configuration

The framework supports multiple environments via `karate.env` system property:

```bash
# Local (default) - embedded Spring Boot
./gradlew karateTest

# Dev environment
./gradlew karateTest -Dkarate.env=dev

# UAT environment
./gradlew karateTest -Dkarate.env=uat

# Production environment
./gradlew karateTest -Dkarate.env=prod
```

Environment-specific configuration is in `src/test/resources/karate-config.js`.

## 📊 Test Reports

After running tests, view the HTML report:

```bash
open build/karate-reports/karate-summary.html
```

The report includes:
- Test execution summary
- Pass/fail status
- Request/response details
- Execution timeline
- Feature and scenario details

## 🔐 Security Features

### Local Security (Pre-commit)
- **Pre-commit hooks** - Automated quality and security checks
- **SpotBugs** - Security vulnerability detection
- **OWASP Dependency-Check** - CVE scanning
- **Secret detection** - Prevent committing sensitive data
- **PMD security rules** - Security best practices

### CI/CD Security (GitHub Actions)
- **CodeQL Analysis** - Security vulnerability scanning (security-extended queries)
- **Dependency Review** - Blocks HIGH severity CVEs on PRs
- **Automated Builds** - Validates every PR and push to main
- **Dependabot** - Weekly automated dependency updates

**Security Status**: [![Security Scan](https://github.com/roldophe/karate-framework/actions/workflows/pr-security-scan.yml/badge.svg)](https://github.com/roldophe/karate-framework/actions/workflows/pr-security-scan.yml)

See [SECURITY.md](SECURITY.md) for complete details.

## 🧑‍💻 IDE Setup

### IntelliJ IDEA
1. Install **Google Java Format** plugin
2. Enable **EditorConfig** support (auto-enabled)
3. Optional: Install **PMD** plugin

### VS Code
1. Install **Extension Pack for Java**
2. Install **EditorConfig** extension
3. Install **Spotless Gradle** extension

## 📖 Learning Resources

- **Official Karate Docs**: https://karatelabs.github.io/karate/
- **GitHub**: https://github.com/karatelabs/karate
- **Quick Start**: [QUICK_START.md](QUICK_START.md)
- **Full Guide**: [KARATE_LEARNING_GUIDE.md](KARATE_LEARNING_GUIDE.md)

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Write tests
4. Run `./gradlew codeQuality`
5. Submit a pull request

## 📝 License

[Add your license here]

## ✨ What's New in Karate 2.1.4

- **JUnit 6 Support** - Aligns with Spring Boot 4.x
- **Improved Performance** - Faster test execution
- **Better Reporting** - Enhanced HTML reports
- **Java 21 Support** - Fully compatible with latest Java LTS

## 🆘 Getting Help

- Check [QUICK_START.md](QUICK_START.md) for common tasks
- Review [KARATE_LEARNING_GUIDE.md](KARATE_LEARNING_GUIDE.md) for detailed examples
- See test reports: `build/karate-reports/karate-summary.html`
- Visit official docs: https://karatelabs.github.io/karate/

---

**Happy Testing! 🥋**
