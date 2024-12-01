package com.gym.microservices.service.impl;

import com.gym.microservices.controller.TrainingHoursTrackerClient;
import com.gym.microservices.dto.TrainerWorkloadRequest;
import com.gym.microservices.mapper.TrainingMapper;
import com.gym.microservices.model.Trainee;
import com.gym.microservices.model.Trainer;
import com.gym.microservices.model.Training;
import com.gym.microservices.model.TrainingType;
import com.gym.microservices.model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AnalyticsSenderTest {

    private static final User TRAINEE_USER = buildUser("Bogdan", "Petrov", "3333333333");
    private static final User TRAINER_USER = buildUser("Oleg", "Bodov", "4444444444");
    private static final Trainee TRAINEE = buildTrainee();
    private static final Trainer TRAINER = buildTrainer();
    private static final Training TRAINING = buildTraining();
    private static final TrainerWorkloadRequest TRAINER_WORKLOAD = buildTrainerWorkloadRequest();

    @Mock
    private TrainingMapper mapper;

    @Mock
    private TrainingHoursTrackerClient trackerClient;

    @InjectMocks
    private AnalyticsSender analyticsSender;

    @Test
    void checkIfProcessWorkloadForSingleTrainingIsCorrect() {
        String action = "ADD";
        ResponseEntity<String> mockResponse = ResponseEntity.ok("Success");

        when(mapper.toTrainerWorkloadRequest(TRAINING, action)).thenReturn(TRAINER_WORKLOAD);
        when(trackerClient.sendWorkload(TRAINER_WORKLOAD)).thenReturn(mockResponse);

        String result = analyticsSender.processWorkload(TRAINING, action);

        assertEquals("Success", result);
        verify(mapper).toTrainerWorkloadRequest(TRAINING, action);
        verify(trackerClient).sendWorkload(TRAINER_WORKLOAD);
    }

    @Test
    void checkIfProcessWorkloadForListTrainingIsCorrect() {
        List<Training> trainings = List.of(TRAINING);
        String action = "ADD";

        when(mapper.toTrainerWorkloadRequest(TRAINING, action)).thenReturn(TRAINER_WORKLOAD);

        String result = analyticsSender.processWorkload(trainings, action);

        assertEquals("", result);
        verify(mapper).toTrainerWorkloadRequest(TRAINING, action);
        verify(trackerClient).sendWorkload(TRAINER_WORKLOAD);
    }

    private static TrainerWorkloadRequest buildTrainerWorkloadRequest() {
        return TrainerWorkloadRequest.builder()
                .firstName("Oleg")
                .lastName("Bodov")
                .username("Oleg.Bodov")
                .trainingDate(LocalDate.of(2024, 10, 10))
                .trainingDuration(2.0)
                .actionType("ADD")
                .isActive(true).build();
    }

    private static User buildUser(String firstName, String lastname, String password) {
        return User.builder()
                .firstName(firstName)
                .lastName(lastname)
                .username(firstName + "." + lastname)
                .password(password)
                .isActive(true).build();
    }

    private static Training buildTraining() {
        return Training.builder()
                .trainee(TRAINEE)
                .trainer(TRAINER)
                .trainingType(new TrainingType(1L, "Box"))
                .trainingDate(LocalDate.of(2024, 10, 10))
                .duration(2.0)
                .build();
    }

    private static Trainee buildTrainee() {
        return Trainee.builder()
                .dateOfBirth(LocalDate.of(1980, 11, 10))
                .address("Merefa")
                .user(TRAINEE_USER)
                .build();
    }

    private static Trainer buildTrainer() {
        return Trainer.builder().user(TRAINER_USER).specialization(new TrainingType(1L, "Box")).build();
    }
}