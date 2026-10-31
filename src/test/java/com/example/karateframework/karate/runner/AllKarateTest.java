package com.example.karateframework.karate.runner;

import org.junit.jupiter.api.BeforeEach;

import com.example.karateframework.karate.KarateTestBase;

import io.karatelabs.junit6.Karate;

/**
 * Test runner that executes ALL Karate feature files using Karate 2.x.
 *
 * <p>Uses @Karate.Test annotation for dynamic test generation with JUnit 6.
 *
 * <p>Excludes scenarios tagged with @ignore.
 *
 * <p>Usage: - ./gradlew karateTest - ./gradlew karateTest -Dkarate.env=dev - ./gradlew karateTest
 * -Dkarate.options="--tags @regression"
 */
class AllKarateTest extends KarateTestBase {

    @BeforeEach
    void setPort() {
        setPortForKarate();
    }

    @Karate.Test
    Karate runAllKarateTests() {
        return Karate.run("classpath:karate/features").tags("~@ignore").relativeTo(getClass());
    }
}
