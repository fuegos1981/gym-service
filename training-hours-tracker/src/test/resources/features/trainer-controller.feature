@specialHook
Feature: TrainerController Integration Tests

  @Endpoint_Handle_correct_workload
  Scenario: Successfully handle workload
    Given a trainer exists in the repository with firstName "John" and lastName "Doe"
    When I send a POST request to "/trainers/workload" using gateway
    Then the response status should be "200"
    And the response body should be "Workload processed successfully."

  @Endpoint_Get_summary_non_existed_trainer
  Scenario: Fail to get summary for a non-existing trainer
    Given no trainer with username "Non.Existent" exists in the repository
    When I send a GET request to "/trainers/Non.Existent/summary" using gateway
    Then the response status should be "404"
    And the response body should contain "Trainer not found in database"

  @Endpoint_Get_summary_without_access
  Scenario: Fail to get summary for a non-existing trainer
    Given a trainer exists in the repository with firstName "John" and lastName "Doe"
    When I send a GET request to "/trainers/John.Doe/summary" without gateway
    Then the response status should be "401"
    And the response body should be "Use Gateway entrance"