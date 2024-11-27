package com.gym.microservices.service;

import com.gym.microservices.dto.TraineeProfile;
import com.gym.microservices.dto.UpdateTraineeRequest;
import com.gym.microservices.dto.UserDetailsResponse;
import com.gym.microservices.model.Trainee;
import com.gym.microservices.model.Trainer;
import com.gym.microservices.model.Training;
import com.gym.microservices.specification.SearchCriteria;

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
