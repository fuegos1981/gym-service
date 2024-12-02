package com.gym.analytics.mapper;

import com.gym.analytics.dto.TrainerWorkloadRequest;
import com.gym.analytics.model.TrainerWorkload;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TrainerWorkloadMapperTest {

    private TrainerWorkloadMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new TrainerWorkloadMapper();
    }

    @Test
    void toGetTrainerWorkload_ShouldMapCorrectly_WhenActionIsAdd() {
        TrainerWorkloadRequest request = buildTrainerWorkloadRequest("ADD");

        TrainerWorkload workload = mapper.toGetTrainerWorkload(request);

        assertNotNull(workload);
        assertEquals("John", workload.getFirstName());
        assertEquals("Doe", workload.getLastName());
        assertEquals("trainer123", workload.getUsername());
        assertTrue(workload.getIsActive());
        assertEquals(LocalDate.of(2024, 11, 23), workload.getTrainingDate());
        assertEquals(120, workload.getTrainingDuration());
    }

    @Test
    void toGetTrainerWorkload_ShouldMapCorrectly_WhenActionIsDelete() {
        TrainerWorkloadRequest request = buildTrainerWorkloadRequest("DELETE");

        TrainerWorkload workload = mapper.toGetTrainerWorkload(request);

        assertNotNull(workload);
        assertEquals("John", workload.getFirstName());
        assertEquals("Doe", workload.getLastName());
        assertEquals("trainer123", workload.getUsername());
        assertTrue(workload.getIsActive());
        assertEquals(LocalDate.of(2024, 11, 23), workload.getTrainingDate());
        assertEquals(-120, workload.getTrainingDuration());
    }

    @Test
    void toGetTrainerWorkload_ShouldHandleCaseInsensitiveActionType() {
        TrainerWorkloadRequest request = buildTrainerWorkloadRequest("add");

        TrainerWorkload workload = mapper.toGetTrainerWorkload(request);

        assertNotNull(workload);
        assertEquals(120, workload.getTrainingDuration());
    }

    private TrainerWorkloadRequest buildTrainerWorkloadRequest(String actionType) {
        return TrainerWorkloadRequest.builder()
                .firstName("John")
                .lastName("Doe")
                .username("trainer123")
                .isActive(true)
                .trainingDate(LocalDate.of(2024, 11, 23))
                .trainingDuration(120.00)
                .actionType(actionType)
                .build();
    }
}