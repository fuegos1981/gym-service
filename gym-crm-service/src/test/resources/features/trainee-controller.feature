@specialHook
Feature: TraineeController Component Tests

  Scenario: Register a new trainee
    Given a trainee with first name "Ivan" and last name "Borov"
    When the client sends the request to the register endpoint
    Then the response status should be 201
    And the response should contain username "Ivan.Borov"

  Scenario: Login change for trainee
    Given a login change request with valid details
    Then the response status should be 200

  Scenario: Get trainee profile
    Given an existing trainee with username "Maria.Ivanova" for getting profile
    Then the response should contain the trainee profile details