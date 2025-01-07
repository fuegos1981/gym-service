Feature: Negative scenarios for TrainerService

  @Endpoint_Create_null_trainer
  Scenario: Create Trainer with null profile
    When I attempt to create a trainer with trainer profile null
    Then a ServiceException is thrown with message "Trainer cannot be null"

  @Endpoint_Update_trainer_with_null_username
  Scenario: Update Trainer with non-existent username
    Given the trainer update request has username "nonexistent_user"
    When I attempt to update the trainer
    Then an EntityNotFoundException is thrown with message "Trainer not found in database"

  @Endpoint_Get_trainer_with_null_username
  Scenario: Find Trainer with null username
    When I attempt to find a trainer by null username
    Then a ServiceException is thrown with message "Username trainer cannot be null"

  @Endpoint_Match_trainer_with_null_password
  Scenario: Match username and password with null password
    Given the username is "Marfa.Petrova" and password is null
    When I attempt to match username and password
    Then a ServiceException is thrown with message "Trainer username or password cannot be null"