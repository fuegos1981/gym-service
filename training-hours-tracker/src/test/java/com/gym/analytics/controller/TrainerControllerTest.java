package com.gym.analytics.controller;

import com.gym.analytics.dto.TrainerMonthlySummaryResponse;
import com.gym.analytics.dto.TrainerWorkloadRequest;
import com.gym.analytics.service.TrainerService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TrainerControllerTest {

    private final static String USERNAME = "trainer123";

    @Mock
    private TrainerService trainerService;

    @InjectMocks
    private TrainerController trainerController;

    @Test
    void handleWorkload_ShouldProcessWorkloadSuccessfully() {
        TrainerWorkloadRequest workloadRequest = TrainerWorkloadRequest.builder()
                .username(USERNAME)
                .trainingDate(LocalDate.of(2024, 11, 23))
                .trainingDuration(120.00)
                .build();

        doNothing().when(trainerService).saveWorkload(workloadRequest);

        ResponseEntity<String> response = trainerController.handleWorkload(workloadRequest);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("Workload processed successfully.", response.getBody());
        verify(trainerService, times(1)).saveWorkload(workloadRequest);
    }

    @Test
    void getMonthlySummary_ShouldReturnTrainerMonthlySummaryResponse() {
        TrainerMonthlySummaryResponse mockResponse = createMockMonthlySummaryResponse();
        when(trainerService.calculateMonthlySummary(USERNAME)).thenReturn(mockResponse);

        ResponseEntity<TrainerMonthlySummaryResponse> response = trainerController.getMonthlySummary(USERNAME);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(mockResponse, response.getBody());
        verify(trainerService, times(1)).calculateMonthlySummary(USERNAME);
    }

    private TrainerMonthlySummaryResponse createMockMonthlySummaryResponse() {
        TrainerMonthlySummaryResponse response = TrainerMonthlySummaryResponse.builder()
                .username(USERNAME)
                .firstName("John")
                .lastName("Doe")
                .Status(TrainerMonthlySummaryResponse.Status.ACTIVE)
                .build();

        List<TrainerMonthlySummaryResponse.YearlySummary> yearlySummaries = new ArrayList<>();
        TrainerMonthlySummaryResponse.YearlySummary yearlySummary = new TrainerMonthlySummaryResponse.YearlySummary();
        yearlySummary.setYear(2024);

        List<TrainerMonthlySummaryResponse.MonthlySummary> monthlySummaries = new ArrayList<>();
        monthlySummaries.add(new TrainerMonthlySummaryResponse.MonthlySummary(Month.NOVEMBER, 180.00));
        yearlySummary.setMonthlySummaries(monthlySummaries);

        yearlySummaries.add(yearlySummary);
        response.setYearlySummaries(yearlySummaries);

        return response;
    }
}