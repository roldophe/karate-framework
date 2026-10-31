Feature: Health Check API
  Verify that the application is up and running

  Background:
    * url baseUrl

  @smoke
  Scenario: Health endpoint returns UP status
    Given path '/actuator/health'
    When method GET
    Then status 200
    And match response.status == 'UP'
    And match response contains { status: '#string', components: '#object' }
