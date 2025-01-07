Feature: Trainer Service

  @Endpoint_Save_correct_workload
  Scenario: Successfully save workload for a trainer
    Given a trainer with username "John.Doe" exists in the repository
    When I save workload for trainer "John.Doe" with training duration "2.0" hours and action "ADD"
    Then the trainer's total workload is updated successfully
    And the trainer's yearly summary includes the added duration

  @Endpoint_Save_failed_workload
  Scenario: Fail to save workload for a non-existing trainer
    Given no trainer with username "Non.Existent" exists in the repository
    When I save workload for trainer "Non.Existent" with training duration "2.0" hours and action "ADD"
    Then a new trainer is created with username "Non.Existent"
    And the workload is saved successfully

  @Endpoint_Get_summary_for_existing_trainer
  Scenario: Successfully retrieve a trainer summary
    Given a trainer with username "Jane.Doe" exists in the repository
    When I retrieve the trainer summary for user with firstName "Jane" and lastName "Doe"
    Then the returned trainer summary matches the repository data

  @Endpoint_Get_summary_for_non_existing_trainer
  Scenario: Fail to retrieve a trainer summary for a non-existing trainer
    Given no trainer with username "Non.Existent" exists in the repository
    When I retrieve the trainer summary for user with firstName "Non" and lastName "Existent"
    Then an EntityNotFoundException is thrown with message "Trainer not found in database"