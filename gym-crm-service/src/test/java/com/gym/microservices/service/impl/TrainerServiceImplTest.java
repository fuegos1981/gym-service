package com.gym.microservices.service.impl;

import com.gym.microservices.dto.TrainerProfile;
import com.gym.microservices.dto.UpdateTrainerRequest;
import com.gym.microservices.exception.ServiceException;
import com.gym.microservices.model.Trainer;
import com.gym.microservices.model.Training;
import com.gym.microservices.model.TrainingType;
import com.gym.microservices.model.User;
import com.gym.microservices.repository.TrainerRepository;
import com.gym.microservices.repository.TrainingRepository;
import com.gym.microservices.repository.TrainingTypeRepository;
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

import java.util.Collections;
import java.util.List;

import static java.util.Optional.of;
import static java.util.Optional.ofNullable;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TrainerServiceImplTest {

    @Mock
    private TrainerRepository repository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TrainingTypeRepository trainingTypeRepository;

    @Mock
    private TrainingRepository trainingRepository;

    @Mock
    private ProfileService profileService;

    @Mock
    private CustomPasswordEncoder passwordEncoder;

    @InjectMocks
    private TrainerServiceImpl service;

    @Captor
    private ArgumentCaptor<Trainer> trainerCaptor;

    @Test
    void checkIfCreatedTrainerIsCorrect() {
        ReflectionTestUtils.setField(service, "profileService", new ProfileService());
        TrainerProfile trainerProfile = new TrainerProfile()
                .firstName("Oleg")
                .lastName("Burov")
                .specialization(com.gym.microservices.dto.TrainingType.BOX);

        when(userRepository.findByUsernamesStartedFrom("Oleg.Burov")).thenReturn(Collections.emptyList());
        when(trainingTypeRepository.findByName("Box")).thenReturn(of(new TrainingType(1L, "Box")));

        service.create(trainerProfile);

        verify(repository).save(trainerCaptor.capture());
        User user = trainerCaptor.getValue().getUser();

        assertTrue(user.getIsActive());
        assertTrue(user.getUsername().startsWith(user.getFirstName() + "." + user.getLastName()));
    }

    @Test
    void checkIfUpdatedTrainerIsCorrect() {
        User user = User.builder()
                .firstName("Maria")
                .lastName("Torska")
                .username("Maria.Torska")
                .password("1111111111")
                .isActive(true).build();

        Trainer trainer = Trainer.builder().specialization(new TrainingType(null, "Aerobic")).user(user).build();

        UpdateTrainerRequest request = new UpdateTrainerRequest()
                .firstName("Maria").lastName("Popova")
                .username("Maria.Torska")
                .specialization(com.gym.microservices.dto.TrainingType.AEROBIC);

        when(repository.findByUserUsername(any())).thenReturn(ofNullable(trainer));
        when(trainingTypeRepository.findByName(eq(com.gym.microservices.dto.TrainingType.AEROBIC.name())))
                .thenReturn(of(new TrainingType(1L, "Aerobic")));

        service.update(request);

        verify(repository).save(trainerCaptor.capture());

        User actualUser = trainerCaptor.getValue().getUser();

        assertEquals(trainer.getUser().getUsername(), actualUser.getUsername());
        assertEquals("Popova", actualUser.getLastName());
    }

    @Test
    void checkIfSelectedTrainerIsNull() {
        Exception exception = assertThrows(ServiceException.class, () -> service.findByUsername(null));
        assertTrue(exception.getMessage().contains("Username trainer cannot be null"));
    }

    @Test
    void checkChangingPasswordIfTrainerIsCorrect() {
        String username = "Inna.Ivova";
        String password = "1111111111";
        String hashPassword = passwordEncoder.generatePasswordHash(password);

        doNothing().when(userRepository).changePassword(eq(username), eq(hashPassword));

        service.changePassword(username, password);

        verify(userRepository).changePassword(eq(username), eq(hashPassword));
    }

    @Test
    void checkIfMatchingUsernameAndPasswordIsCorrect() {
        String username = "Inna.Ivova";
        String password = "111111111111";

        when(userRepository.retrievePasswordByUsername(eq(username))).thenReturn("test");
        when(passwordEncoder.generatePasswordHash(any())).thenReturn("test");

        boolean actual = service.matchUsernameAndPassword(username, password);

        verify(userRepository).retrievePasswordByUsername(username);
        assertTrue(actual);
    }

    @SuppressWarnings("unchecked")
    @Test
    void checkIfGetTrainingsByCriteriaIsCalled() {
        List<SearchCriteria> critters = List.of(new SearchCriteria("trainer.user.username", SearchOperation.EQUALITY, "test"));
        List<Training> expectedTrainings = List.of(Training.builder().name("test training").build());

        when(trainingRepository.findAll(any(Specification.class))).thenReturn(expectedTrainings);

        List<Training> actualTrainings = service.getTrainingsByCriteria(critters);

        assertNotNull(actualTrainings);
        assertEquals(1, actualTrainings.size());
        assertEquals("test training", actualTrainings.get(0).getName());

        verify(trainingRepository, times(1)).findAll(any(Specification.class));
    }

    @Test
    void checkChangingIsActiveIfTrainerIsCorrect() {
        String username = "Inna.Ivova";

        doNothing().when(userRepository).changeIsActive(username, true);

        boolean actual = service.changeIsActive(username, true);

        verify(userRepository).changeIsActive(username, true);
        assertTrue(actual);
    }
}