Feature: Trainee Service Component Tests

  @Endpoint2_1
  Scenario: Create a new trainee profile
    Given a trainee profile with first name "John" and last name "Doe"
    When the service creates the trainee
    Then the trainee username should be "John.Doe"
    And a new password should be generated

  @Endpoint2_2
  Scenario: Update an existing trainee profile
    Given an existing trainee with username "Jane.Doe"
    When the service updates the trainee with first name "Jane" and last name "Smith"
    Then the trainee should have the updated name "Jane Smith"

  @Endpoint2_3
  Scenario: Match username and password
    Given a trainee with username "John.Doe" and password "password12"
    When the service matches the username and password "password12"
    Then the result should be true

  @Endpoint2_4
  Scenario: Change trainee active status
    Given a trainee with username "John.Doe" is currently active
    When the service changes the active status to false
    Then the trainee should be inactive

  @Endpoint2_5
  Scenario: Delete trainee
    Given a trainee with username "John.Doe" exists for deletion
    When the service deletes the trainee with username "John.Doe"
    Then the delete result should be true