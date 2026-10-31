package com.example.karateframework.karate.runner;

import org.junit.jupiter.api.BeforeEach;

import com.example.karateframework.karate.KarateTestBase;
import com.example.karateframework.karate.TestMarkers;

import io.karatelabs.junit6.Karate;

/**
 * Test runner that executes only Karate scenarios tagged with @smoke using Karate 2.x.
 *
 * <p>Uses @Karate.Test annotation for dynamic test generation with JUnit 6.
 *
 * <p>Useful for quick smoke tests or CI pipelines.
 *
 * <p>Usage: - ./gradlew test --tests SmokeKarateTest - ./gradlew karateTest
 * -Dkarate.options="--tags @smoke"
 */
class SmokeKarateTest extends KarateTestBase {

    @BeforeEach
    void setPort() {
        setPortForKarate();
    }

    @Karate.Test
    Karate runSmokeTests() {
        return Karate.run("classpath:karate/features")
                .tags(TestMarkers.SMOKE)
                .relativeTo(getClass());
    }
}
