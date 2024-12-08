package com.gym.analytics.impl;

import com.gym.analytics.dto.MonthlySummary;
import com.gym.analytics.dto.TrainerMonthlySummaryResponse;
import com.gym.analytics.dto.TrainerWorkloadRequest;
import com.gym.analytics.dto.YearlySummary;
import com.gym.analytics.mapper.TrainerWorkloadMapper;
import com.gym.analytics.model.TrainerWorkload;
import com.gym.analytics.repository.TrainerWorkloadRepository;
import com.gym.analytics.service.impl.TrainerServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TrainerServiceImplTest {

    private static final String USERNAME = "trainer123";

    @Mock
    private TrainerWorkloadRepository repository;
    @Mock
    private TrainerWorkloadMapper mapper;
    @InjectMocks
    private TrainerServiceImpl service;

    @Test
    void saveWorkload_ShouldSaveMappedEntity() {
        TrainerWorkloadRequest request = new TrainerWorkloadRequest()
                .username(USERNAME)
                .firstName("John")
                .lastName("Doe")
                .isActive(true)
                .trainingDate(LocalDate.of(2024, 11, 23))
                .trainingDuration(120.00)
                .actionType(TrainerWorkloadRequest.ActionTypeEnum.ADD);

        TrainerWorkload mappedWorkload = buildTrainerWorkload(LocalDate.of(2024, 11, 23), 120);

        when(mapper.toGetTrainerWorkload(request)).thenReturn(mappedWorkload);

        service.saveWorkload(request);

        ArgumentCaptor<TrainerWorkload> captor = ArgumentCaptor.forClass(TrainerWorkload.class);
        verify(repository, times(1)).save(captor.capture());
        TrainerWorkload savedWorkload = captor.getValue();

        assertEquals(USERNAME, savedWorkload.getUsername());
        assertEquals(120, savedWorkload.getTrainingDuration());
        assertEquals(LocalDate.of(2024, 11, 23), savedWorkload.getTrainingDate());
    }

    @Test
    void calculateMonthlySummary_ShouldReturnCorrectSummary() {
        List<TrainerWorkload> workloads = new ArrayList<>();
        workloads.add(buildTrainerWorkload(LocalDate.of(2024, 11, 23), 120));
        workloads.add(buildTrainerWorkload(LocalDate.of(2024, 12, 1), 90));

        when(repository.findByUsernameOrderByTrainingDateAsc(USERNAME)).thenReturn(workloads);

        TrainerMonthlySummaryResponse response = service.calculateMonthlySummary(USERNAME);

        assertNotNull(response);
        assertEquals(USERNAME, response.getUsername());
        assertEquals("John", response.getFirstName());
        assertEquals("Doe", response.getLastName());
        assertEquals(TrainerMonthlySummaryResponse.StatusEnum.ACTIVE, response.getStatus());
        assertNotNull(response.getYearlySummaries());
        assertEquals(1, response.getYearlySummaries().size());

        YearlySummary yearlySummary = response.getYearlySummaries().get(0);
        assertEquals(2024, yearlySummary.getYear());
        assertEquals(2, yearlySummary.getMonthlySummaries().size());

        MonthlySummary novemberSummary = yearlySummary.getMonthlySummaries().get(0);
        assertEquals(120, novemberSummary.getTotalDuration());
        assertEquals(MonthlySummary.MonthEnum.NOVEMBER, novemberSummary.getMonth());

        MonthlySummary decemberSummary = yearlySummary.getMonthlySummaries().get(1);
        assertEquals(90, decemberSummary.getTotalDuration());
        assertEquals(MonthlySummary.MonthEnum.DECEMBER, decemberSummary.getMonth());
    }

    @Test
    void calculateMonthlySummary_ShouldReturnNull_WhenNoWorkloads() {
        when(repository.findByUsernameOrderByTrainingDateAsc(USERNAME)).thenReturn(new ArrayList<>());

        TrainerMonthlySummaryResponse response = service.calculateMonthlySummary(USERNAME);

        assertNull(response);
    }

    private TrainerWorkload buildTrainerWorkload(LocalDate trainingDate, double duration) {
        return TrainerWorkload.builder()
                .username(USERNAME)
                .firstName("John")
                .lastName("Doe")
                .isActive(true)
                .trainingDate(trainingDate)
                .trainingDuration(duration)
                .build();
    }
}