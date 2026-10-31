package com.example.karateframework.karate;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeAll;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import com.intuit.karate.Results;
import com.intuit.karate.Runner;

/**
 * Base class for Karate tests. Handles Spring Boot application startup and Karate configuration.
 *
 * <p>Supports two modes based on karate.env: - local: Starts Spring Boot with random port -
 * dev/uat/prod: Uses external URL (no Spring Boot startup)
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
            // In local mode, we're using @SpringBootTest with random port
            System.out.println("Running in LOCAL mode - Spring Boot will start on random port");
        } else {
            System.out.println(
                    "Running in " + karateEnv.toUpperCase() + " mode - using external URL");
        }
    }

    /**
     * Get the port assigned by Spring Boot. Subclasses should call this before running Karate
     * tests.
     */
    protected int getPort() {
        return port;
    }

    /**
     * Helper method to build and run Karate tests with standard configuration.
     *
     * @param path Classpath path to feature files (e.g., "classpath:karate/features")
     * @param tags JUnit/Karate tags to filter scenarios (e.g., "@smoke", "~@ignore")
     * @param parallel Number of parallel threads (1 = sequential)
     * @param outputDir Directory for Karate reports
     * @return Karate Results object
     */
    protected Results runKarate(String path, String tags, int parallel, String outputDir) {
        Runner.Builder<?> builder = Runner.path(path);

        if (tags != null && !tags.isEmpty()) {
            builder = builder.tags(tags);
        }

        if (outputDir != null && !outputDir.isEmpty()) {
            builder =
                    builder.outputCucumberJson(true)
                            .outputJunitXml(true)
                            .outputHtmlReport(true)
                            .reportDir(outputDir);
        }

        // Set system property for local server port if in local mode
        String karateEnv = System.getProperty("karate.env", "local");
        if ("local".equals(karateEnv) && port > 0) {
            builder = builder.systemProperty("local.server.port", String.valueOf(port));
        }

        // parallel() is a terminal operation that executes tests and returns Results
        return builder.parallel(parallel);
    }

    /**
     * Assert that Karate tests passed (no failures).
     *
     * @param results Karate Results from test execution
     */
    protected void assertKaratePassed(Results results) {
        assertEquals(
                0, results.getFailCount(), "Karate tests failed:\n" + results.getErrorMessages());
    }

    /**
     * Convenience method to run Karate tests with default parallel=5 and standard report dir.
     *
     * @param path Classpath path to feature files
     * @param tags Tags to filter scenarios
     * @return Karate Results
     */
    protected Results runKarate(String path, String tags) {
        return runKarate(path, tags, 5, "build/karate-reports");
    }

    /**
     * Run all features in a path with no tag filtering.
     *
     * @param path Classpath path to feature files
     * @return Karate Results
     */
    protected Results runKarate(String path) {
        return runKarate(path, null);
    }
}
