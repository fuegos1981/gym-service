package com.gym.analytics.mapper;

import com.gym.analytics.dto.MonthlySummary;
import com.gym.analytics.dto.TrainerMonthlySummaryResponse;
import com.gym.analytics.dto.YearlySummary;
import com.gym.analytics.model.Trainer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Month;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class TrainerMapperTest {

    private TrainerMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new TrainerMapper();
    }

    @Test
    void checkIfToGetTrainerMonthlySummaryResponseIsWorked() {
        Trainer.Year.MonthlySummary januarySummary = new Trainer.Year.MonthlySummary();
        januarySummary.setMonth(Month.JANUARY);
        januarySummary.setTrainingSummaryDuration(15.5);

        Trainer.Year year2023 = new Trainer.Year();
        year2023.setYear(2023);
        year2023.setMonthlySummaries(List.of(januarySummary));

        Trainer trainer = buildTrainer(true, List.of(year2023));

        TrainerMonthlySummaryResponse response = mapper.toGetTrainerMonthlySummaryResponse(trainer);

        assertNotNull(response);
        assertEquals("John", response.getFirstName());
        assertEquals("Doe", response.getLastName());
        assertEquals("J.Doe", response.getUsername());
        assertEquals(TrainerMonthlySummaryResponse.StatusEnum.ACTIVE, response.getStatus());

        assertNotNull(response.getYearlySummaries());
        assertEquals(1, response.getYearlySummaries().size());

        YearlySummary yearlySummary = response.getYearlySummaries().get(0);
        assertEquals(2023, yearlySummary.getYear());

        assertNotNull(yearlySummary.getMonthlySummaries());
        assertEquals(1, yearlySummary.getMonthlySummaries().size());

        MonthlySummary monthlySummary = yearlySummary.getMonthlySummaries().get(0);
        assertEquals(MonthlySummary.MonthEnum.JANUARY, monthlySummary.getMonth());
        assertEquals(15.5, monthlySummary.getTotalDuration());
    }

    @Test
    void checkIfToGetTrainerMonthlySummaryResponseWithNullYears() {
        Trainer trainer = buildTrainer(true, null);

        TrainerMonthlySummaryResponse response = mapper.toGetTrainerMonthlySummaryResponse(trainer);

        assertNotNull(response);
        assertEquals("John", response.getFirstName());
        assertEquals("Doe", response.getLastName());
        assertEquals("J.Doe", response.getUsername());
        assertEquals(TrainerMonthlySummaryResponse.StatusEnum.ACTIVE, response.getStatus());
        assertNull(response.getYearlySummaries());
    }

    @Test
    void checkIfToGetTrainerMonthlySummaryResponseWithNullMonthlySummaries() {
        Trainer.Year year2023 = new Trainer.Year();
        year2023.setYear(2023);
        year2023.setMonthlySummaries(null);

        Trainer trainer = buildTrainer(false, List.of(year2023));

        TrainerMonthlySummaryResponse response = mapper.toGetTrainerMonthlySummaryResponse(trainer);

        assertNotNull(response);
        assertEquals("John", response.getFirstName());
        assertEquals("Doe", response.getLastName());
        assertEquals("J.Doe", response.getUsername());
        assertEquals(TrainerMonthlySummaryResponse.StatusEnum.NOT_ACTIVE, response.getStatus());

        assertNotNull(response.getYearlySummaries());
        assertEquals(1, response.getYearlySummaries().size());

        YearlySummary yearlySummary = response.getYearlySummaries().get(0);
        assertEquals(2023, yearlySummary.getYear());
        assertNull(yearlySummary.getMonthlySummaries());
    }

    private static Trainer buildTrainer(boolean status, List<Trainer.Year> years) {
        return Trainer.builder()
                .firstName("John")
                .lastName("Doe")
                .username("J.Doe")
                .status(status)
                .years(years)
                .build();
    }
}