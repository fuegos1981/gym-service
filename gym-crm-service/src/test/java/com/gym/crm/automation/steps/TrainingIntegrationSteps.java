package com.gym.crm.automation.steps;

import com.gym.crm.dto.AddTrainingsRequest;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TrainingIntegrationSteps extends GlobalSender {

    private static final String API_VERSION = "/api/v1/gym-crm-service";
    private static final String TRAINEE_USERNAME = "Ivan.Ivanov";
    private static final String TRAINER_USERNAME = "Oleg.Burov";
    private static final String TRAINING_NAME = "Box in our life";

    private ResponseEntity<?> response;

    @Given("I don't have token for entrance")
    public void theTrainerProfileIsNull() {
    }

    @When("I attempt to create a training without token")
    public void iAttemptToCreateTrainingWithoutToken() {
        AddTrainingsRequest request = buildTrainingsRequest();
        response = post(API_VERSION + "/training/create", request, Object.class);
    }

    @Then("the response name should be 401")
    public void aAccessExceptionIsThrownWithMessage() {
        assertEquals(401, response.getStatusCode().value());
    }

    private AddTrainingsRequest buildTrainingsRequest() {
        return new AddTrainingsRequest()
                .traineeUsername(TRAINEE_USERNAME)
                .trainingDate(LocalDate.of(2024, 10, 10))
                .trainerUsername(TRAINER_USERNAME)
                .trainingName(TRAINING_NAME)
                .trainingDuration(2.0);
    }
}
