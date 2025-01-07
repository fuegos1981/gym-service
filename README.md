Gym Service

TEST

mvn -pl training-hours-tracker verify -Dgroups="Endpoint_Save_correct_workload" - command fo running cucumber test with tag Endpoint1
in microservice training-hours-tracker

mvn -pl training-hours-tracker test -Dtest=CucumberRunnerTest - command for running all cucumber tests
in microservice training-hours-tracker

mvn -pl training-hours-tracker test - command for running all tests in microservice training-hours-tracker

mvn -pl gym-crm-service verify -Dgroups="Endpoint_Create_trainee" - command fo running cucumber test with tag @Endpoint_Create_trainee 
in microservice gym-crm-service

mvn -pl gym-crm-service test -Dtest=CucumberRunnerTest - command for running all cucumber tests 
in microservice gym-crm-service

mvn -pl gym-crm-service test - command for running all tests in microservice gym-crm-service
