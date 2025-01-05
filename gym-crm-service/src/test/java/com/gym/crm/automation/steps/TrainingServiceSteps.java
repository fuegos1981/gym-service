package com.gym.crm.automation.steps;

import com.gym.crm.dto.AddTrainingsRequest;
import com.gym.crm.exception.EntityNotFoundException;
import com.gym.crm.model.Trainee;
import com.gym.crm.model.Trainer;
import com.gym.crm.model.Training;
import com.gym.crm.model.TrainingType;
import com.gym.crm.model.User;
import com.gym.crm.repository.TraineeRepository;
import com.gym.crm.repository.TrainerRepository;
import com.gym.crm.repository.TrainingRepository;
import com.gym.crm.service.impl.AnalyticsSender;
import com.gym.crm.service.impl.TrainingServiceImpl;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TrainingServiceSteps {

    @Mock
    private TraineeRepository traineeRepository;

    @Mock
    private TrainerRepository trainerRepository;

    @Mock
    private TrainingRepository trainingRepository;

    @Mock
    private AnalyticsSender analyticsSender;

    @InjectMocks
    private TrainingServiceImpl trainingService;

    private Exception exception;
    private AddTrainingsRequest request;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Given("a trainee with username {string} exists")
    public void aTraineeWithUsernameExists(String username) {
        Trainee trainee = buildTrainee(username);
        when(traineeRepository.findByUserUsername(username)).thenReturn(Optional.of(trainee));
    }

    @Given("no trainee with username {string} exists")
    public void noTraineeWithUsernameExists(String username) {
        when(traineeRepository.findByUserUsername(username)).thenReturn(Optional.empty());
    }

    @Given("a trainer with username {string} exists")
    public void aTrainerWithUsernameExists(String username) {
        Trainer trainer = buildTrainer(username);
        when(trainerRepository.findByUserUsername(username)).thenReturn(Optional.of(trainer));
    }

    @Given("no trainer with username {string} exists")
    public void noTrainerWithUsernameExists(String username) {
        when(trainerRepository.findByUserUsername(username)).thenReturn(Optional.empty());
    }

    @When("I create a training with name {string}, date {string}, and duration {string}")
    public void iCreateATrainingWithNameDateAndDuration(String name, String date, String duration) {
        request = new AddTrainingsRequest()
                .traineeUsername("john.doe")
                .trainerUsername("jane.doe")
                .trainingName(name)
                .trainingDate(LocalDate.parse(date))
                .trainingDuration(Double.parseDouble(duration.replace(" minutes", "")));

        when(trainingRepository.save(any(Training.class))).thenAnswer(invocation -> {
            Training training = invocation.getArgument(0);
            return training;
        });

        try {
            trainingService.create(request);
        } catch (Exception e) {
            exception = e;
        }
    }

    @Then("the training is successfully created with the name {string}")
    public void theTrainingIsSuccessfullyCreatedWithTheName(String name) {
        ArgumentCaptor<Training> trainingCaptor = ArgumentCaptor.forClass(Training.class);

        verify(trainingRepository, times(1)).save(trainingCaptor.capture());
        Training savedTraining = trainingCaptor.getValue();

        assertNotNull(savedTraining);
        assertEquals(name, savedTraining.getName());

        verify(analyticsSender, times(1)).processWorkload(savedTraining, "ADD");
    }

    @Then("as result an EntityNotFoundException is thrown with message {string}")
    public void anEntityNotFoundExceptionIsThrownWithMessage(String expectedMessage) {
        assertNotNull(exception);
        assertTrue(exception instanceof EntityNotFoundException);
        assertEquals(expectedMessage, exception.getMessage());
    }

    private Trainee buildTrainee(String username) {
        User traineeUser = User.builder()
                .firstName("Maria")
                .lastName("Burova")
                .username(username)
                .password("1111111111")
                .isActive(true).build();

        return Trainee.builder()
                .dateOfBirth(LocalDate.of(1980, 11, 10))
                .address("Merefa")
                .user(traineeUser).build();
    }

    private Trainer buildTrainer(String username) {
        User trainerUser = User.builder()
                .firstName("Oleg")
                .lastName("Ilin")
                .username(username)
                .password("2222222222")
                .isActive(true).build();

        return Trainer.builder()
                .specialization(new TrainingType(null, "Yoga"))
                .user(trainerUser).build();
    }
}
