package com.gym.analytics.automation.steps;

import com.gym.analytics.dto.TrainerMonthlySummaryResponse;
import com.gym.analytics.dto.TrainerWorkloadRequest;
import com.gym.analytics.exception.EntityNotFoundException;
import com.gym.analytics.mapper.TrainerMapper;
import com.gym.analytics.model.Trainer;
import com.gym.analytics.repository.TrainerReportRepository;
import com.gym.analytics.service.impl.TrainerServiceImpl;
import com.gym.analytics.service.impl.TrainingSummaryManager;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TrainerServiceSteps {

    @Mock
    private TrainerReportRepository repository;

    @Mock
    private TrainerMapper mapper;

    @Mock
    private TrainingSummaryManager manager;

    @InjectMocks
    private TrainerServiceImpl service;

    private TrainerMonthlySummaryResponse response;
    private Trainer savedTrainer;
    private Trainer existedTrainer;
    private Exception exception;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Given("a trainer with username {string} exists in the repository")
    public void aTrainerWithUsernameExistsInTheRepository(String username) {
        existedTrainer = Trainer.builder().username(username).build();
        when(repository.findByUsername(username)).thenReturn(Optional.of(existedTrainer));
    }

    @Given("no trainer with username {string} exists in the repository")
    public void noTrainerWithUsernameExistsInTheRepository(String username) {
        when(repository.findByUsername(username)).thenReturn(Optional.empty());
        when(repository.save(any(Trainer.class))).thenAnswer(invocation -> invocation.getArgument(0));
    }

    @When("I save workload for trainer {string} with training duration {string} hours and action {string}")
    public void iSaveWorkloadForTrainerWithTrainingDurationHoursAndAction(String username, String duration, String action) {
        TrainerWorkloadRequest workloadRequest = new TrainerWorkloadRequest()
                .username(username)
                .trainingDuration(Double.valueOf(duration))
                .actionType(TrainerWorkloadRequest.ActionTypeEnum.valueOf(action));

        try {
            savedTrainer = service.saveWorkload(workloadRequest);
        } catch (Exception e) {
            exception = e;
        }
    }

    @Then("the trainer's total workload is updated successfully")
    public void theTrainersTotalWorkloadIsUpdatedSuccessfully() {
        verify(repository, times(1)).save(any(Trainer.class));
        assertNull(exception);
    }

    @Then("a new trainer is created with username {string}")
    public void aNewTrainerIsCreatedWithUsername(String username) {
        assertNotNull(savedTrainer);
        assertEquals(username, savedTrainer.getUsername());
        verify(repository, times(1)).save(any(Trainer.class));
    }

    @Then("the workload is saved successfully")
    public void theWorkloadIsSavedSuccessfully() {
        assertNull(exception);
        verify(manager, times(1)).addDurationToYearlySummary(any(Trainer.class), any(), eq(2.0));
    }

    @Then("the trainer's yearly summary includes the added duration")
    public void theTrainersYearlySummaryIncludesTheAddedDuration() {
        verify(manager, times(1)).addDurationToYearlySummary(any(Trainer.class), any(), anyDouble());
    }

    @When("I retrieve the trainer summary for user with firstName {string} and lastName {string}")
    public void iRetrieveTheTrainerSummaryFor(String firstName, String lastName) {
        String username = firstName + "." + lastName;
        TrainerMonthlySummaryResponse expectedResponse = new TrainerMonthlySummaryResponse()
                .username(username)
                .firstName(firstName)
                .lastName(lastName)
                .status(TrainerMonthlySummaryResponse.StatusEnum.ACTIVE);

        when(mapper.toGetTrainerMonthlySummaryResponse(existedTrainer)).thenReturn(expectedResponse);

        try {
            response = service.getTrainer(username);
        } catch (Exception e) {
            exception = e;
        }
    }

    @Then("the returned trainer summary matches the repository data")
    public void theReturnedTrainerSummaryMatchesTheRepositoryData() {
        assertNotNull(response);
        verify(mapper, times(1)).toGetTrainerMonthlySummaryResponse(any(Trainer.class));
    }

    @Then("an EntityNotFoundException is thrown with message {string}")
    public void anEntityNotFoundExceptionIsThrownWithMessage(String message) {
        assertNotNull(exception);
        assertTrue(exception instanceof EntityNotFoundException);
        assertEquals(message, exception.getMessage());
    }
}
