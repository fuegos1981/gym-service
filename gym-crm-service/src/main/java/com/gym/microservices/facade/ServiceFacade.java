package com.gym.microservices.facade;

import com.gym.microservices.dto.AddTrainingsRequest;
import com.gym.microservices.dto.GetTraineeResponse;
import com.gym.microservices.dto.GetTrainerResponse;
import com.gym.microservices.dto.GetTrainersResponseInner;
import com.gym.microservices.dto.TraineeProfile;
import com.gym.microservices.dto.TraineeTrainingsResponseInner;
import com.gym.microservices.dto.TrainerProfile;
import com.gym.microservices.dto.TrainerTrainingsResponseInner;
import com.gym.microservices.dto.TrainingFilter;
import com.gym.microservices.dto.UpdateTraineeRequest;
import com.gym.microservices.dto.UpdateTraineeResponse;
import com.gym.microservices.dto.UpdateTraineesTrainersRequest;
import com.gym.microservices.dto.UpdateTraineesTrainersRequestTrainersInner;
import com.gym.microservices.dto.UpdateTraineesTrainersResponseInner;
import com.gym.microservices.dto.UpdateTrainerRequest;
import com.gym.microservices.dto.UpdateTrainerResponse;
import com.gym.microservices.dto.UserDetailsResponse;
import com.gym.microservices.exception.ServiceException;
import com.gym.microservices.mapper.TraineeMapper;
import com.gym.microservices.mapper.TrainerMapper;
import com.gym.microservices.mapper.TrainingFilterMapper;
import com.gym.microservices.mapper.TrainingMapper;
import com.gym.microservices.model.Trainee;
import com.gym.microservices.model.Trainer;
import com.gym.microservices.model.Training;
import com.gym.microservices.model.TrainingType;
import com.gym.microservices.service.TraineeService;
import com.gym.microservices.service.TrainerService;
import com.gym.microservices.service.TrainingService;
import com.gym.microservices.specification.SearchCriteria;
import com.gym.microservices.service.TraineeService;
import com.gym.microservices.service.TrainerService;
import com.gym.microservices.service.TrainingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ServiceFacade {

    private final TraineeService traineeService;
    private final TrainerService trainerService;
    private final TrainingService trainingService;
    private final TraineeMapper traineeMapper;
    private final TrainerMapper trainerMapper;
    private final TrainingMapper trainingMapper;
    private final TrainingFilterMapper trainingFilterMapper;

    @Autowired
    public ServiceFacade(TraineeService traineeService, TrainerService trainerService,
                         TrainingService trainingService, TraineeMapper traineeMapper,
                         TrainerMapper trainerMapper, TrainingMapper trainingMapper, TrainingFilterMapper trainingFilterMapper) {
        this.traineeService = traineeService;
        this.trainerService = trainerService;
        this.trainingService = trainingService;
        this.traineeMapper = traineeMapper;
        this.trainerMapper = trainerMapper;
        this.trainingMapper = trainingMapper;
        this.trainingFilterMapper = trainingFilterMapper;
    }

    public UserDetailsResponse createTrainee(TraineeProfile request) {
        return traineeService.create(request);
    }

    public UserDetailsResponse createTrainer(TrainerProfile request) {
        return trainerService.create(request);
    }

    public GetTraineeResponse findTraineeByUsername(String username) {
        Trainee trainee = traineeService.findByUsername(username);
        return traineeMapper.toGetTraineeResponse(trainee);
    }

    public GetTrainerResponse findTrainerByUsername(String username) {
        Trainer trainer = trainerService.findByUsername(username);
        return trainerMapper.toGetTrainerResponse(trainer);
    }

    public boolean changePasswordTrainee(String username, String password) {
        return traineeService.changePassword(username, password);
    }

    public boolean changePasswordTrainer(String username, String password) {
        return trainerService.changePassword(username, password);
    }

    public UpdateTraineeResponse updateTrainee(UpdateTraineeRequest request) {
        Trainee trainee = traineeService.update(request);
        return traineeMapper.toUpdateTraineeResponse(trainee);
    }

    public UpdateTrainerResponse updateTrainer(UpdateTrainerRequest request) {
        Trainer trainer = trainerService.update(request);
        return trainerMapper.toUpdateTrainerResponse(trainer);
    }

    public boolean changeIsActiveForTrainee(String username, boolean isActive) {
        return traineeService.changeIsActive(username, isActive);
    }

    public boolean changeIsActiveForTrainer(String username, boolean isActive) {
        return trainerService.changeIsActive(username, isActive);
    }

    public boolean deleteTrainee(String username) {
        return traineeService.delete(username);
    }

    public List<TraineeTrainingsResponseInner> getTrainingsByCriteriaForTrainee(TrainingFilter filter) {
        if (filter == null || filter.getTraineeName() == null) {
            throw new ServiceException("Trainee username cannot be null");
        }

        List<SearchCriteria> critters = trainingFilterMapper.getSearchCrittersForTraining(filter);

        List<Training> trainings = traineeService.getTrainingsByCriteria(critters);

        return trainings.stream()
                .map(trainingMapper::toTraineeTrainingsResponseInner)
                .collect(Collectors.toList());
    }

    public List<TrainerTrainingsResponseInner> getTrainingsByCriteriaForTrainer(TrainingFilter filter) {
        if (filter == null || filter.getTrainerName() == null) {
            throw new ServiceException("Trainer username cannot be null");
        }

        List<SearchCriteria> critters = trainingFilterMapper.getSearchCrittersForTraining(filter);

        List<Training> trainings = trainerService.getTrainingsByCriteria(critters);

        return trainings.stream()
                .map(trainingMapper::toTrainerTrainingsResponseInner)
                .collect(Collectors.toList());
    }

    public Training createTraining(AddTrainingsRequest request) {
        return trainingService.create(request);
    }

    public List<GetTrainersResponseInner> getTrainersNotAssignedForTrainee(String username) {
        List<Trainer> trainers = traineeService.getTrainersNotAssignedForTrainee(username);

        return trainers.stream()
                .map(trainerMapper::toGetTrainersResponseInner)
                .toList();
    }

    public List<UpdateTraineesTrainersResponseInner> updateTrainersForTrainee(String traineeUsername, UpdateTraineesTrainersRequest request) {
        List<String> trainerUsernames = request.getTrainers().stream()
                .map(UpdateTraineesTrainersRequestTrainersInner::getTrainerUsername)
                .toList();

        Trainee trainee = traineeService.updateTrainersForTrainee(traineeUsername, trainerUsernames);

        return trainee.getTrainers().stream()
                .map(trainerMapper::toUpdateTraineesTrainersResponseInner)
                .toList();
    }

    public Set<Trainer> getTrainersTrainee(String username) {
        return traineeService.getTrainersTrainee(username);
    }

    public List<TrainingType> getTrainingTypes() {
        return trainerService.getTrainingTypes();
    }
}
