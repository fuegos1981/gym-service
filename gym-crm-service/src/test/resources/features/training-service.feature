Feature: Training Service
  Test the creation of trainings with different scenarios

  @Endpoint_Create_correct_training
  Scenario: Successfully create a training
    Given a trainee with username "john.doe" exists
    And a trainer with username "jane.doe" exists
    When I create a training with name "Yoga Session", date "2025-01-10", and duration "60 minutes"
    Then the training is successfully created with the name "Yoga Session"

  @Endpoint_Create_training_with_trainee_not_exist
  Scenario: Fail to create a training when trainee does not exist
    Given no trainee with username "john.doe" exists
    And a trainer with username "jane.doe" exists
    When I create a training with name "Yoga Session", date "2025-01-10", and duration "60 minutes"
    Then as result an EntityNotFoundException is thrown with message "Trainee in training cannot be null"

  @Endpoint_Create_training_with_trainer_not_exist
  Scenario: Fail to create a training when trainer does not exist
    Given a trainee with username "john.doe" exists
    And no trainer with username "jane.doe" exists
    When I create a training with name "Yoga Session", date "2025-01-10", and duration "60 minutes"
    Then as result an EntityNotFoundException is thrown with message "Trainer in training cannot be null"