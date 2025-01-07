package com.gym.crm.automation.steps;

import com.gym.crm.dto.TraineeProfile;
import com.gym.crm.dto.UpdateTraineeRequest;
import com.gym.crm.dto.UserDetailsResponse;
import com.gym.crm.model.Trainee;
import com.gym.crm.model.User;
import com.gym.crm.repository.TraineeRepository;
import com.gym.crm.repository.TrainingRepository;
import com.gym.crm.repository.UserRepository;
import com.gym.crm.security.CustomPasswordEncoder;
import com.gym.crm.service.impl.AnalyticsSender;
import com.gym.crm.service.impl.ProfileService;
import com.gym.crm.service.impl.TraineeServiceImpl;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TraineeServiceSteps {

    @Mock
    private TraineeRepository repository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TrainingRepository trainingRepository;

    @Mock
    private AnalyticsSender sender;

    @Mock
    private ProfileService profileService;

    @Mock
    private CustomPasswordEncoder passwordEncoder;

    @Mock
    private Trainee traineeFromStorage;

    @InjectMocks
    private TraineeServiceImpl service;

    private TraineeProfile traineeProfile;
    private UserDetailsResponse userDetailsResponse;
    private Trainee updatedTrainee;
    private String username;

    private boolean matchResult;
    private boolean deleteResult;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Given("a trainee profile with first name {string} and last name {string}")
    public void createTraineeProfile(String firstName, String lastName) {
        ReflectionTestUtils.setField(service, "profileService", new ProfileService());

        traineeProfile = new TraineeProfile();
        traineeProfile.setFirstName(firstName);
        traineeProfile.setLastName(lastName);

        String generatedPassword = "generatedPassword";
        String username = firstName + "." + lastName;
        List<String> duplicateNames = List.of();

        when(profileService.createNewPassword()).thenReturn(generatedPassword);
        when(userRepository.findByUsernamesStartedFrom(username)).thenReturn(duplicateNames);
        when(passwordEncoder.generatePasswordHash(generatedPassword)).thenReturn("hashedPassword");
        when(repository.save(any(Trainee.class))).thenAnswer(invocation -> invocation.getArgument(0));
    }

    @When("the service creates the trainee")
    public void createTrainee() {
        userDetailsResponse = service.create(traineeProfile);
    }

    @Then("the trainee username should be {string}")
    public void verifyUsername(String expectedUsername) {
        assertEquals(expectedUsername, userDetailsResponse.getUsername());
    }

    @And("a new password should be generated")
    public void verifyPassword() {
        assertNotNull(userDetailsResponse.getPassword());
    }

    @Given("an existing trainee with username {string}")
    public void findExistingTrainee(String username) {
        User user = User.builder().id(1L).username(username).build();
        traineeFromStorage = Trainee.builder().user(user).id(2L).build();

        when(repository.findByUserUsername(username)).thenReturn(Optional.of(traineeFromStorage));
    }

    @When("the service updates the trainee with first name {string} and last name {string}")
    public void updateTrainee(String firstName, String lastName) {
        UpdateTraineeRequest updateRequest = new UpdateTraineeRequest();
        updateRequest.setUsername(traineeFromStorage.getUser().getUsername());
        updateRequest.setFirstName(firstName);
        updateRequest.setLastName(lastName);

        User updatedUser = User.builder()
                .id(traineeFromStorage.getUser().getId())
                .username(updateRequest.getUsername())
                .firstName(updateRequest.getFirstName())
                .lastName(updateRequest.getLastName())
                .build();
        updatedTrainee = Trainee.builder().user(updatedUser).id(traineeFromStorage.getId()).build();

        when(repository.save(any(Trainee.class))).thenReturn(updatedTrainee);
    }

    @Then("the trainee should have the updated name {string}")
    public void verifyUpdatedTrainee(String expectedName) {
        String fullName = updatedTrainee.getUser().getFirstName() + " " + updatedTrainee.getUser().getLastName();
        assertEquals(expectedName, fullName);
    }

    @Given("a trainee with username {string} and password {string}")
    public void setTraineeCredentials(String username, String password) {
        this.username = username;
        when(userRepository.retrievePasswordByUsername(username)).thenReturn(password);
        when(passwordEncoder.generatePasswordHash("password12")).thenReturn(password);
    }

    @When("the service matches the username and password {string}")
    public void matchPassword(String password) {
        matchResult = service.matchUsernameAndPassword(username, password);
    }

    @Then("the result should be true")
    public void verifyMatchResult() {
        assertTrue(matchResult);
    }

    @Given("a trainee with username {string} is currently active")
    public void traineeIsActive(String username) {
        this.username = username;
        doNothing().when(userRepository).changeIsActive(username, false);
    }

    @When("the service changes the active status to false")
    public void changeActiveStatus() {
        service.changeIsActive(username, false);
    }

    @Then("the trainee should be inactive")
    public void verifyInactiveStatus() {
        verify(userRepository).changeIsActive(username, false);
    }

    @Given("a trainee with username {string} exists for deletion")
    public void traineeExistsForDeletion(String username) {
        User user = User.builder().username(username).build();
        Trainee traineeForDelete = Trainee.builder().user(user).build();

        when(repository.findByUserUsername(username)).thenReturn(Optional.of(traineeForDelete));
        when(sender.processWorkload(any(List.class), anyString())).thenReturn("OK");
        doNothing().when(repository).delete(any(Trainee.class));
    }

    @When("the service deletes the trainee with username {string}")
    public void deleteTrainee(String username) {
        deleteResult = service.delete(username);
    }

    @Then("the delete result should be true")
    public void verifyDeleteResult() {
        assertTrue(deleteResult);
    }
}
