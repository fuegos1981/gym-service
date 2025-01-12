package com.gym.automation.cucumber.steps;

import com.gym.analytics.TrainingHoursTrackerApplication;
import com.gym.automation.AutoApplication;
import com.gym.automation.repository.MySqlRepository;
import com.gym.crm.GymApplication;
import io.cucumber.java.Before;
import io.cucumber.spring.CucumberContextConfiguration;
import lombok.AllArgsConstructor;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.sql.SQLException;
import java.time.LocalDate;

@ExtendWith(SpringExtension.class)
@CucumberContextConfiguration
@ContextConfiguration(classes = AutoApplication.class)
@SpringBootTest
@AllArgsConstructor
public class GlobalHooks {

    private final static String AUTH_USERNAME = "Test.User";
    private final static String AUTH_FIRSTNAME = "Test";
    private final static String AUTH_LASTNAME = "User";
    private final static String AUTH_PASSWORD = "2579B5B0CEC6BE9B09C7B06CD9446A9F";//6666666666

    private final static String TRAINEE_USERNAME = "Maria.Ivanova";
    private final static String TRAINEE_FIRSTNAME = "Maria";
    private final static String TRAINEE_LASTNAME = "Ivanova";
    private final static String TRAINEE_PASSWORD = "2E78D671A5E0A2B817FE5967CFBD61C6";//4444444444
    private final static String TRAINEE_ADDRESS = "Merefa";

    private final static String TRAINER_USERNAME = "Petr.Andreev";
    private final static String TRAINER_FIRSTNAME = "Petr";
    private final static String TRAINER_LASTNAME = "Andreev";
    private final static String TRAINER_PASSWORD = "E31A2DD1B0E22669737B3E226F45982B";//5555555555
    private final static String TRAINING_TYPE = "Box";

    private final MySqlRepository repository;
    private final MongoTemplate mongoTemplate;

    @Before
    public void setupDatabase() throws SQLException {
        resetDatabaseMongo();

        startCrm();
        startHours();

        addAuthorizedUser();
        addTrainee();
        addTrainer();
    }

    private void resetDatabaseMongo() {
        for (String collectionName : mongoTemplate.getCollectionNames()) {
            mongoTemplate.dropCollection(collectionName);
        }
    }

    private void startCrm() {
        SpringApplication firstApp = new SpringApplication(GymApplication.class);
        firstApp.setAdditionalProfiles("autotest");
        firstApp.run();
    }

    private void startHours() {
        SpringApplication secondApp = new SpringApplication(TrainingHoursTrackerApplication.class);
        secondApp.setAdditionalProfiles("hoursautotest");
        secondApp.run();
    }

    private void addAuthorizedUser() throws SQLException {
        repository.save(MySqlRepository.INSERT_USER, AUTH_FIRSTNAME, AUTH_LASTNAME, AUTH_USERNAME, AUTH_PASSWORD, true);
    }

    private void addTrainee() throws SQLException {
        Long idUser = repository.save(MySqlRepository.INSERT_USER, TRAINEE_FIRSTNAME, TRAINEE_LASTNAME, TRAINEE_USERNAME, TRAINEE_PASSWORD, true);

        repository.save(MySqlRepository.INSERT_TRAINEE, idUser, LocalDate.of(1980, 11, 10), TRAINEE_ADDRESS);
    }

    private void addTrainer() throws SQLException {
        Long idTrainingType = repository.read(TRAINING_TYPE);
        if (idTrainingType.equals(-1L)) {
            idTrainingType = repository.save(MySqlRepository.INSERT_TRAINING_TYPE, TRAINING_TYPE);
        }

        Long idUser = repository.save(MySqlRepository.INSERT_USER, TRAINER_FIRSTNAME, TRAINER_LASTNAME, TRAINER_USERNAME, TRAINER_PASSWORD, true);

        repository.save(MySqlRepository.INSERT_TRAINER, idUser, idTrainingType);
    }
}
