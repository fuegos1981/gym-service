package com.gym.analytics.controller;

import com.gym.analytics.dto.TrainerWorkloadRequest;
import com.gym.analytics.service.TrainerService;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TrainingHoursTrackerConsumerTest {

    @Mock
    private TrainerService trainerService;

    @InjectMocks
    private TrainingHoursTrackerConsumer consumer;

    @Test
    void checkIfReceiveWorkloadIsWorked() {
        TrainerWorkloadRequest workloadRequest = new TrainerWorkloadRequest()
                .username("John.Doe")
                .firstName("John")
                .lastName("Doe")
                .isActive(true)
                .trainingDate(LocalDate.of(2024, 11, 23))
                .trainingDuration(120.00)
                .actionType(TrainerWorkloadRequest.ActionTypeEnum.ADD);

        String transactionId = "12345";

        consumer.receiveWorkload(workloadRequest, transactionId);

        verify(trainerService, times(1)).saveWorkload(workloadRequest);
    }

    @Test
    void checkIfHandleDeadLetterQueueIsWorked() throws JMSException {
        Message mockMessage = mock(Message.class);
        String transactionId = "67890";
        when(mockMessage.getStringProperty("transactionId")).thenReturn(transactionId);

        consumer.handleDeadLetterQueue(mockMessage);

        verify(mockMessage, times(1)).getStringProperty("transactionId");
    }

    @Test
    void checkIfHandleDeadLetterQueueWithException() throws JMSException {
        Message mockMessage = mock(Message.class);
        when(mockMessage.getStringProperty("transactionId")).thenThrow(new JMSException("Error"));

        consumer.handleDeadLetterQueue(mockMessage);

        verify(mockMessage, times(1)).getStringProperty("transactionId");
    }
}