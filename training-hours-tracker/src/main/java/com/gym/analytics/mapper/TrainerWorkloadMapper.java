package com.gym.analytics.mapper;

import com.gym.analytics.dto.TrainerWorkloadRequest;
import com.gym.analytics.model.TrainerWorkload;
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
                .trainingDuration((request.getActionType().toString().equalsIgnoreCase(ADD_ACTION) ? 1.00 : -1.00) * request.getTrainingDuration())
                .build();
    }


}
