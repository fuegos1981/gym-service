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

mvn verify -pl !discovery-server -Dgroups="Endpoint_Create_correct_training_test" - command for running  main integration test

Maven build for docker
mvn clean package -Pdocker-build
mvn clean package -pl gym-crm-service -Pdocker-build

Working with docker
docker-compose up -d
docker-compose down
docker-compose logs
docker logs mongo-db
docker logs mysql-db
docker logs mysql-db
docker logs activemq
docker logs discovery-server
docker logs gateway
docker logs gym-crm-service
docker logs training-hours-tracker



