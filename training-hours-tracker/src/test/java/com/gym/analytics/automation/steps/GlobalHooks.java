package com.gym.analytics.automation.steps;

import com.gym.analytics.TrainingHoursTrackerApplication;
import com.gym.analytics.model.Trainer;
import com.gym.analytics.repository.TrainerReportRepository;
import com.gym.analytics.service.impl.TrainingSummaryManager;
import io.cucumber.java.Before;
import io.cucumber.spring.CucumberContextConfiguration;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;

@ExtendWith(SpringExtension.class)
@CucumberContextConfiguration
@ContextConfiguration(classes = TrainingHoursTrackerApplication.class)
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class GlobalHooks {

    private final static String FIRST_NAME = "John";
    private final static String LAST_NAME = "Doe";
    private final static String USERNAME = "John.Doe";

    @Autowired
    private TrainerReportRepository repository;
    @Autowired
    private TrainingSummaryManager manager;

    @Before("@specialHook")
    public void setupDatabase() {
        if (repository.findByUsername(USERNAME).isPresent()) {
            return;
        }

        Trainer trainer = Trainer.builder()
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .username(USERNAME)
                .status(true)
                .build();

        manager.addDurationToYearlySummary(trainer, LocalDate.of(2024, 12, 21), 2.0);
        manager.addDurationToYearlySummary(trainer, LocalDate.of(2024, 11, 21), 3.0);

        repository.save(trainer);
    }
}
