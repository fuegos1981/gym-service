package com.gym.microservices.facade;

import com.gym.microservices.dto.TrainingFilter;
import com.gym.microservices.dto.UpdateTraineesTrainersRequest;
import com.gym.microservices.mapper.TraineeMapper;
import com.gym.microservices.mapper.TrainerMapper;
import com.gym.microservices.mapper.TrainingFilterMapper;
import com.gym.microservices.mapper.TrainingMapper;
import com.gym.microservices.model.Trainee;
import com.gym.microservices.service.TraineeService;
import com.gym.microservices.service.TrainerService;
import com.gym.microservices.service.TrainingService;
import com.gym.microservices.specification.SearchCriteria;
import com.gym.microservices.specification.SearchOperation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ServiceFacadeTest {
    @Mock
    private TrainingService trainingService;

    @Mock
    private TraineeService traineeService;

    @Mock
    private TrainerService trainerService;

    @Mock
    private TrainingMapper trainingMapper;

    @Mock
    private TraineeMapper traineeMapper;

    @Mock
    private TrainerMapper trainerMapper;

    @Mock
    private TrainingFilterMapper trainingFilterMapper;

    @InjectMocks
    private ServiceFacade serviceFacade;

    @Test
    void checkIfCreateTraineeIsCalled() {
        serviceFacade.createTrainee(null);
        verify(traineeService).create(null);
    }

    @Test
    void checkIfUpdateTraineeIsCalled() {
        serviceFacade.updateTrainee(null);
        verify(traineeService).update(null);
    }

    @Test
    void checkIfDeleteTraineeIsCalled() {
        serviceFacade.deleteTrainee(null);
        verify(traineeService).delete(null);
    }

    @Test
    void checkIfFindTraineeByUsernameIsCalled() {
        serviceFacade.findTraineeByUsername(null);
        verify(traineeService).findByUsername(null);
    }

    @Test
    void checkIfCreateTrainerIsCalled() {
        serviceFacade.createTrainer(null);
        verify(trainerService).create(null);
    }

    @Test
    void checkIfUpdateTrainerIsCalled() {
        serviceFacade.updateTrainer(null);
        verify(trainerService).update(null);
    }

    @Test
    void checkIfFindTrainerByUsernameIsCalled() {
        serviceFacade.findTrainerByUsername(null);
        verify(trainerService).findByUsername(null);
    }

    @Test
    void checkIfCreateTrainingIsCalled() {
        serviceFacade.createTraining(null);
        verify(trainingService).create(null);
    }

    @Test
    void checkIfChangePasswordTraineeIsCalled() {
        serviceFacade.changePasswordTrainee("test", "test");
        verify(traineeService).changePassword("test", "test");
    }

    @Test
    void checkIfChangePasswordTrainerIsCalled() {
        serviceFacade.changePasswordTrainer("test", "test");
        verify(trainerService).changePassword("test", "test");
    }

    @Test
    void checkIfChangeIsActiveTraineeIsCalled() {
        serviceFacade.changeIsActiveForTrainee("test", true);
        verify(traineeService).changeIsActive("test", true);
    }

    @Test
    void checkIfChangeIsActiveTrainerIsCalled() {
        serviceFacade.changeIsActiveForTrainer("test", true);
        verify(trainerService).changeIsActive("test", true);
    }

    @Test
    void checkIfGetTrainingsByUsernameAndCriteriaForTraineeIsCalled() {
        TrainingFilter filter = new TrainingFilter().trainingType("Box").traineeName("Ivan.Ivanov");
        List<SearchCriteria> critters = List.of(new SearchCriteria("trainingType.name", SearchOperation.EQUALITY, "Box"),
                new SearchCriteria("trainee.user.username", SearchOperation.EQUALITY, "Ivan.Ivanov"));

        when(trainingFilterMapper.getSearchCrittersForTraining(filter)).thenReturn(critters);

        serviceFacade.getTrainingsByCriteriaForTrainee(filter);

        verify(traineeService).getTrainingsByCriteria(critters);
    }

    @Test
    void checkIfGetTrainingsByUsernameAndCriteriaForTrainerIsCalled() {
        TrainingFilter filter = new TrainingFilter().trainingType("Box").trainerName("Ivan.Ivanov");
        List<SearchCriteria> critters = List.of(new SearchCriteria("trainingType.name", SearchOperation.EQUALITY, "Box"),
                new SearchCriteria("trainer.user.username", SearchOperation.EQUALITY, "Ivan.Ivanov"));

        when(trainingFilterMapper.getSearchCrittersForTraining(filter)).thenReturn(critters);

        serviceFacade.getTrainingsByCriteriaForTrainer(filter);

        verify(trainerService).getTrainingsByCriteria(critters);
    }

    @Test
    void checkIfGetTrainersNotAssignedForTraineeIsCalled() {
        String username = "Ivan.Ivanov";

        when(traineeService.getTrainersNotAssignedForTrainee(username)).thenReturn(Collections.emptyList());

        serviceFacade.getTrainersNotAssignedForTrainee(username);

        verify(traineeService).getTrainersNotAssignedForTrainee(username);
    }

    @Test
    void checkIfUpdateTrainersForTraineeIsCalled() {
        String username = "Ivan.Ivanov";
        UpdateTraineesTrainersRequest trainers = new UpdateTraineesTrainersRequest();
        List<String> trainerUsernames = new ArrayList<>();
        when(traineeService.updateTrainersForTrainee(eq(username), eq(trainerUsernames))).thenReturn(new Trainee());

        serviceFacade.updateTrainersForTrainee(username, trainers);

        verify(traineeService).updateTrainersForTrainee(username, trainerUsernames);
    }
}