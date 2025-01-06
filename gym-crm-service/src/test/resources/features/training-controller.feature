@specialHook
Feature: TrainingController integration not functional Test

  @Endpoint_Create_training_without_token
  Scenario: Create a new training without token
    Given I don't have token for entrance
    When I attempt to create a training without token
    Then the response name should be 401

