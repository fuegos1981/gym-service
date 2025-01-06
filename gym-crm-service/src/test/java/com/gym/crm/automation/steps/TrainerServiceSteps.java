package com.gym.crm.automation.steps;

import com.gym.crm.dto.UpdateTrainerRequest;
import com.gym.crm.exception.ServiceException;
import com.gym.crm.repository.TrainerRepository;
import com.gym.crm.repository.UserRepository;
import com.gym.crm.security.CustomPasswordEncoder;
import com.gym.crm.service.impl.ProfileService;
import com.gym.crm.service.impl.TrainerServiceImpl;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TrainerServiceSteps {

    @Mock
    private TrainerRepository repository;

    @Mock
    private ProfileService profileService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CustomPasswordEncoder passwordEncoder;

    @InjectMocks
    private TrainerServiceImpl trainerService;

    private Exception exception;
    private String username;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @When("I attempt to create a trainer with trainer profile null")
    public void iAttemptToCreateATrainer() {
        exception = assertThrows(ServiceException.class, () -> trainerService.create(null));
    }

    @Then("a ServiceException is thrown with message {string}")
    public void aServiceExceptionIsThrownWithMessage(String expectedMessage) {
        assertNotNull(exception);
        assertTrue(exception instanceof ServiceException);
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Given("the trainer update request has username {string}")
    public void theTrainerUpdateRequestHasUsername(String username) {
        this.username = username;
    }

    @When("I attempt to update the trainer")
    public void iAttemptToUpdateTheTrainer() {
        UpdateTrainerRequest request = new UpdateTrainerRequest().username(username);
        when(repository.findByUserUsername(username)).thenThrow(new EntityNotFoundException("Trainer not found in database"));
        exception = assertThrows(EntityNotFoundException.class, () -> trainerService.update(request));
    }

    @Then("an EntityNotFoundException is thrown with message {string}")
    public void anEntityNotFoundExceptionIsThrownWithMessage(String expectedMessage) {
        assertNotNull(exception);
        assertTrue(exception instanceof EntityNotFoundException);
        assertEquals(expectedMessage, exception.getMessage());
    }

    @When("I attempt to find a trainer by null username")
    public void iAttemptToFindATrainerByUsername() {
        exception = assertThrows(ServiceException.class, () -> trainerService.findByUsername(null));
    }

    @Given("the username is {string} and password is null")
    public void theUsernameIsNullAndPasswordIsNull(String username) {
        this.username = username;
    }

    @When("I attempt to match username and password")
    public void iAttemptToMatchUsernameAndPassword() {
        exception = assertThrows(ServiceException.class, () -> trainerService.matchUsernameAndPassword(username, null));
    }
}
