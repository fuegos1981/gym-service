package com.gym.microservices.service.impl;

import com.gym.microservices.dto.AddTrainingsRequest;
import com.gym.microservices.model.Trainee;
import com.gym.microservices.model.Trainer;
import com.gym.microservices.model.Training;
import com.gym.microservices.model.TrainingType;
import com.gym.microservices.model.User;
import com.gym.microservices.repository.TraineeRepository;
import com.gym.microservices.repository.TrainerRepository;
import com.gym.microservices.repository.TrainingRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static java.util.Optional.of;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TrainingServiceImplTest {

    @Mock
    private TrainingRepository repository;

    @Mock
    private TraineeRepository traineeRepository;

    @Mock
    private TrainerRepository trainerRepository;

    @InjectMocks
    private TrainingServiceImpl service;

    @Captor
    private ArgumentCaptor<Training> trainingCaptor;

    @Test
    void checkIfCreatedTrainingIsCorrect() {
        User traineeUser = User.builder()
                .firstName("Maria")
                .lastName("Burova")
                .username("Maria.Burova")
                .password("1111111111")
                .isActive(true).build();

        Trainee trainee = Trainee.builder()
                .dateOfBirth(LocalDate.of(1980, 11, 10))
                .address("Merefa")
                .user(traineeUser).build();

        User trainerUser = User.builder()
                .firstName("Oleg")
                .lastName("Ilin")
                .username("Oleg.Ilin")
                .password("2222222222")
                .isActive(true).build();

        Trainer trainer = Trainer.builder()
                .specialization(new TrainingType(null, "Aerobic"))
                .user(trainerUser).build();

        AddTrainingsRequest trainingCreateRequest = new AddTrainingsRequest()
                .traineeUsername(trainee.getUser().getUsername())
                .trainerUsername(trainer.getUser().getUsername())
                .trainingDuration(1.30)
                .trainingDate(LocalDate.of(2024, 9, 10))
                .trainingName("Modern box");

        when(traineeRepository.findByUserUsername(anyString())).thenReturn(of(trainee));
        when(trainerRepository.findByUserUsername(anyString())).thenReturn(of(trainer));

        service.create(trainingCreateRequest);

        verify(repository).save(trainingCaptor.capture());
        assertThat(trainingCaptor.getValue().getName()).isEqualTo("Modern box");
    }
}