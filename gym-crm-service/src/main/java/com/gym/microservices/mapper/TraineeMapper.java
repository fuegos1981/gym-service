package com.gym.microservices.mapper;

import com.gym.microservices.dto.GetTraineeResponse;
import com.gym.microservices.dto.GetTrainersResponseInner;
import com.gym.microservices.dto.TrainingType;
import com.gym.microservices.dto.UpdateTraineeResponse;
import com.gym.microservices.model.Trainee;
import com.gym.microservices.model.Trainer;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class TraineeMapper {

    public UpdateTraineeResponse toUpdateTraineeResponse(Trainee trainee) {
        return new UpdateTraineeResponse()
                .firstName(trainee.getUser().getFirstName())
                .lastName(trainee.getUser().getLastName())
                .dateOfBirth(trainee.getDateOfBirth())
                .address(trainee.getAddress())
                .trainers(getGetTrainersResponseInners(trainee))
                .username(trainee.getUser().getUsername())
                .isActive(trainee.getUser().getIsActive());
    }

    public GetTraineeResponse toGetTraineeResponse(Trainee trainee) {
        return new GetTraineeResponse()
                .firstName(trainee.getUser().getFirstName())
                .lastName(trainee.getUser().getLastName())
                .dateOfBirth(trainee.getDateOfBirth())
                .address(trainee.getAddress())
                .trainers(getGetTrainersResponseInners(trainee))
                .isActive(trainee.getUser().getIsActive());
    }

    private List<GetTrainersResponseInner> getGetTrainersResponseInners(Trainee trainee) {
        return trainee.getTrainers().stream()
                .map(this::toGetTrainersResponseInner)
                .collect(Collectors.toList());
    }

    private GetTrainersResponseInner toGetTrainersResponseInner(Trainer trainer) {
        return new GetTrainersResponseInner()
                .firstName(trainer.getUser().getFirstName())
                .lastName(trainer.getUser().getLastName())
                .specialization(TrainingType.fromValue(trainer.getSpecialization().getName()))
                .username(trainer.getUniqueName());
    }
}
