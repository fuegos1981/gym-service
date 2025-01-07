package com.gym.analytics.automation.steps;

import com.gym.analytics.dto.TrainerWorkloadRequest;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TrainerIntegrationSteps extends GlobalSender {

    private static final String API_VERSION = "/api/v1/training-hours-tracker";

    private ResponseEntity<?> response;
    private String firstName;
    private String lastName;

    @Given("a trainer exists in the repository with firstName {string} and lastName {string}")
    public void aTrainerExistsInTheRepositoryWithUsername(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    @Given("no trainer with firstName {string} and lastName {string} exists in the repository")
    public void noTrainerWithUsernameExistsInTheRepository(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    @When("I send a POST request to {string} using gateway")
    public void iSendAPOSTRequestToWithTheFollowingPayload(String url) {
        TrainerWorkloadRequest request = new TrainerWorkloadRequest()
                .firstName(firstName)
                .lastName(lastName)
                .isActive(true)
                .actionType(TrainerWorkloadRequest.ActionTypeEnum.ADD)
                .username(firstName + "." + lastName)
                .trainingDate(LocalDate.of(2025, 1, 1))
                .trainingDuration(5.0);

        response = post(API_VERSION + url, request, String.class);
    }

    @When("I send a GET request to {string} using gateway")
    public void iSendAGETRequestTo(String url) {
        response = get(API_VERSION + url, Object.class, true);
    }

    @When("I send a GET request to {string} without gateway")
    public void iSendAGETRequestWithoutRequestTo(String url) {
        response = get(API_VERSION + url, Object.class, false);
    }

    @Then("the response status should be {string}")
    public void theResponseStatusShouldBe(String status) {
        assertEquals(Integer.valueOf(status), response.getStatusCode().value());
    }

    @Then("the response body should be {string}")
    public void theResponseBodyShouldBe(String expectedBody) {
        String responseBody = response.getBody().toString();
        assertTrue(responseBody.contains(expectedBody));
    }

    @Then("the response body should contain {string}")
    public void theResponseBodyShouldContain(String expectedSubstring) {
        String responseBody = response.getBody().toString();
        assertTrue(responseBody.contains(expectedSubstring));
    }
}
