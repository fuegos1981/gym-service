package com.gym.analytics.service.impl;

import com.gym.analytics.model.Trainer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class TrainingSummaryManagerTest {

    private static final LocalDate FIRST_TRAINING_DATE = LocalDate.of(2024, 11, 23);
    private static final LocalDate SECOND_TRAINING_DATE = LocalDate.of(2024, 12, 10);
    private static final double FIRST_DURATION = 120;
    private static final double SECOND_DURATION = 90;

    private Trainer trainer;
    private final TrainingSummaryManager manager = new TrainingSummaryManager();

    @BeforeEach
    void setUp() {
        trainer = Trainer.builder()
                .username("trainer123")
                .firstName("John")
                .lastName("Doe")
                .status(true)
                .years(null)
                .build();
    }

    @Test
    void addDurationToYearlySummary_ShouldAddNewYearlySummary_WhenYearDoesNotExist() {
        manager.addDurationToYearlySummary(trainer, FIRST_TRAINING_DATE, FIRST_DURATION);

        assertNotNull(trainer.getYears());
        assertEquals(1, trainer.getYears().size());
        Trainer.Year yearlySummary = trainer.getYears().get(0);
        assertEquals(2024, yearlySummary.getYear());
        assertEquals(1, yearlySummary.getMonthlySummaries().size());
        Trainer.Year.MonthlySummary monthlySummary = yearlySummary.getMonthlySummaries().get(0);
        assertEquals(Month.NOVEMBER, monthlySummary.getMonth());
        assertEquals(120, monthlySummary.getTrainingSummaryDuration());
    }

    @Test
    void addDurationToYearlySummary_ShouldUpdateExistingMonthlySummary_WhenMonthExists() {
        manager.addDurationToYearlySummary(trainer, FIRST_TRAINING_DATE, FIRST_DURATION);

        double additionalDuration = 60.00;

        manager.addDurationToYearlySummary(trainer, FIRST_TRAINING_DATE, additionalDuration);

        assertNotNull(trainer.getYears());
        Trainer.Year yearlySummary = trainer.getYears().get(0);
        assertEquals(1, yearlySummary.getMonthlySummaries().size());
        Trainer.Year.MonthlySummary monthlySummary = yearlySummary.getMonthlySummaries().get(0);
        assertEquals(Month.NOVEMBER, monthlySummary.getMonth());
        assertEquals(180, monthlySummary.getTrainingSummaryDuration());
    }

    @Test
    void addDurationToYearlySummary_ShouldAddNewMonthlySummary_WhenMonthDoesNotExist() {
        manager.addDurationToYearlySummary(trainer, FIRST_TRAINING_DATE, FIRST_DURATION);
        manager.addDurationToYearlySummary(trainer, SECOND_TRAINING_DATE, SECOND_DURATION);

        assertNotNull(trainer.getYears());
        Trainer.Year yearlySummary = trainer.getYears().get(0);
        assertEquals(2, yearlySummary.getMonthlySummaries().size());

        List<Trainer.Year.MonthlySummary> monthlySummaries = yearlySummary.getMonthlySummaries();
        assertEquals(120, monthlySummaries.get(0).getTrainingSummaryDuration());
        assertEquals(90, monthlySummaries.get(1).getTrainingSummaryDuration());
    }

    @Test
    void addDurationToYearlySummary_ShouldHandleMultipleYears() {
        manager.addDurationToYearlySummary(trainer, FIRST_TRAINING_DATE, FIRST_DURATION);
        manager.addDurationToYearlySummary(trainer, LocalDate.of(2023, 12, 10), SECOND_DURATION);

        assertNotNull(trainer.getYears());
        assertEquals(2, trainer.getYears().size());

        Trainer.Year summary2023 = trainer.getYears().stream()
                .filter(y -> y.getYear() == 2023)
                .findFirst()
                .orElse(null);
        assertNotNull(summary2023);
        assertEquals(1, summary2023.getMonthlySummaries().size());
        assertEquals(90, summary2023.getMonthlySummaries().get(0).getTrainingSummaryDuration());

        Trainer.Year summary2024 = trainer.getYears().stream()
                .filter(y -> y.getYear() == 2024)
                .findFirst()
                .orElse(null);
        assertNotNull(summary2024);
        assertEquals(1, summary2024.getMonthlySummaries().size());
        assertEquals(120, summary2024.getMonthlySummaries().get(0).getTrainingSummaryDuration());
    }
}