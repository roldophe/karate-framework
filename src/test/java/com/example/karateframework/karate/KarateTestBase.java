package com.example.karateframework.karate;

import org.junit.jupiter.api.BeforeAll;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

/**
 * Base class for Karate tests using Karate 2.x with JUnit 6.
 *
 * <p>Handles Spring Boot application startup and port configuration for Karate tests.
 *
 * <p>Supports two modes based on karate.env: - local: Starts Spring Boot with random port -
 * dev/uat/prod: Uses external URL (no Spring Boot startup)
 *
 * <p>Note: Karate 2.x uses @Karate annotation with @TestFactory for dynamic test generation.
 * Subclasses should use @Karate.Test annotation to define test suites.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class KarateTestBase {

    @LocalServerPort private int port;

    /**
     * Set the local server port as a system property before tests run. This allows karate-config.js
     * to read it.
     */
    @BeforeAll
    public static void beforeAll() {
        String karateEnv = System.getProperty("karate.env", "local");

        if ("local".equals(karateEnv)) {
            System.out.println("Running in LOCAL mode - Spring Boot will start on random port");
        } else {
            System.out.println(
                    "Running in " + karateEnv.toUpperCase() + " mode - using external URL");
        }
    }

    /**
     * Get the port assigned by Spring Boot. This is used by Karate tests to configure the base URL.
     *
     * <p>In Karate 2.x, the port is automatically passed via system properties set before test
     * execution.
     */
    protected int getPort() {
        return port;
    }

    /**
     * Set the server port as a system property before running Karate tests. This method should be
     * called from a @BeforeEach method in subclasses to ensure the port is available to Karate.
     */
    protected void setPortForKarate() {
        String karateEnv = System.getProperty("karate.env", "local");
        if ("local".equals(karateEnv) && port > 0) {
            System.setProperty("local.server.port", String.valueOf(port));
        }
    }
}
