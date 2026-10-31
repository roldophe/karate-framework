/**
 * Common utility functions for Karate tests.
 * Can be called from feature files using karate.call().
 */

function fn() {
    var utils = {};

    /**
     * Generate a random email address
     */
    utils.randomEmail = function() {
        var uuid = java.util.UUID.randomUUID() + '';
        return 'test-' + uuid.substring(0, 8) + '@example.com';
    };

    /**
     * Generate a random username
     */
    utils.randomUsername = function() {
        var uuid = java.util.UUID.randomUUID() + '';
        return 'user_' + uuid.substring(0, 8);
    };

    /**
     * Generate a random string of specified length
     */
    utils.randomString = function(length) {
        var chars = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789';
        var result = '';
        for (var i = 0; i < length; i++) {
            result += chars.charAt(Math.floor(Math.random() * chars.length));
        }
        return result;
    };

    /**
     * Get current timestamp
     */
    utils.timestamp = function() {
        return java.lang.System.currentTimeMillis();
    };

    /**
     * Sleep for specified milliseconds
     */
    utils.sleep = function(millis) {
        java.lang.Thread.sleep(millis);
    };

    return utils;
}
