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

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TraineeRepository traineeRepository;

    @Before("@specialHook")
    public void setupDatabase() {
        if (userRepository.findByUsername("Test.User").isEmpty()) {
            User user = User.builder()
                    .firstName("Test")
                    .lastName("User")
                    .username("Test.User")
                    .password("password123")
                    .isActive(true).build();

            userRepository.save(user);
        }

        if (userRepository.findByUsername("Maria.Ivanova").isEmpty()) {
            User user = User.builder()
                    .firstName("Maria")
                    .lastName("Ivanova")
                    .username("Maria.Ivanova")
                    .password("password12")
                    .isActive(true).build();

            Trainee trainee = Trainee.builder()
                    .user(user)
                    .address("Merefa")
                    .dateOfBirth(LocalDate.of(1980, 11, 10))
                    .build();

            traineeRepository.save(trainee);
        }
    }
}
