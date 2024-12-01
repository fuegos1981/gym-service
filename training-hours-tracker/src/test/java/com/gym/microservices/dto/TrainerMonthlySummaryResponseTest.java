package com.gym.microservices.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class TrainerMonthlySummaryResponseTest {

    private static final LocalDate FIRST_TRAINING_DATE = LocalDate.of(2024, 11, 23);
    private static final LocalDate SECOND_TRAINING_DATE = LocalDate.of(2024, 12, 10);
    private static final double FIRST_DURATION = 120;
    private static final double SECOND_DURATION = 90;

    private TrainerMonthlySummaryResponse summaryResponse;

    @BeforeEach
    void setUp() {
        summaryResponse = TrainerMonthlySummaryResponse.builder()
                .username("trainer123")
                .firstName("John")
                .lastName("Doe")
                .Status(TrainerMonthlySummaryResponse.Status.ACTIVE)
                .yearlySummaries(null)
                .build();
    }

    @Test
    void addDurationToYearlySummary_ShouldAddNewYearlySummary_WhenYearDoesNotExist() {
        summaryResponse.addDurationToYearlySummary(FIRST_TRAINING_DATE, FIRST_DURATION);

        assertNotNull(summaryResponse.getYearlySummaries());
        assertEquals(1, summaryResponse.getYearlySummaries().size());
        TrainerMonthlySummaryResponse.YearlySummary yearlySummary = summaryResponse.getYearlySummaries().get(0);
        assertEquals(2024, yearlySummary.getYear());
        assertEquals(1, yearlySummary.getMonthlySummaries().size());
        TrainerMonthlySummaryResponse.MonthlySummary monthlySummary = yearlySummary.getMonthlySummaries().get(0);
        assertEquals(Month.NOVEMBER, monthlySummary.getMonth());
        assertEquals(120, monthlySummary.getTotalDuration());
    }

    @Test
    void addDurationToYearlySummary_ShouldUpdateExistingMonthlySummary_WhenMonthExists() {
        summaryResponse.addDurationToYearlySummary(FIRST_TRAINING_DATE, FIRST_DURATION);

        double additionalDuration = 60.00;

        summaryResponse.addDurationToYearlySummary(FIRST_TRAINING_DATE, additionalDuration);

        assertNotNull(summaryResponse.getYearlySummaries());
        TrainerMonthlySummaryResponse.YearlySummary yearlySummary = summaryResponse.getYearlySummaries().get(0);
        assertEquals(1, yearlySummary.getMonthlySummaries().size());
        TrainerMonthlySummaryResponse.MonthlySummary monthlySummary = yearlySummary.getMonthlySummaries().get(0);
        assertEquals(Month.NOVEMBER, monthlySummary.getMonth());
        assertEquals(180, monthlySummary.getTotalDuration());
    }

    @Test
    void addDurationToYearlySummary_ShouldAddNewMonthlySummary_WhenMonthDoesNotExist() {
        summaryResponse.addDurationToYearlySummary(FIRST_TRAINING_DATE, FIRST_DURATION);
        summaryResponse.addDurationToYearlySummary(SECOND_TRAINING_DATE, SECOND_DURATION);

        assertNotNull(summaryResponse.getYearlySummaries());
        TrainerMonthlySummaryResponse.YearlySummary yearlySummary = summaryResponse.getYearlySummaries().get(0);
        assertEquals(2, yearlySummary.getMonthlySummaries().size());

        List<TrainerMonthlySummaryResponse.MonthlySummary> monthlySummaries = yearlySummary.getMonthlySummaries();
        assertEquals(120, monthlySummaries.get(0).getTotalDuration());
        assertEquals(90, monthlySummaries.get(1).getTotalDuration());
    }

    @Test
    void addDurationToYearlySummary_ShouldHandleMultipleYears() {
        summaryResponse.addDurationToYearlySummary(FIRST_TRAINING_DATE, FIRST_DURATION);
        summaryResponse.addDurationToYearlySummary(LocalDate.of(2023, 12, 10), SECOND_DURATION);

        assertNotNull(summaryResponse.getYearlySummaries());
        assertEquals(2, summaryResponse.getYearlySummaries().size());

        TrainerMonthlySummaryResponse.YearlySummary summary2023 = summaryResponse.getYearlySummaries().stream()
                .filter(y -> y.getYear() == 2023)
                .findFirst()
                .orElse(null);
        assertNotNull(summary2023);
        assertEquals(1, summary2023.getMonthlySummaries().size());
        assertEquals(90, summary2023.getMonthlySummaries().get(0).getTotalDuration());

        TrainerMonthlySummaryResponse.YearlySummary summary2024 = summaryResponse.getYearlySummaries().stream()
                .filter(y -> y.getYear() == 2024)
                .findFirst()
                .orElse(null);
        assertNotNull(summary2024);
        assertEquals(1, summary2024.getMonthlySummaries().size());
        assertEquals(120, summary2024.getMonthlySummaries().get(0).getTotalDuration());
    }
}