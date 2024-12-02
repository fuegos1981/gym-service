package com.gym.crm.service.impl;

import com.gym.crm.dto.AddTrainingsRequest;
import com.gym.crm.exception.EntityNotFoundException;
import com.gym.crm.model.Trainee;
import com.gym.crm.model.Trainer;
import com.gym.crm.model.Training;
import com.gym.crm.repository.TraineeRepository;
import com.gym.crm.repository.TrainerRepository;
import com.gym.crm.repository.TrainingRepository;
import com.gym.crm.service.TrainingService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class TrainingServiceImpl implements TrainingService {

    private final TraineeRepository traineeRepository;
    private final TrainerRepository trainerRepository;
    private final TrainingRepository repository;
    private final AnalyticsSender sender;

    @Override
    @Transactional
    public Training create(AddTrainingsRequest request) {
        Trainee trainee = traineeRepository.findByUserUsername(request.getTraineeUsername())
                .orElseThrow(() -> new EntityNotFoundException("Trainee in training cannot be null"));

        Trainer trainer = trainerRepository.findByUserUsername(request.getTrainerUsername())
                .orElseThrow(() -> new EntityNotFoundException("Trainer in training cannot be null"));

        Training training = Training.builder()
                .trainee(trainee)
                .trainer(trainer)
                .name(request.getTrainingName())
                .trainingType(trainer.getSpecialization())
                .trainingDate(request.getTrainingDate())
                .duration(request.getTrainingDuration()).build();

        Training savedTraining = repository.save(training);
        sender.processWorkload(savedTraining, "ADD");

        return savedTraining;
    }
}
