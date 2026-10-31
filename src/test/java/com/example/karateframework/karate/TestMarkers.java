package com.example.karateframework.karate;

/** Constants for Karate test tags. Use these in test runners instead of hard-coded strings. */
public final class TestMarkers {

    private TestMarkers() {
        // Prevent instantiation
    }

    /**
     * Smoke tests - critical path scenarios that must always pass. Run before merging code or
     * deploying.
     */
    public static final String SMOKE = "@smoke";

    /** Regression tests - comprehensive test suite. Run periodically or in nightly builds. */
    public static final String REGRESSION = "@regression";

    /** Integration tests - tests that require external systems. */
    public static final String INTEGRATION = "@integration";

    /** Ignore marker - skip these tests. Use for WIP features or known issues. */
    public static final String IGNORE = "~@ignore";

    /** Performance tests - scenarios that test performance characteristics. */
    public static final String PERFORMANCE = "@performance";

    /** Security tests - scenarios that test security aspects. */
    public static final String SECURITY = "@security";

    /** Default test filter: run everything except ignored tests. */
    public static final String DEFAULT_FILTER = IGNORE;
}
