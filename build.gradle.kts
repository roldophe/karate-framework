plugins {
    java
    id("org.springframework.boot") version "4.0.5"
    id("io.spring.dependency-management") version "1.1.7"
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