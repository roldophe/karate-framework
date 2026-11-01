Feature: User CRUD Operations with Database

  Background:
    * url baseUrl
    * def randomId = function(){ return java.util.UUID.randomUUID() + '' }
    * def uniqueUsername = 'user_' + new Date().getTime()
    * def uniqueEmail = uniqueUsername + '@example.com'

  @smoke @users @create
  Scenario: Create a new user
    Given path '/api/users'
    And request
      """
      {
        "username": "#(uniqueUsername)",
        "email": "#(uniqueEmail)",
        "firstName": "John",
        "lastName": "Doe",
        "active": true
      }
      """
    When method POST
    Then status 201
    And match response.username == uniqueUsername
    And match response.email == uniqueEmail
    And match response.firstName == 'John'
    And match response.lastName == 'Doe'
    And match response.active == true
    And match response.id == '#uuid'
    And match response.createdAt == '#notnull'
    And match response.updatedAt == '#notnull'

  @smoke @users @read
  Scenario: Get user by ID
    # Create user first
    Given path '/api/users'
    And request
      """
      {
        "username": "#(uniqueUsername)",
        "email": "#(uniqueEmail)",
        "firstName": "Jane",
        "lastName": "Smith"
      }
      """
    When method POST
    Then status 201
    * def userId = response.id

    # Get user by ID
    Given path '/api/users', userId
    When method GET
    Then status 200
    And match response.id == userId
    And match response.username == uniqueUsername
    And match response.email == uniqueEmail

  @users @read
  Scenario: Get user by username
    # Create user first
    Given path '/api/users'
    And request
      """
      {
        "username": "#(uniqueUsername)",
        "email": "#(uniqueEmail)",
        "firstName": "Bob",
        "lastName": "Johnson"
      }
      """
    When method POST
    Then status 201

    # Get user by username
    Given path '/api/users/username', uniqueUsername
    When method GET
    Then status 200
    And match response.username == uniqueUsername

  @users @read
  Scenario: Get all users
    Given path '/api/users'
    When method GET
    Then status 200
    And match response == '#array'

  @users @update
  Scenario: Update user
    # Create user first
    Given path '/api/users'
    And request
      """
      {
        "username": "#(uniqueUsername)",
        "email": "#(uniqueEmail)",
        "firstName": "Original",
        "lastName": "Name"
      }
      """
    When method POST
    Then status 201
    * def userId = response.id
    * def updatedUsername = uniqueUsername + '_updated'
    * def updatedEmail = updatedUsername + '@example.com'

    # Update user
    Given path '/api/users', userId
    And request
      """
      {
        "username": "#(updatedUsername)",
        "email": "#(updatedEmail)",
        "firstName": "Updated",
        "lastName": "User",
        "active": true
      }
      """
    When method PUT
    Then status 200
    And match response.id == userId
    And match response.username == updatedUsername
    And match response.firstName == 'Updated'

  @users @delete
  Scenario: Delete user
    # Create user first
    Given path '/api/users'
    And request
      """
      {
        "username": "#(uniqueUsername)",
        "email": "#(uniqueEmail)",
        "firstName": "Delete",
        "lastName": "Me"
      }
      """
    When method POST
    Then status 201
    * def userId = response.id

    # Delete user
    Given path '/api/users', userId
    When method DELETE
    Then status 204

    # Verify user is deleted
    Given path '/api/users', userId
    When method GET
    Then status 404

  @users @deactivate
  Scenario: Deactivate user
    # Create user first
    Given path '/api/users'
    And request
      """
      {
        "username": "#(uniqueUsername)",
        "email": "#(uniqueEmail)",
        "firstName": "Active",
        "lastName": "User"
      }
      """
    When method POST
    Then status 201
    * def userId = response.id

    # Deactivate user
    Given path '/api/users', userId, 'deactivate'
    When method PATCH
    Then status 204

    # Verify user is deactivated
    Given path '/api/users', userId
    When method GET
    Then status 200
    And match response.active == false

  @users @search
  Scenario: Search users
    # Create users first
    * def user1Username = uniqueUsername + '_search1'
    * def user1Email = user1Username + '@example.com'
    Given path '/api/users'
    And request
      """
      {
        "username": "#(user1Username)",
        "email": "#(user1Email)",
        "firstName": "Search",
        "lastName": "Test1"
      }
      """
    When method POST
    Then status 201

    # Search users
    Given path '/api/users'
    And param search = 'search'
    When method GET
    Then status 200
    And match response == '#array'
    And match response[*].username contains user1Username

  @users @validation
  Scenario: Validation - Username too short
    Given path '/api/users'
    And request
      """
      {
        "username": "ab",
        "email": "#(uniqueEmail)",
        "firstName": "Test",
        "lastName": "User"
      }
      """
    When method POST
    Then status 400

  @users @validation
  Scenario: Validation - Invalid email
    Given path '/api/users'
    And request
      """
      {
        "username": "#(uniqueUsername)",
        "email": "invalid-email",
        "firstName": "Test",
        "lastName": "User"
      }
      """
    When method POST
    Then status 400

  @users @validation
  Scenario: Validation - Duplicate username
    # Create first user
    Given path '/api/users'
    And request
      """
      {
        "username": "#(uniqueUsername)",
        "email": "#(uniqueEmail)",
        "firstName": "First",
        "lastName": "User"
      }
      """
    When method POST
    Then status 201

    # Try to create user with same username
    * def anotherEmail = 'another_' + uniqueEmail
    Given path '/api/users'
    And request
      """
      {
        "username": "#(uniqueUsername)",
        "email": "#(anotherEmail)",
        "firstName": "Second",
        "lastName": "User"
      }
      """
    When method POST
    Then status 400

  @users @notfound
  Scenario: Get non-existent user
    * def fakeId = randomId()
    Given path '/api/users', fakeId
    When method GET
    Then status 404
