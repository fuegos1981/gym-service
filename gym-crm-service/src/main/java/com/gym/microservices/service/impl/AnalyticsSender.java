package com.gym.microservices.service.impl;

import com.gym.microservices.controller.TrainingHoursTrackerClient;
import com.gym.microservices.dto.TrainerWorkloadRequest;
import com.gym.microservices.mapper.TrainingMapper;
import com.gym.microservices.model.Training;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AnalyticsSender {

    private final TrainingMapper mapper;
    private final TrainingHoursTrackerClient trackerClient;

    public AnalyticsSender(TrainingMapper mapper, TrainingHoursTrackerClient trackerClient) {
        this.mapper = mapper;
        this.trackerClient = trackerClient;
    }

    public String processWorkload(Training training, String action) {
        TrainerWorkloadRequest request = mapper.toTrainerWorkloadRequest(training, action);
        ResponseEntity<String> response = trackerClient.sendWorkload(request);
        return response.getBody();
    }

    public String processWorkload(List<Training> trainings, String action) {
        trainings.forEach(training -> {
            TrainerWorkloadRequest request = mapper.toTrainerWorkloadRequest(training, action);
            trackerClient.sendWorkload(request);
        });

        return "";
    }
}
