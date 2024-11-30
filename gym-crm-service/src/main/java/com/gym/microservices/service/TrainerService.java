package com.gym.microservices.service;

import com.gym.microservices.dto.TrainerProfile;
import com.gym.microservices.dto.UpdateTrainerRequest;
import com.gym.microservices.dto.UserDetailsResponse;
import com.gym.microservices.model.Trainer;
import com.gym.microservices.model.Training;
import com.gym.microservices.model.TrainingType;
import com.gym.microservices.specification.SearchCriteria;

import java.util.List;

public interface TrainerService {

    UserDetailsResponse create(TrainerProfile request);

    Trainer update(UpdateTrainerRequest request);

    Trainer findByUsername(String username);

    boolean matchUsernameAndPassword(String username, String password);

    boolean changeIsActive(String username, boolean isActive);

    boolean changePassword(String username, String password);

    List<Training> getTrainingsByCriteria(List<SearchCriteria> critters);

    List<TrainingType> getTrainingTypes();
}
