package com.gym.analytics.mapper;

import com.gym.analytics.model.TrainerWorkload;
import com.gym.analytics.dto.TrainerWorkloadRequest;
import org.springframework.stereotype.Component;

@Component
public class TrainerWorkloadMapper {

    private final static String ADD_ACTION = "ADD";

    public TrainerWorkload toGetTrainerWorkload(TrainerWorkloadRequest request) {
        return TrainerWorkload.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .trainingDate(request.getTrainingDate())
                .username(request.getUsername())
                .isActive(request.getIsActive())
                .trainingDuration((request.getActionType().equalsIgnoreCase(ADD_ACTION) ? 1 : -1) * request.getTrainingDuration())
                .build();
    }
}
