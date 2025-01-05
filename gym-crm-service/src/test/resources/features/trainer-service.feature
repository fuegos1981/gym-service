Feature: Negative scenarios for TrainerService

  @Endpoint3_1
  Scenario: Create Trainer with null profile
    Given the trainer profile is null
    When I attempt to create a trainer
    Then a ServiceException is thrown with message "Trainer cannot be null"

  @Endpoint3_2
  Scenario: Update Trainer with non-existent username
    Given the trainer update request has username "nonexistent_user"
    When I attempt to update the trainer
    Then an EntityNotFoundException is thrown with message "Trainer not found in database"

  @Endpoint3_3
  Scenario: Find Trainer with null username
    Given the username is null
    When I attempt to find a trainer by username
    Then a ServiceException is thrown with message "Username trainer cannot be null"

  @Endpoint3_4
  Scenario: Match username and password with null inputs
    Given the username is null and password is null
    When I attempt to match username and password
    Then a ServiceException is thrown with message "Trainer username or password cannot be null"