package com.gym.crm.service.impl;

import com.gym.crm.controller.TrainingHoursTrackerClient;
import com.gym.analytics.dto.TrainerWorkloadRequest;
import com.gym.crm.mapper.TrainingMapper;
import com.gym.crm.model.Training;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class AnalyticsSender {

    private final TrainingMapper mapper;
    private final TrainingHoursTrackerClient trackerClient;

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
