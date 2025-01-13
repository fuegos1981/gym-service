package com.gym.automation.cucumber.steps;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gym.crm.dto.AddTrainingsRequest;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TrainingIntegrationTest {

    private static final String URL_CREATE_TRAINING = "/api/v1/gym-crm-service/training/create";
    private static final String API_VERSION_TRAINER_HOURS = "/api/v1/training-hours-tracker";
    private static final String TRAINING_NAME = "Box in our life";

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    private String traineeUsername;
    private String trainerUsername;

    @Value("${crm.service.port}")
    private int crmServicePort;

    @Value("${hoursTracker.service.port}")
    private int hoursTrackerServicePort;

    private HttpResponse<String> response;

    public TrainingIntegrationTest(ObjectMapper objectMapper, HttpClient httpClient) {
        this.objectMapper = objectMapper;
        this.httpClient = httpClient;
    }

    @Given("I create a training with username trainee {string} and username trainer {string}")
    public void i_create_a_training_with_username_trainee(String traineeUsername, String trainerUsername) {
        this.traineeUsername = traineeUsername;
        this.trainerUsername = trainerUsername;
    }

    @When("I sends the request to the create endpoint")
    public void sendRequest() throws InterruptedException, IOException {
        String fullUrl = "http://localhost:" + crmServicePort + URL_CREATE_TRAINING;

        String requestBody = objectMapper.writeValueAsString(buildTrainingsRequest());

        HttpRequest request = getHttpRequestBuilder(fullUrl)
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        Thread.sleep(2000);
    }

    @Then("the response name should be 201")
    public void getResponseCode() {
        assertNotNull(response);
        assertEquals(201, response.statusCode());
    }

    @Then("in microservice training-hours-tracker has information about trainer with username {string}")
    public void checkTrainerUsername(String username) throws IOException, InterruptedException {
        String fullUrl = String.format("http://localhost:%s%s/trainers/%s/summary", hoursTrackerServicePort, API_VERSION_TRAINER_HOURS, username);
        HttpRequest request = getHttpRequestBuilder(fullUrl).GET().build();
        HttpResponse<String> responseHoursTracker = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertNotNull(responseHoursTracker);
        assertEquals(200, responseHoursTracker.statusCode());
        assertTrue(responseHoursTracker.body().contains(username));
    }

    private AddTrainingsRequest buildTrainingsRequest() {
        return new AddTrainingsRequest()
                .traineeUsername(traineeUsername)
                .trainingDate(LocalDate.of(2024, 10, 10))
                .trainerUsername(trainerUsername)
                .trainingName(TRAINING_NAME)
                .trainingDuration(2.0);
    }

    private HttpRequest.Builder getHttpRequestBuilder(String url) {
        return HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .header("X-Requested-With", "XMLHttpRequest")
                .header("Accept", "application/json")
                .header("Gateway", "my-secure-gateway-secret-key-which-is-shared")
                .header("X-Transaction-Id", "478928888")
                .header("X-USER", "Test.User")
                .header("Authorization", "Bearer trtrttrtrtr");
    }
}
