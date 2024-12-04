package com.gym.crm.service.impl;

import com.gym.analytics.dto.TrainerWorkloadRequest;
import com.gym.crm.controller.TrainingHoursTrackerClient;
import com.gym.crm.exception.TrainingHoursTrackerException;
import com.gym.crm.mapper.TrainingMapper;
import com.gym.crm.model.Training;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor
public class AnalyticsSender {

    private final TrainingMapper mapper;
    private final TrainingHoursTrackerClient trackerClient;

    @CircuitBreaker(name = "trackerClient", fallbackMethod = "fallbackProcessWorkload")
    public String processWorkload(Training training, String action) {
        TrainerWorkloadRequest request = mapper.toTrainerWorkloadRequest(training, action);
        ResponseEntity<String> response = trackerClient.sendWorkload(request);

        return response.getBody();
    }

    public String processWorkload(List<Training> trainings, String action) {
        return trainings.stream()
                .map(training -> processWorkload(training, action))
                .collect(Collectors.joining("\n"));
    }

    public String fallbackProcessWorkload(Training training, String action, Throwable throwable) {
        log.error("Fallback executed for Training: {}, Action: {}",
                training.getName(), action, throwable.getMessage());
        throw new TrainingHoursTrackerException("Can't send statistics to Training-Hours-Tracker", throwable);
    }
}
