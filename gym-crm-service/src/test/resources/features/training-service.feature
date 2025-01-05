Feature: Training Service
  Test the creation of trainings with different scenarios

  @Endpoint4_1
  Scenario: Successfully create a training
    Given a trainee with username "john.doe" exists
    And a trainer with username "jane.doe" exists
    When I create a training with name "Yoga Session", date "2025-01-10", and duration "60 minutes"
    Then the training is successfully created with the name "Yoga Session"

  @Endpoint4_2
  Scenario: Fail to create a training when trainee does not exist
    Given no trainee with username "john.doe" exists
    And a trainer with username "jane.doe" exists
    When I create a training with name "Yoga Session", date "2025-01-10", and duration "60 minutes"
    Then as result an EntityNotFoundException is thrown with message "Trainee in training cannot be null"

  @Endpoint4_3
  Scenario: Fail to create a training when trainer does not exist
    Given a trainee with username "john.doe" exists
    And no trainer with username "jane.doe" exists
    When I create a training with name "Yoga Session", date "2025-01-10", and duration "60 minutes"
    Then as result an EntityNotFoundException is thrown with message "Trainer in training cannot be null"