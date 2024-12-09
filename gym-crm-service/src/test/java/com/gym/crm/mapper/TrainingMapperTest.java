package com.gym.crm.mapper;

import com.gym.crm.dto.TraineeTrainingsResponseInner;
import com.gym.crm.dto.TrainerTrainingsResponseInner;
import com.gym.analytics.dto.TrainerWorkloadRequest;
import com.gym.crm.model.Trainee;
import com.gym.crm.model.Trainer;
import com.gym.crm.model.Training;
import com.gym.crm.model.TrainingType;
import com.gym.crm.model.User;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TrainingMapperTest {

    private static final User TRAINEE_USER = buildUser("Bogdan", "Petrov", "3333333333");
    private static final User TRAINER_USER = buildUser("Oleg", "Bodov", "4444444444");
    private static final Trainee TRAINEE = buildTrainee();
    private static final Trainer TRAINER = buildTrainer();
    private static final Training TRAINING = buildTraining();

    private final TrainingMapper mapper = new TrainingMapper();

    @Test
    void checkIfToTraineeTrainingsResponseInnerIsCorrect() {
        TraineeTrainingsResponseInner actual = mapper.toTraineeTrainingsResponseInner(TRAINING);

        assertEquals(TRAINING.getName(), actual.getTrainingName());
        assertEquals(TRAINING.getTrainingType().getName(), actual.getTrainingType().getValue());
        assertEquals(TRAINING.getTrainingDate(), actual.getTrainingDate());
        assertEquals(TRAINING.getTrainer().getUser().getUsername(), actual.getTrainerName());
        assertEquals(TRAINING.getTrainingDate(), actual.getTrainingDate());
        assertEquals(TRAINING.getDuration(), actual.getTrainingDuration());
    }

    @Test
    void checkIfToTrainerTrainingsResponseInnerIsCorrect() {
        TrainerTrainingsResponseInner actual = mapper.toTrainerTrainingsResponseInner(TRAINING);

        assertEquals(TRAINING.getName(), actual.getTrainingName());
        assertEquals(TRAINING.getTrainingType().getName(), actual.getTrainingType().getValue());
        assertEquals(TRAINING.getTrainingDate(), actual.getTrainingDate());
        assertEquals(TRAINING.getTrainee().getUser().getUsername(), actual.getTraineeName());
        assertEquals(TRAINING.getTrainingDate(), actual.getTrainingDate());
        assertEquals(TRAINING.getDuration(), actual.getTrainingDuration());
    }

    @Test
    void checkIfToTrainerWorkloadRequestIsCorrect() {
        String action = "ADD";

        TrainerWorkloadRequest actual = mapper.toTrainerWorkloadRequest(TRAINING, action);

        assertEquals(TRAINING.getTrainer().getUser().getUsername(), actual.getUsername());
        assertEquals(TRAINING.getTrainer().getUser().getFirstName(), actual.getFirstName());
        assertEquals(TRAINING.getTrainer().getUser().getLastName(), actual.getLastName());
        assertEquals(TRAINING.getTrainingDate(), actual.getTrainingDate());
        assertEquals(action, actual.getActionType().toString());
        assertEquals(TRAINING.getDuration(), actual.getTrainingDuration());
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