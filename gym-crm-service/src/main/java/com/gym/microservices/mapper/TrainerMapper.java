package com.gym.microservices.mapper;

import com.gym.microservices.dto.GetTrainerResponse;
import com.gym.microservices.dto.GetTrainersResponseInner;
import com.gym.microservices.dto.TrainingType;
import com.gym.microservices.dto.UpdateTraineesTrainersResponseInner;
import com.gym.microservices.dto.UpdateTrainerResponse;
import com.gym.microservices.dto.UserProfile;
import com.gym.microservices.model.Trainee;
import com.gym.microservices.model.Trainer;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class TrainerMapper {

    public GetTrainersResponseInner toGetTrainersResponseInner(Trainer trainer) {
        return new GetTrainersResponseInner()
                .firstName(trainer.getUser().getFirstName())
                .lastName(trainer.getUser().getLastName())
                .specialization(TrainingType.fromValue(trainer.getSpecialization().getName()))
                .username(trainer.getUser().getUsername());
    }

    public GetTrainerResponse toGetTrainerResponse(Trainer trainer) {
        return new GetTrainerResponse()
                .firstName(trainer.getUser().getFirstName())
                .lastName(trainer.getUser().getLastName())
                .isActive(trainer.getUser().getIsActive())
                .trainees(getUserProfile(trainer))
                .specialization(TrainingType.fromValue(trainer.getSpecialization().getName()));
    }

    public UpdateTrainerResponse toUpdateTrainerResponse(Trainer trainer) {
        return new UpdateTrainerResponse()
                .firstName(trainer.getUser().getFirstName())
                .lastName(trainer.getUser().getLastName())
                .specialization(TrainingType.fromValue(trainer.getSpecialization().getName()))
                .trainees(getUserProfile(trainer))
                .username(trainer.getUser().getUsername())
                .isActive(trainer.getUser().getIsActive());
    }

    public UpdateTraineesTrainersResponseInner toUpdateTraineesTrainersResponseInner(Trainer trainer) {
        return new UpdateTraineesTrainersResponseInner()
                .firstName(trainer.getUser().getFirstName())
                .lastName(trainer.getUser().getLastName())
                .specialization(TrainingType.fromValue(trainer.getSpecialization().getName()))
                .username(trainer.getUser().getUsername());
    }

    private List<UserProfile> getUserProfile(Trainer trainer) {
        return trainer.getTrainees().stream()
                .map(this::toUserProfile)
                .collect(Collectors.toList());
    }

    private UserProfile toUserProfile(Trainee trainee) {
        return new UserProfile()
                .firstName(trainee.getUser().getFirstName())
                .lastName(trainee.getUser().getLastName())
                .username(trainee.getUniqueName());
    }
}
