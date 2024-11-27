package com.gym.microservices.impl;

import com.gym.microservices.dto.TrainerMonthlySummaryResponse;
import com.gym.microservices.dto.TrainerWorkloadRequest;
import com.gym.microservices.mapper.TrainerWorkloadMapper;
import com.gym.microservices.model.TrainerWorkload;
import com.gym.microservices.repository.TrainerWorkloadRepository;
import com.gym.microservices.service.impl.TrainerServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
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
        TrainerWorkloadRequest request = TrainerWorkloadRequest.builder()
                .username(USERNAME)
                .firstName("John")
                .lastName("Doe")
                .isActive(true)
                .trainingDate(LocalDate.of(2024, 11, 23))
                .trainingDuration(120)
                .actionType("ADD")
                .build();

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
        assertEquals(TrainerMonthlySummaryResponse.Status.ACTIVE, response.getStatus());
        assertNotNull(response.getYearlySummaries());
        assertEquals(1, response.getYearlySummaries().size());

        TrainerMonthlySummaryResponse.YearlySummary yearlySummary = response.getYearlySummaries().get(0);
        assertEquals(2024, yearlySummary.getYear());
        assertEquals(2, yearlySummary.getMonthlySummaries().size());

        TrainerMonthlySummaryResponse.MonthlySummary novemberSummary = yearlySummary.getMonthlySummaries().get(0);
        assertEquals(120, novemberSummary.getTotalDuration());
        assertEquals(java.time.Month.NOVEMBER, novemberSummary.getMonth());

        TrainerMonthlySummaryResponse.MonthlySummary decemberSummary = yearlySummary.getMonthlySummaries().get(1);
        assertEquals(90, decemberSummary.getTotalDuration());
        assertEquals(java.time.Month.DECEMBER, decemberSummary.getMonth());
    }

    @Test
    void calculateMonthlySummary_ShouldReturnNull_WhenNoWorkloads() {
        when(repository.findByUsernameOrderByTrainingDateAsc(USERNAME)).thenReturn(new ArrayList<>());

        TrainerMonthlySummaryResponse response = service.calculateMonthlySummary(USERNAME);

        assertNull(response);
    }

    private TrainerWorkload buildTrainerWorkload(LocalDate trainingDate, int duration) {
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