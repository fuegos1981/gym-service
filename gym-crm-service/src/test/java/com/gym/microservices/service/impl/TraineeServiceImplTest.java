package com.gym.microservices.service.impl;

import com.gym.microservices.dto.TraineeProfile;
import com.gym.microservices.dto.UpdateTraineeRequest;
import com.gym.microservices.dto.UserDetailsResponse;
import com.gym.microservices.exception.ServiceException;
import com.gym.microservices.model.Trainee;
import com.gym.microservices.model.Trainer;
import com.gym.microservices.model.Training;
import com.gym.microservices.model.User;
import com.gym.microservices.repository.TraineeRepository;
import com.gym.microservices.repository.TrainerRepository;
import com.gym.microservices.repository.TrainingRepository;
import com.gym.microservices.repository.UserRepository;
import com.gym.microservices.security.CustomPasswordEncoder;
import com.gym.microservices.specification.SearchCriteria;
import com.gym.microservices.specification.SearchOperation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static java.util.Optional.ofNullable;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TraineeServiceImplTest {

    @Mock
    private TraineeRepository repository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TrainerRepository trainerRepository;

    @Mock
    private TrainingRepository trainingRepository;

    @Mock
    private AnalyticsSender sender;

    @Mock
    private ProfileService profileService;

    @Mock
    private CustomPasswordEncoder passwordEncoder;

    @InjectMocks
    private TraineeServiceImpl service;

    @Captor
    private ArgumentCaptor<Trainee> traineeCaptor;

    private static final String USERNAME = "Maria.Ivanova";

    @Test
    void checkIfCreatedTraineeIsCorrect() {
        ReflectionTestUtils.setField(service, "profileService", new ProfileService());
        TraineeProfile createRequest = new TraineeProfile()
                .firstName("Ivan")
                .lastName("Petrov")
                .dateOfBirth(LocalDate.of(1980, 11, 10))
                .address("Merefa");

        User user = buildUser("Ivan", "Petrov", "Ivan.Petrov");
        Trainee trainee = buildTrainee(user);

        when(userRepository.findByUsernamesStartedFrom("Ivan.Petrov")).thenReturn(Collections.emptyList());
        when(repository.save(any())).thenReturn(trainee);

        UserDetailsResponse response = service.create(createRequest);

        verify(repository).save(traineeCaptor.capture());

        assertThat(response.getPassword().length()).isEqualTo(10);
        assertTrue(response.getUsername().startsWith(user.getFirstName() + "." + user.getLastName()));
    }

    @Test
    void checkIfCreatedTraineeIsNull() {
        Exception exception = assertThrows(ServiceException.class, () -> service.create(null));
        assertTrue(exception.getMessage().contains("Trainee cannot be null"));
    }

    @Test
    void checkIfUpdatedTraineeIsCorrect() {
        User user = buildUser("Inna", "Ivanova", "Inna.Ivanova");
        Trainee trainee = buildTrainee(user);

        UpdateTraineeRequest updateTraineeRequest = new UpdateTraineeRequest()
                .firstName("Ivan")
                .lastName("Sebova")
                .isActive(true)
                .username("Inna.Ivanova")
                .dateOfBirth(LocalDate.of(1980, 11, 10))
                .address("Merefa");

        User expectedUser = buildUser("Inna", "Sebova", "Inna.Ivanova");
        Trainee expected = buildTrainee(expectedUser);

        when(repository.findByUserUsername(any())).thenReturn(ofNullable(trainee));
        when(repository.save(any())).thenReturn(expected);

        Trainee actual = service.update(updateTraineeRequest);

        verify(repository).save(traineeCaptor.capture());
        User actualUser = actual.getUser();

        assertEquals(expected, actual);
        assertEquals(updateTraineeRequest.getUsername(), actualUser.getUsername());
        assertEquals("Sebova", actualUser.getLastName());
    }

    @Test
    void checkIfDeleteTraineeIsNull() {
        Exception exception = assertThrows(ServiceException.class, () -> service.delete(null));
        assertTrue(exception.getMessage().contains("Trainee username cannot be null"));
    }

    @Test
    void checkIfDeleteTraineeIsCorrect() {
        User user = buildUser("Maria", "Ivanova", USERNAME);
        Trainee trainee = buildTrainee(user);

        when(repository.findByUserUsername(eq(USERNAME))).thenReturn(ofNullable(trainee));
        when(sender.processWorkload(any(List.class), anyString())).thenReturn("OK");
        doNothing().when(repository).delete(trainee);

        service.delete(USERNAME);
        verify(repository).delete(trainee);
    }

    @Test
    void checkIfTraineeForChangeIsActiveIsNull() {
        Exception exception = assertThrows(ServiceException.class, () -> service.changeIsActive(null, true));
        assertTrue(exception.getMessage().contains("Trainee username cannot be null"));
    }

    @Test
    void checkIfTraineeForChangeIsActiveIsCorrect() {
        doNothing().when(userRepository).changeIsActive(eq(USERNAME), eq(true));

        boolean actual = service.changeIsActive(USERNAME, true);

        verify(userRepository).changeIsActive(USERNAME, true);
        assertTrue(actual);
    }

    @Test
    void checkIfTraineeForGetTrainersTraineeIsNull() {
        Exception exception = assertThrows(ServiceException.class, () -> service.getTrainersTrainee(null));
        assertTrue(exception.getMessage().contains("Username trainee cannot be null"));
    }

    @Test
    void checkIfTraineeForgetTrainersTraineeIsCorrect() {
        when(repository.findByUserUsername(eq(USERNAME))).thenReturn(Optional.of(new Trainee()));

        Set<Trainer> trainers = service.getTrainersTrainee(USERNAME);

        verify(repository).findByUserUsername(USERNAME);
        assertEquals(Collections.emptySet(), trainers);
    }

    @Test
    void checkIfSelectedTraineeIsNull() {
        Exception exception = assertThrows(ServiceException.class, () -> service.findByUsername(null));
        assertTrue(exception.getMessage().contains("Username trainee cannot be null"));
    }

    @Test
    void checkChangingPasswordIfTraineeIsCorrect() {
        String password = "1111111111";
        String hashPassword = passwordEncoder.generatePasswordHash(password);

        doNothing().when(userRepository).changePassword(eq(USERNAME), eq(hashPassword));

        service.changePassword(USERNAME, password);

        verify(userRepository).changePassword(eq(USERNAME), eq(hashPassword));
    }

    @Test
    void checkIfTraineeForChangePasswordIsNull() {
        Exception exception = assertThrows(ServiceException.class, () -> service.changePassword(null, "33333333333"));
        assertTrue(exception.getMessage().contains("Trainee username or password cannot be null"));
    }

    @Test
    void checkIfMatchingUsernameAndPasswordIsCorrect() {
        String password = "1111111111";

        when(userRepository.retrievePasswordByUsername(eq(USERNAME))).thenReturn("test");
        when(passwordEncoder.generatePasswordHash(any())).thenReturn("test");

        boolean actual = service.matchUsernameAndPassword(USERNAME, password);

        verify(userRepository).retrievePasswordByUsername(USERNAME);
        assertTrue(actual);
    }

    @Test
    void checkIfGetTrainersNotAssignedForTraineeIsCalled() {
        service.getTrainersNotAssignedForTrainee(USERNAME);
        verify(repository).getTrainersNotAssignedForTrainee(USERNAME);
    }

    @Test
    void checkIfUpdateTrainersForTraineeIsCalled() {
        User user = buildUser("Maria", "Ivanova", USERNAME);
        Trainee trainee = buildTrainee(user);
        List<String> trainerUsernames = Collections.emptyList();

        when(repository.findByUserUsername(eq(USERNAME))).thenReturn(ofNullable(trainee));

        service.updateTrainersForTrainee(USERNAME, trainerUsernames);

        verify(repository).findByUserUsername(USERNAME);
    }

    @SuppressWarnings("unchecked")
    @Test
    void checkIfGetTrainingsByCriteriaIsCalled() {
        List<SearchCriteria> critters = List.of(new SearchCriteria("trainee.user.username", SearchOperation.EQUALITY, "test"));
        List<Training> expectedTrainings = List.of(Training.builder().name("test training").build());

        when(trainingRepository.findAll(any(Specification.class))).thenReturn(expectedTrainings);

        List<Training> actualTrainings = service.getTrainingsByCriteria(critters);

        assertNotNull(actualTrainings);
        assertEquals(1, actualTrainings.size());
        assertEquals("test training", actualTrainings.get(0).getName());

        verify(trainingRepository, times(1)).findAll(any(Specification.class));
    }

    private static User buildUser(String firstName, String lastname, String username) {
        return User.builder()
                .firstName(firstName)
                .lastName(lastname)
                .username(username)
                .password("1111111111")
                .isActive(true).build();
    }

    private static Trainee buildTrainee(User user) {
        return Trainee.builder()
                .dateOfBirth(LocalDate.of(1980, 11, 10))
                .address("Merefa")
                .user(user)
                .build();
    }
}