package com.gym.microservices.mapper;

import com.gym.microservices.dto.TraineeTrainingsResponseInner;
import com.gym.microservices.dto.TrainerTrainingsResponseInner;
import com.gym.microservices.dto.TrainerWorkloadRequest;
import com.gym.microservices.dto.TrainingType;
import com.gym.microservices.model.Training;
import com.gym.microservices.model.User;
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
