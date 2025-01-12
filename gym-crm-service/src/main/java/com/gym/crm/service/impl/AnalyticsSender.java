package com.gym.crm.service.impl;

import com.gym.analytics.dto.TrainerWorkloadRequest;
import com.gym.crm.constants.GlobalConstants;
import com.gym.crm.exception.TrainingHoursTrackerException;
import com.gym.crm.mapper.TrainingMapper;
import com.gym.crm.model.Training;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AnalyticsSender {

    private final TrainingMapper mapper;
    private final JmsTemplate jmsTemplate;

    public AnalyticsSender(TrainingMapper mapper, JmsTemplate jmsTemplate) {
        this.mapper = mapper;
        this.jmsTemplate = jmsTemplate;
    }

    @Value("${jsm.queue.destination}")
    private String destination;

    public String processWorkload(Training training, String action) {
        String transactionId = MDC.get(GlobalConstants.TRANSACTION_ID);

        try {
            TrainerWorkloadRequest request = mapper.toTrainerWorkloadRequest(training, action);

            jmsTemplate.convertAndSend(destination, request, message -> {
                message.setStringProperty("transactionId", transactionId);
                return message;
            });
            log.info("Transaction ID: {}. Workload for Training: {}, Action: {} sent to queue.",
                    transactionId, training.getName(), action);

            return "Message sent to queue successfully.";
        } catch (Exception e) {
            return fallbackProcessWorkload(transactionId, training, action, e);
        }
    }

    public String processWorkload(List<Training> trainings, String action) {
        return trainings.stream()
                .map(training -> processWorkload(training, action))
                .collect(Collectors.joining("\n"));
    }

    public String fallbackProcessWorkload(String transactionId, Training training, String action, Throwable throwable) {
        log.error("Transaction ID: {}. Fallback executed for Training: {}, Action: {}, Error: {}",
                transactionId, training.getName(), action, throwable.getMessage());
        throw new TrainingHoursTrackerException("Can't send statistics to Training-Hours-Tracker", throwable);
    }
}
