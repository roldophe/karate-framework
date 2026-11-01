Feature: User CRUD Operations
  Test CRUD operations for User API

  Background:
    * url baseUrl
    * def uniqueSuffix = new Date().getTime()
    * def createUserRequest = read('classpath:karate/data/request/create-user.json')
    * set createUserRequest.username = 'karate_test_' + uniqueSuffix
    * set createUserRequest.email = 'karate_test_' + uniqueSuffix + '@example.com'
    * def updateUserRequest = read('classpath:karate/data/request/update-user.json')
    * set updateUserRequest.username = 'karate_updated_' + uniqueSuffix
    * set updateUserRequest.email = 'karate_updated_' + uniqueSuffix + '@example.com'
    * def expectedUserResponse = read('classpath:karate/data/response/user-response-schema.json')

  @smoke @regression
  Scenario: Create a new user
    Given path '/api/users'
    And request createUserRequest
    When method POST
    Then status 201
    And match response.id == '#uuid'
    And match response.username == createUserRequest.username
    And match response.email == createUserRequest.email
    And match response.firstName == createUserRequest.firstName
    And match response.lastName == createUserRequest.lastName
    And match response.active == true

  @smoke @regression
  Scenario: Complete user lifecycle - Create, Read, Update, Delete
    # Create user
    Given path '/api/users'
    And request createUserRequest
    When method POST
    Then status 201
    And match response == expectedUserResponse
    * def userId = response.id
    * def createdUser = response

    # Read user
    Given path '/api/users/' + userId
    When method GET
    Then status 200
    And match response == createdUser

    # Update user
    Given path '/api/users/' + userId
    And request updateUserRequest
    When method PUT
    Then status 200
    And match response.id == userId
    And match response.username == updateUserRequest.username
    And match response.email == updateUserRequest.email

    # Delete user
    Given path '/api/users/' + userId
    When method DELETE
    Then status 204

    # Verify deletion
    Given path '/api/users/' + userId
    When method GET
    Then status 404

  @regression
  Scenario: Get non-existent user returns 404
    * def nonExistentId = '00000000-0000-0000-0000-000000000000'
    Given path '/api/users', nonExistentId
    When method GET
    Then status 404

  @regression
  Scenario: Update non-existent user returns 404
    * def nonExistentId = '00000000-0000-0000-0000-000000000000'
    Given path '/api/users', nonExistentId
    And request updateUserRequest
    When method PUT
    Then status 404

  @regression
  Scenario: Delete non-existent user returns 404
    * def nonExistentId = '00000000-0000-0000-0000-000000000000'
    Given path '/api/users', nonExistentId
    When method DELETE
    Then status 404

  @regression
  Scenario Outline: Create users with different data
    * def userData = read('classpath:karate/data/csv/users.csv')
    * def userRow = userData[<rowIndex>]
    * def timestamp = new Date().getTime()
    * def uniqueUsername = userRow.username + '_' + timestamp + '_' + <rowIndex>
    * def uniqueEmail = userRow.username + '_' + timestamp + '_' + <rowIndex> + '@example.com'

    Given path '/api/users'
    And request
      """
      {
        "username": "#(uniqueUsername)",
        "email": "#(uniqueEmail)",
        "firstName": "#(userRow.firstName)",
        "lastName": "#(userRow.lastName)",
        "active": true
      }
      """
    When method POST
    Then status 201
    And match response.id == '#uuid'
    And match response.username == uniqueUsername
    And match response.email == uniqueEmail

    Examples:
      | rowIndex |
      | 0        |
      | 1        |
      | 2        |
