package com.example.karateframework.karate.runner;

import org.junit.jupiter.api.Test;

import com.example.karateframework.karate.KarateTestBase;
import com.example.karateframework.karate.TestMarkers;
import com.intuit.karate.Results;

/**
 * Test runner that executes ONLY smoke tests in parallel. Smoke tests are tagged with @smoke and
 * represent critical path scenarios.
 *
 * <p>Usage: - ./gradlew karateTest --tests SmokeKarateTest - ./gradlew karateTest -Dkarate.env=dev
 * --tests SmokeKarateTest
 */
class SmokeKarateTest extends KarateTestBase {

    @Test
    void runSmokeTests() {
        Results results =
                runKarate(
                        "classpath:karate/features", TestMarkers.SMOKE, 3, "build/karate-reports");

        assertKaratePassed(results);
    }
}
