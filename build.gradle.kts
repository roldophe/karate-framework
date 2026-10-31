plugins {
    java
    id("org.springframework.boot") version "4.0.5"
    id("io.spring.dependency-management") version "1.1.7"
    id("com.diffplug.spotless") version "6.25.0"
    id("pmd")
    id("com.github.spotbugs") version "6.0.26"
    id("org.owasp.dependencycheck") version "11.1.1"
}

group = "com.example"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    // Spring Boot
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-actuator")

    // Test dependencies
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        // Spring Boot 4.0 uses JUnit 6, but Karate 1.5.x requires JUnit 5
        // Exclude JUnit 6 to prevent conflicts
        exclude(group = "org.junit.jupiter", module = "junit-jupiter")
        exclude(group = "org.junit", module = "junit-bom")
    }

    // Karate Testing Framework (uses JUnit 5)
    // Latest stable version: 1.5.1 (groupId changed from com.intuit.karate to io.karatelabs in 1.5.0)
    testImplementation("io.karatelabs:karate-junit5:1.5.1")

    // Explicitly add JUnit 5 dependencies to override Spring Boot 4's JUnit 6
    testImplementation(platform("org.junit:junit-bom:5.11.4"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.withType<Test> {
    useJUnitPlatform()

    // Pass system properties to test JVM
    systemProperty("karate.env", System.getProperty("karate.env", "local"))
    systemProperty("karate.options", System.getProperty("karate.options", ""))

    // Always run tests (don't use cached results)
    outputs.upToDateWhen { false }

    doLast {
        println("\n===========================================")
        println("Karate Test Report:")
        println("${project.layout.buildDirectory.get().asFile}/karate-reports/karate-summary.html")
        println("===========================================\n")
    }
}

// Separate task for unit tests (excluding Karate runners)
tasks.named<Test>("test") {
    filter {
        excludeTestsMatching("*KarateTest")
    }
}

// Dedicated task for Karate tests
val karateTest = tasks.register<Test>("karateTest") {
    description = "Runs Karate API tests"
    group = "verification"

    // Ensure test classes are compiled before running
    dependsOn(tasks.named("compileTestJava"))
    dependsOn(tasks.named("processTestResources"))

    // Use the test source set classpath
    testClassesDirs = sourceSets.test.get().output.classesDirs
    classpath = sourceSets.test.get().runtimeClasspath

    useJUnitPlatform()

    filter {
        includeTestsMatching("*KarateTest")
    }

    // Pass CLI properties to test runtime
    systemProperty("karate.env", System.getProperty("karate.env", "local"))
    systemProperty("karate.options", System.getProperty("karate.options", ""))

    // Always run (never use cached results)
    outputs.upToDateWhen { false }

    doLast {
        println("\n===========================================")
        println("Karate Test Report:")
        println("${project.layout.buildDirectory.get().asFile}/karate-reports/karate-summary.html")
        println("===========================================\n")
    }
}

// Ensure test resources from src/test/java are included (optional)
sourceSets {
    test {
        resources {
            srcDir("src/test/java")
            exclude("**/*.java")
        }
    }
}

// =====================================
// Code Quality & Formatting
// =====================================

// Spotless - Code Formatting
spotless {
    java {
        // Use Google Java Format
        googleJavaFormat("1.19.2").aosp().reflowLongStrings()

        // Import order
        importOrder("java", "javax", "org", "com", "")
        removeUnusedImports()

        // Format feature files as well
        target("src/**/*.java")
        targetExclude("build/**")

        // Trailing whitespace
        trimTrailingWhitespace()
        endWithNewline()

        // License header (optional)
        // licenseHeaderFile("config/spotless/license-header.txt")
    }

    // Kotlin Gradle formatting disabled due to ktlint compatibility issues with Gradle 9.x
    // kotlinGradle {
    //     target("*.gradle.kts")
    //     ktlint()
    // }

    format("misc") {
        target("**/*.md", "**/.gitignore", "**/*.yaml", "**/*.yml", "**/*.json")
        targetExclude("build/**", ".gradle/**")
        trimTrailingWhitespace()
        indentWithSpaces(2)
        endWithNewline()
    }
}

// PMD - Static Code Analysis
pmd {
    isConsoleOutput = true
    toolVersion = "7.0.0"
    rulesMinimumPriority = 5
    ruleSetFiles = files("config/pmd/ruleset.xml")

    // Analyze both main and test code
    sourceSets = listOf(project.sourceSets.main.get(), project.sourceSets.test.get())
}

tasks.withType<Pmd> {
    reports {
        xml.required = true
        html.required = true
    }
}

// Custom task to run all code quality checks
tasks.register("codeQuality") {
    description = "Runs all code quality checks (Spotless, PMD)"
    group = "verification"

    dependsOn("spotlessCheck", "pmdMain", "pmdTest")
}

// Custom task to format code and run checks
tasks.register("formatAndCheck") {
    description = "Formats code with Spotless and runs quality checks"
    group = "verification"

    dependsOn("spotlessApply", "pmdMain", "pmdTest")
}

// =====================================
// Security Analysis
// =====================================

// SpotBugs - Find bugs and security vulnerabilities
spotbugs {
    toolVersion = "4.8.6"
    effort = com.github.spotbugs.snom.Effort.MAX
    reportLevel = com.github.spotbugs.snom.Confidence.MEDIUM

    // Exclude test code from security analysis (optional)
    // sourceSets = listOf(project.sourceSets.main.get())
}

tasks.withType<com.github.spotbugs.snom.SpotBugsTask> {
    reports {
        create("html") {
            required = true
            outputLocation = file("${layout.buildDirectory.get()}/reports/spotbugs/${this.name}.html")
        }
        create("xml") {
            required = true
            outputLocation = file("${layout.buildDirectory.get()}/reports/spotbugs/${this.name}.xml")
        }
    }

    // Exclude test files from SpotBugs analysis (they have different patterns)
    excludeFilter = file("config/spotbugs/exclude.xml")
}

// OWASP Dependency-Check - CVE vulnerability scanning
dependencyCheck {
    // Scan all configurations
    scanConfigurations = listOf("runtimeClasspath", "testRuntimeClasspath")

    // Report format
    formats = listOf("HTML", "JSON", "XML")

    // Fail build on CVSS score >= 7 (High severity)
    failBuildOnCVSS = 7.0f

    // Suppress false positives
    suppressionFile = "config/dependency-check/suppressions.xml"

    // NVD API configuration (optional - faster updates)
    nvd {
        // Get API key from: https://nvd.nist.gov/developers/request-an-api-key
        apiKey = System.getenv("NVD_API_KEY") ?: ""
        delay = 2000 // milliseconds between requests
    }

    // Analyzers configuration
    analyzers {
        assemblyEnabled = false
        nuspecEnabled = false
        nodeEnabled = false
    }
}

// Custom task for all security checks
tasks.register("securityCheck") {
    description = "Runs all security checks (SpotBugs + OWASP Dependency-Check)"
    group = "verification"

    dependsOn("spotbugsMain", "spotbugsTest", "dependencyCheckAnalyze")
}

// Custom task for pre-commit checks (fast)
tasks.register("preCommitCheck") {
    description = "Fast pre-commit checks (Spotless + PMD + SpotBugs)"
    group = "verification"

    dependsOn("spotlessApply", "pmdMain", "spotbugsMain")
}

// Custom task for comprehensive checks (includes slow CVE scan)
tasks.register("fullSecurityCheck") {
    description = "Comprehensive security and quality checks (includes CVE scanning)"
    group = "verification"

    dependsOn("spotlessCheck", "pmdMain", "pmdTest", "spotbugsMain", "spotbugsTest", "dependencyCheckAnalyze")
}