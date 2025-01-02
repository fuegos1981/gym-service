package com.gym.crm.automation.steps;

import com.gym.crm.dto.LoginChangeRequest;
import com.gym.crm.dto.TraineeProfile;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TraineeIntegrationSteps extends TraineeTest {

    private ResponseEntity<?> response;
    private String firstName;
    private String lastName;

    @Given("a trainee with first name {string} and last name {string}")
    public void aTraineeRegistrationRequestWithValidDetails(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    @When("the client sends the request to the register endpoint")
    public void theClientSendsTheRequestToTheRegisterEndpoint() {
        TraineeProfile traineeProfile = new TraineeProfile()
                .firstName(firstName)
                .lastName(lastName)
                .address("Merefa")
                .dateOfBirth(LocalDate.of(2000, 11, 1));

        response = post("/api/v1/gym-crm-service/trainee/register", traineeProfile, Object.class);
    }

    @Then("the response status should be 201")
    public void theResponseStatusShouldBe201() {
        assertEquals(201, response.getStatusCode().value());
    }

    @And("the response should contain username {string}")
    public void theResponseShouldContainTheRegisteredTraineeDetails(String expectedUsername) {
        String responseBody = response.getBody().toString();
        assertTrue(responseBody.contains(expectedUsername));
    }

    @Given("a login change request with valid details")
    public void aLoginChangeRequestWithValidDetails() {
        LoginChangeRequest request = new LoginChangeRequest();
        request.setUsername("Maria.Ivanova");
        request.oldPassword("password12");
        request.newPassword("newPass123");

        response = put("/api/v1/gym-crm-service/trainee/change-login", request, Object.class);
    }

    @Then("the response status should be 200")
    public void theResponseStatusShouldBe200() {
        assertEquals(200, response.getStatusCode().value());
    }

    @Given("an existing trainee with username {string} for getting profile")
    public void getTraineeWithUsername(String username) {
        response = get("/api/v1/gym-crm-service/trainee/" + username, Object.class);
    }

    @Then("the response should contain the trainee profile details")
    public void theResponseShouldContainTheTraineeProfileDetails() {
        String responseBody = response.getBody().toString();
        assertTrue(responseBody.contains("Maria")&&responseBody.contains("Ivanova"));
    }
}
