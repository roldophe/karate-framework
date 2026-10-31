function fn() {
    // Read environment (default: local)
    var env = karate.env || 'local';
    karate.log('karate.env system property was:', env);

    // Base configuration
    var config = {
        env: env,
        connectTimeout: 30000,
        readTimeout: 30000
    };

    // Environment-specific configuration
    if (env === 'local') {
        // Local development - Spring Boot running on random port
        var port = karate.properties['local.server.port'] || '8080';
        config.baseUrl = 'http://localhost:' + port;
        karate.log('Local mode: baseUrl =', config.baseUrl);
    } else if (env === 'dev') {
        // Development environment
        config.baseUrl = 'https://api-dev.example.com';
    } else if (env === 'uat') {
        // UAT/Staging environment
        config.baseUrl = 'https://api-uat.example.com';
    } else if (env === 'prod') {
        // Production environment
        config.baseUrl = 'https://api.example.com';
    }

    // Common headers
    var uuid = java.util.UUID.randomUUID() + '';
    config.headers = {
        'Content-Type': 'application/json',
        'Accept': 'application/json',
        'X-Request-ID': uuid
    };

    // Authentication (optional)
    // Uncomment to fetch token once per test run using callSingle
    // var authToken = karate.callSingle('classpath:karate/common/auth/get-token.feature', config).authToken;
    // config.authToken = authToken;
    // config.headers['Authorization'] = 'Bearer ' + authToken;

    // Read sensitive data from environment variables or system properties
    // NEVER hard-code secrets in this file
    config.apiKey = karate.properties['api.key'] || java.lang.System.getenv('API_KEY');
    config.testUser = {
        username: karate.properties['test.username'] || java.lang.System.getenv('TEST_USERNAME') || 'testuser',
        password: karate.properties['test.password'] || java.lang.System.getenv('TEST_PASSWORD') || 'testpass'
    };

    karate.configure('connectTimeout', config.connectTimeout);
    karate.configure('readTimeout', config.readTimeout);
    karate.configure('ssl', true); // Allow self-signed certificates in test environments

    karate.log('Configuration:', config);
    return config;
}
