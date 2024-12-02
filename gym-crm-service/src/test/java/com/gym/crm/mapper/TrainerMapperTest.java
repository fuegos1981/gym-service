package com.gym.crm.mapper;

import com.gym.crm.dto.GetTrainerResponse;
import com.gym.crm.dto.GetTrainersResponseInner;
import com.gym.crm.dto.UpdateTraineesTrainersResponseInner;
import com.gym.crm.dto.UpdateTrainerResponse;
import com.gym.crm.model.Trainee;
import com.gym.crm.model.Trainer;
import com.gym.crm.model.TrainingType;
import com.gym.crm.model.User;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TrainerMapperTest {

    private static final User TRAINEE_USER = buildUser("Bogdan", "Petrov", "3333333333");
    private static final User TRAINER_USER = buildUser("Oleg", "Bodov", "4444444444");
    private static final Trainee TRAINEE = buildTrainee();
    private static final Trainer TRAINER = buildTrainer();

    private static final TrainerMapper mapper = new TrainerMapper();

    @Test
    void checkIfToGetTrainersResponseInnerIsCorrect() {
        GetTrainersResponseInner actual = mapper.toGetTrainersResponseInner(TRAINER);

        assertEquals(TRAINER_USER.getUsername(), actual.getUsername());
        assertEquals(TRAINER_USER.getFirstName(), actual.getFirstName());
        assertEquals(TRAINER_USER.getLastName(), actual.getLastName());
        assertEquals(TRAINER.getSpecialization().getName(), actual.getSpecialization().getValue());
    }

    @Test
    void checkIfToGetTrainerResponseIsCorrect() {
        GetTrainerResponse actual = mapper.toGetTrainerResponse(TRAINER);

        assertEquals(TRAINER_USER.getIsActive(), actual.getIsActive());
        assertEquals(TRAINER_USER.getFirstName(), actual.getFirstName());
        assertEquals(TRAINER_USER.getLastName(), actual.getLastName());
        assertEquals(TRAINER.getTrainees().size(), actual.getTrainees().size());
        assertEquals(TRAINER.getSpecialization().getName(), actual.getSpecialization().getValue());
    }

    @Test
    void checkIfToUpdateTrainerResponseIsCorrect() {
        UpdateTrainerResponse actual = mapper.toUpdateTrainerResponse(TRAINER);

        assertEquals(TRAINER_USER.getIsActive(), actual.getIsActive());
        assertEquals(TRAINER_USER.getUsername(), actual.getUsername());
        assertEquals(TRAINER_USER.getFirstName(), actual.getFirstName());
        assertEquals(TRAINER_USER.getLastName(), actual.getLastName());
        assertEquals(TRAINER.getTrainees().size(), actual.getTrainees().size());
        assertEquals(TRAINER.getSpecialization().getName(), actual.getSpecialization().getValue());
    }

    @Test
    void checkIfToUpdateTraineesTrainersResponseInnerIsCorrect() {
        UpdateTraineesTrainersResponseInner actual = mapper.toUpdateTraineesTrainersResponseInner(TRAINER);

        assertEquals(TRAINER_USER.getUsername(), actual.getUsername());
        assertEquals(TRAINER_USER.getFirstName(), actual.getFirstName());
        assertEquals(TRAINER_USER.getLastName(), actual.getLastName());
        assertEquals(TRAINER.getSpecialization().getName(), actual.getSpecialization().getValue());
    }

    private static User buildUser(String firstName, String lastname, String password) {
        return User.builder()
                .firstName(firstName)
                .lastName(lastname)
                .username(firstName + "." + lastname)
                .password(password)
                .isActive(true).build();
    }

    private static Trainee buildTrainee() {
        return Trainee.builder()
                .dateOfBirth(LocalDate.of(1980, 11, 10))
                .address("Merefa")
                .user(TRAINEE_USER)
                .build();
    }

    private static Trainer buildTrainer() {
        return Trainer.builder().user(TRAINER_USER).
                trainees(Set.of(TRAINEE))
                .specialization(new TrainingType(1L, "Box")).build();
    }
}