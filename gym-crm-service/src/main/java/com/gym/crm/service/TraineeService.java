package com.gym.crm.service;

import com.gym.crm.dto.TraineeProfile;
import com.gym.crm.dto.UpdateTraineeRequest;
import com.gym.crm.dto.UserDetailsResponse;
import com.gym.crm.model.Trainee;
import com.gym.crm.model.Trainer;
import com.gym.crm.model.Training;
import com.gym.crm.specification.SearchCriteria;

import java.util.List;
import java.util.Set;

public interface TraineeService {

    UserDetailsResponse create(TraineeProfile request);

    Trainee update(UpdateTraineeRequest request);

    boolean delete(String username);

    Trainee findByUsername(String username);

    boolean matchUsernameAndPassword(String username, String password);

    boolean changeIsActive(String username, boolean isActive);

    boolean changePassword(String username, String password);

    List<Training> getTrainingsByCriteria(List<SearchCriteria> critters);

    List<Trainer> getTrainersNotAssignedForTrainee(String username);

    Trainee updateTrainersForTrainee(String traineeUsername, List<String> trainerUsernames);

    Set<Trainer> getTrainersTrainee(String username);
}
