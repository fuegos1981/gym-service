package com.gym.analytics.controller;

import com.gym.analytics.constants.GlobalConstants;
import com.gym.analytics.dto.TrainerWorkloadRequest;
import com.gym.analytics.service.TrainerService;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@Slf4j
public class TrainingHoursTrackerConsumer {

    private final TrainerService service;

    @JmsListener(destination = "training-hours-queue")
    public void receiveWorkload(TrainerWorkloadRequest workload, @Header("transactionId") String transactionId) {
        MDC.put(GlobalConstants.TRANSACTION_ID, transactionId);
        service.saveWorkload(workload);
        log.info("Received message with Transaction ID: {}. Workload: {}", transactionId, workload);
        MDC.clear();
    }

    @JmsListener(destination = "ActiveMQ.DLQ")
    public void handleDeadLetterQueue(Message message) {
        try {
            String transactionId = message.getStringProperty("transactionId");
            log.info("Received DLQ message with Transaction ID: {} and DLQ message: {}", transactionId, message);
        } catch (JMSException e) {
            log.info("Received DLQ message: {}", message);
        }
    }
}
