package com.gym.crm.mapper;

import com.gym.crm.dto.GetTraineeResponse;
import com.gym.crm.dto.UpdateTraineeResponse;
import com.gym.crm.model.Trainee;
import com.gym.crm.model.Trainer;
import com.gym.crm.model.TrainingType;
import com.gym.crm.model.User;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TraineeMapperTest {

    private static final User TRAINEE_USER = buildUser("Bogdan", "Petrov", "3333333333");
    private static final User TRAINER_USER = buildUser("Oleg", "Bodov", "4444444444");
    private static final Trainer TRAINER = buildTrainer();
    private static final Trainee TRAINEE = buildTrainee();

    private static final TraineeMapper mapper = new TraineeMapper();

    @Test
    void checkIfToUpdateTraineeResponseIsCorrect() {
        UpdateTraineeResponse actual = mapper.toUpdateTraineeResponse(TRAINEE);

        assertEquals(TRAINEE_USER.getUsername(), actual.getUsername());
        assertEquals(TRAINEE_USER.getFirstName(), actual.getFirstName());
        assertEquals(TRAINEE_USER.getLastName(), actual.getLastName());
        assertEquals(TRAINEE_USER.getIsActive(), actual.getIsActive());
        assertEquals(TRAINEE.getAddress(), actual.getAddress());
    }

    @Test
    void checkIfToGetTraineeResponseIsCorrect() {
        GetTraineeResponse actual = mapper.toGetTraineeResponse(TRAINEE);

        assertEquals(TRAINEE_USER.getFirstName(), actual.getFirstName());
        assertEquals(TRAINEE_USER.getLastName(), actual.getLastName());
        assertEquals(TRAINEE_USER.getIsActive(), actual.getIsActive());
        assertEquals(TRAINEE.getAddress(), actual.getAddress());
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
                .trainers(Set.of(TRAINER))
                .build();
    }

    private static Trainer buildTrainer() {
        return Trainer.builder().user(TRAINER_USER)
                .specialization(new TrainingType(1L, "Box"))
                .build();
    }
}