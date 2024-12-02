package com.gym.crm.mapper;

import com.gym.crm.dto.TraineeTrainingsResponseInner;
import com.gym.crm.dto.TrainerTrainingsResponseInner;
import com.gym.analytics.dto.TrainerWorkloadRequest;
import com.gym.crm.dto.TrainingType;
import com.gym.crm.model.Training;
import com.gym.crm.model.User;
import org.springframework.stereotype.Component;

@Component
public class TrainingMapper {

    public TraineeTrainingsResponseInner toTraineeTrainingsResponseInner(Training training) {
        return new TraineeTrainingsResponseInner()
                .trainingType(TrainingType.fromValue(training.getTrainingType().getName()))
                .trainerName(training.getTrainer().getUniqueName())
                .trainingDate(training.getTrainingDate())
                .trainingName(training.getName())
                .trainingDuration(training.getDuration());
    }

    public TrainerTrainingsResponseInner toTrainerTrainingsResponseInner(Training training) {
        return new TrainerTrainingsResponseInner()
                .trainingType(TrainingType.fromValue(training.getTrainingType().getName()))
                .traineeName(training.getTrainee().getUniqueName())
                .trainingDate(training.getTrainingDate())
                .trainingName(training.getName())
                .trainingDuration(training.getDuration());
    }

    public TrainerWorkloadRequest toTrainerWorkloadRequest(Training training, String action) {
        User user = training.getTrainer().getUser();

        return TrainerWorkloadRequest.builder()
                .username(user.getUsername())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .isActive(user.getIsActive())
                .trainingDate(training.getTrainingDate())
                .trainingDuration(training.getDuration())
                .actionType(action)
                .build();
    }
}
