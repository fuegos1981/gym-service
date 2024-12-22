package com.gym.analytics.service.impl;

import com.gym.analytics.dto.TrainerMonthlySummaryResponse;
import com.gym.analytics.dto.TrainerWorkloadRequest;
import com.gym.analytics.exception.EntityNotFoundException;
import com.gym.analytics.mapper.TrainerMapper;
import com.gym.analytics.model.Trainer;
import com.gym.analytics.repository.TrainerReportRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TrainerServiceImplTest {

    @InjectMocks
    private TrainerServiceImpl trainerService;

    @Mock
    private TrainerReportRepository repository;

    @Mock
    private TrainerMapper mapper;

    @Mock
    private TrainingSummaryManager manager;

    @Test
    void checkIfSaveWorkloadWithCorrectData() {
        TrainerWorkloadRequest request = new TrainerWorkloadRequest(
                "test.user", "Test", "User", true,
                LocalDate.of(2024, 12, 21), 2.0,
                TrainerWorkloadRequest.ActionTypeEnum.ADD
        );

        Trainer existingTrainer = Trainer.builder()
                .username("test.user")
                .firstName("Existing")
                .lastName("Trainer")
                .status(false)
                .build();

        when(repository.findByUsername("test.user")).thenReturn(Optional.of(existingTrainer));
        when(repository.save(any(Trainer.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Trainer result = trainerService.saveWorkload(request);

        assertNotNull(result);
        assertEquals("test.user", result.getUsername());
        assertEquals("Test", result.getFirstName());
        assertEquals("User", result.getLastName());
        assertTrue(result.getStatus());

        verify(manager).addDurationToYearlySummary(eq(existingTrainer), eq(request.getTrainingDate()), eq(2.0));
        verify(repository).save(existingTrainer);
    }

    @Test
    void checkIfSaveWorkloadWithNotExistedTrainer() {
        TrainerWorkloadRequest request = new TrainerWorkloadRequest(
                "new.user", "New", "User", true,
                LocalDate.of(2024, 12, 21), 1.5,
                TrainerWorkloadRequest.ActionTypeEnum.ADD
        );

        when(repository.findByUsername("new.user")).thenReturn(Optional.empty());
        when(repository.save(any(Trainer.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Trainer result = trainerService.saveWorkload(request);

        assertNotNull(result);
        assertEquals("new.user", result.getUsername());
        assertEquals("New", result.getFirstName());
        assertEquals("User", result.getLastName());
        assertTrue(result.getStatus());

        verify(manager).addDurationToYearlySummary(any(Trainer.class), eq(request.getTrainingDate()), eq(1.5));
        verify(repository).save(any(Trainer.class));
    }

    @Test
    void checkIfSaveWorkloadWithActionTypeIsDelete() {
        TrainerWorkloadRequest request = new TrainerWorkloadRequest(
                "test.user", "Test", "User", true,
                LocalDate.of(2024, 12, 21), 3.0,
                TrainerWorkloadRequest.ActionTypeEnum.DELETE
        );

        Trainer existingTrainer = Trainer.builder()
                .username("test.user")
                .firstName("Existing")
                .lastName("Trainer")
                .status(true)
                .build();

        when(repository.findByUsername("test.user")).thenReturn(Optional.of(existingTrainer));
        when(repository.save(any(Trainer.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Trainer result = trainerService.saveWorkload(request);

        assertNotNull(result);

        verify(manager).addDurationToYearlySummary(eq(existingTrainer), eq(request.getTrainingDate()), eq(-3.0));
        verify(repository).save(existingTrainer);
    }

    @Test
    void checkIfGetTrainerIsWorked() {
        Trainer trainer = Trainer.builder()
                .username("test.user")
                .firstName("Test")
                .lastName("User")
                .status(true)
                .build();

        TrainerMonthlySummaryResponse expectedResponse = new TrainerMonthlySummaryResponse()
                .username("test.user")
                .firstName("Test")
                .lastName("User")
                .status(TrainerMonthlySummaryResponse.StatusEnum.ACTIVE);

        when(repository.findByUsername("test.user")).thenReturn(Optional.of(trainer));
        when(mapper.toGetTrainerMonthlySummaryResponse(trainer)).thenReturn(expectedResponse);

        TrainerMonthlySummaryResponse result = trainerService.getTrainer("test.user");

        assertNotNull(result);
        assertEquals(expectedResponse, result);

        verify(repository).findByUsername("test.user");
        verify(mapper).toGetTrainerMonthlySummaryResponse(trainer);
    }

    @Test
    void checkIfGetTrainerThrowEntityNotFoundExceptionIfTrainerNotFound() {
        when(repository.findByUsername("unknown.user")).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                trainerService.getTrainer("unknown.user"));

        assertEquals("Trainer not found in database", exception.getMessage());

        verify(repository).findByUsername("unknown.user");
        verifyNoInteractions(mapper);
    }
}