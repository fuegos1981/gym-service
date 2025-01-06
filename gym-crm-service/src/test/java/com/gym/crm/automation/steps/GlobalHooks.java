package com.gym.crm.automation.steps;

import com.gym.crm.GymApplication;
import com.gym.crm.model.Trainee;
import com.gym.crm.model.User;
import com.gym.crm.repository.TraineeRepository;
import com.gym.crm.repository.UserRepository;
import io.cucumber.java.Before;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = GymApplication.class)
public class GlobalHooks {

    private final static String AUTH_USERNAME = "Test.User";
    private final static String AUTH_FIRSTNAME = "Test";
    private final static String AUTH_LASTNAME = "User";
    private final static String AUTH_PASSWORD = "password123";

    private final static String TRAINEE_USERNAME = "Maria.Ivanova";
    private final static String TRAINEE_FIRSTNAME = "Maria";
    private final static String TRAINEE_LASTNAME = "Ivanova";
    private final static String TRAINEE_PASSWORD = "password12";
    private final static String TRAINEE_ADDRESS = "Merefa";

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TraineeRepository traineeRepository;

    @Before("@specialHook")
    public void setupDatabase() {
        addAuthorizedUser();
        addTrainee();
    }

    private void addAuthorizedUser() {
        if (userRepository.findByUsername(AUTH_USERNAME).isEmpty()) {
            User user = User.builder()
                    .firstName(AUTH_FIRSTNAME)
                    .lastName(AUTH_LASTNAME)
                    .username(AUTH_USERNAME)
                    .password(AUTH_PASSWORD)
                    .isActive(true).build();

            userRepository.save(user);
        }
    }

    private void addTrainee() {
        if (userRepository.findByUsername(TRAINEE_USERNAME).isEmpty()) {
            User user = User.builder()
                    .firstName(TRAINEE_FIRSTNAME)
                    .lastName(TRAINEE_LASTNAME)
                    .username(TRAINEE_USERNAME)
                    .password(TRAINEE_PASSWORD)
                    .isActive(true).build();

            Trainee trainee = Trainee.builder()
                    .user(user)
                    .address(TRAINEE_ADDRESS)
                    .dateOfBirth(LocalDate.of(1980, 11, 10))
                    .build();

            traineeRepository.save(trainee);
        }
    }
}
