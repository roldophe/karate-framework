Feature: External API Integration Testing

  Background:
    * url baseUrl
    * def externalApiUrl = 'http://localhost:1080'

  @smoke @external @get
  Scenario: Get external user via integration endpoint
    Given path '/api/external-integration/users/123'
    When method GET
    Then status 200
    And match response.id == 'ext-123'
    And match response.name == 'External User'
    And match response.email == 'external@example.com'
    And match response.source == 'external-api'

  @smoke @external @post
  Scenario: Validate data via external API
    Given path '/api/external-integration/validate'
    And request
      """
      {
        "data": "test-data"
      }
      """
    When method POST
    Then status 200
    And match response.valid == true
    And match response.message == 'Validation successful'

  @external @direct
  Scenario: Direct call to mock external API - Get user
    Given url externalApiUrl
    And path '/api/external/users/456'
    When method GET
    Then status 200
    And match response.id == 'ext-123'
    And match response.source == 'external-api'

  @external @direct
  Scenario: Direct call to mock external API - Validate data
    Given url externalApiUrl
    And path '/api/external/validate'
    And request
      """
      {
        "data": "validation-test"
      }
      """
    When method POST
    Then status 200
    And match response.valid == true

  @external @error
  Scenario: Handle external API errors gracefully
    # This scenario would test error handling
    # For now, we assume the integration endpoint handles errors
    Given path '/api/external-integration/users/non-existent'
    When method GET
    # The external API should return an error, which our controller catches
    Then status 500
