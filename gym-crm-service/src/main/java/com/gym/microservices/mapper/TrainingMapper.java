package com.gym.microservices.mapper;

import com.gym.microservices.dto.TraineeTrainingsResponseInner;
import com.gym.microservices.dto.TrainerTrainingsResponseInner;
import com.gym.microservices.dto.TrainingType;
import com.gym.microservices.model.Training;
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
}
