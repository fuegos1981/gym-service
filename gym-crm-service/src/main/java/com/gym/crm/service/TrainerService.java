package com.gym.crm.service;

import com.gym.crm.dto.TrainerProfile;
import com.gym.crm.dto.UpdateTrainerRequest;
import com.gym.crm.dto.UserDetailsResponse;
import com.gym.crm.model.Trainer;
import com.gym.crm.model.Training;
import com.gym.crm.model.TrainingType;
import com.gym.crm.specification.SearchCriteria;

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
