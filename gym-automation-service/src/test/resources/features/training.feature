Feature: TrainingController integration not functional Test

  @Endpoint_Create_correct_training_test
  Scenario: Create a new training with token
    Given I create a training with username trainee "Maria.Ivanova" and username trainer "Petr.Andreev"
    When I sends the request to the create endpoint
    Then the response name should be 201
    And in microservice training-hours-tracker has information about trainer with username "Petr.Andreev"