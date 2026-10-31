Feature: Reusable user creation
  This feature can be called from other features to create a user

  Scenario: Create a user and return the response
    * url baseUrl
    * def utils = call read('classpath:karate/common/utils.js')

    # Use provided user data or generate random data
    * def username = karate.get('username', utils.randomUsername())
    * def email = karate.get('email', utils.randomEmail())
    * def firstName = karate.get('firstName', 'Test')
    * def lastName = karate.get('lastName', 'User')

    Given path '/api/users'
    And request
      """
      {
        "username": "#(username)",
        "email": "#(email)",
        "firstName": "#(firstName)",
        "lastName": "#(lastName)"
      }
      """
    When method POST
    Then status 201
    And match response.id == '#uuid'

    # Return the created user for use in calling feature
    * def createdUser = response
