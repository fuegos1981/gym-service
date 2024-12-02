package com.gym.crm.facade;

import com.gym.crm.dto.AddTrainingsRequest;
import com.gym.crm.dto.GetTraineeResponse;
import com.gym.crm.dto.GetTrainerResponse;
import com.gym.crm.dto.GetTrainersResponseInner;
import com.gym.crm.dto.TraineeProfile;
import com.gym.crm.dto.TraineeTrainingsResponseInner;
import com.gym.crm.dto.TrainerProfile;
import com.gym.crm.dto.TrainerTrainingsResponseInner;
import com.gym.crm.dto.TrainingFilter;
import com.gym.crm.dto.UpdateTraineeRequest;
import com.gym.crm.dto.UpdateTraineeResponse;
import com.gym.crm.dto.UpdateTraineesTrainersRequest;
import com.gym.crm.dto.UpdateTraineesTrainersRequestTrainersInner;
import com.gym.crm.dto.UpdateTraineesTrainersResponseInner;
import com.gym.crm.dto.UpdateTrainerRequest;
import com.gym.crm.dto.UpdateTrainerResponse;
import com.gym.crm.dto.UserDetailsResponse;
import com.gym.crm.exception.ServiceException;
import com.gym.crm.mapper.TraineeMapper;
import com.gym.crm.mapper.TrainerMapper;
import com.gym.crm.mapper.TrainingFilterMapper;
import com.gym.crm.mapper.TrainingMapper;
import com.gym.crm.model.Trainee;
import com.gym.crm.model.Trainer;
import com.gym.crm.model.Training;
import com.gym.crm.model.TrainingType;
import com.gym.crm.service.TraineeService;
import com.gym.crm.service.TrainerService;
import com.gym.crm.service.TrainingService;
import com.gym.crm.specification.SearchCriteria;
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
