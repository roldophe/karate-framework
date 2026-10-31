Feature: User CRUD Operations
  Test CRUD operations for User API

  Background:
    * url baseUrl
    * def createUserRequest = read('classpath:karate/data/request/create-user.json')
    * def updateUserRequest = read('classpath:karate/data/request/update-user.json')
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
    Given path '/api/users/non-existent-id-12345'
    When method GET
    Then status 404

  @regression
  Scenario: Update non-existent user returns 404
    Given path '/api/users/non-existent-id-12345'
    And request updateUserRequest
    When method PUT
    Then status 404

  @regression
  Scenario: Delete non-existent user returns 404
    Given path '/api/users/non-existent-id-12345'
    When method DELETE
    Then status 404

  @regression
  Scenario Outline: Create users with different data
    * def userData = read('classpath:karate/data/csv/users.csv')
    * def userRow = userData[<rowIndex>]

    Given path '/api/users'
    And request
      """
      {
        "username": "#(userRow.username)",
        "email": "#(userRow.email)",
        "firstName": "#(userRow.firstName)",
        "lastName": "#(userRow.lastName)"
      }
      """
    When method POST
    Then status 201
    And match response.id == '#uuid'
    And match response.username == userRow.username
    And match response.email == userRow.email

    Examples:
      | rowIndex |
      | 0        |
      | 1        |
      | 2        |
