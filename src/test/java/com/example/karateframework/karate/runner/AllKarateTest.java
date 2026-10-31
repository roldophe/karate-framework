package com.example.karateframework.karate.runner;

import com.example.karateframework.karate.KarateTestBase;
import com.example.karateframework.karate.TestMarkers;
import com.intuit.karate.Results;
import org.junit.jupiter.api.Test;

/**
 * Test runner that executes ALL Karate feature files in parallel.
 * Excludes scenarios tagged with @ignore.
 *
 * Usage:
 * - ./gradlew karateTest
 * - ./gradlew karateTest -Dkarate.env=dev
 * - ./gradlew karateTest -Dkarate.options="--tags @regression"
 */
class AllKarateTest extends KarateTestBase {

    @Test
    void runAllKarateTests() {
        Results results = runKarate(
            "classpath:karate/features",
            TestMarkers.IGNORE,
            5,
            "build/karate-reports"
        );

        assertKaratePassed(results);
    }
}
