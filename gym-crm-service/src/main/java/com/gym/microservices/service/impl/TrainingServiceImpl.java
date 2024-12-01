package com.gym.microservices.service.impl;

import com.gym.microservices.dto.AddTrainingsRequest;
import com.gym.microservices.exception.EntityNotFoundException;
import com.gym.microservices.model.Trainee;
import com.gym.microservices.model.Trainer;
import com.gym.microservices.model.Training;
import com.gym.microservices.repository.TraineeRepository;
import com.gym.microservices.repository.TrainerRepository;
import com.gym.microservices.repository.TrainingRepository;
import com.gym.microservices.service.TrainingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TrainingServiceImpl implements TrainingService {

    private final TraineeRepository traineeRepository;
    private final TrainerRepository trainerRepository;
    private final TrainingRepository repository;
    private final AnalyticsSender sender;

    @Autowired
    public TrainingServiceImpl(TraineeRepository traineeRepository,
                               TrainerRepository trainerRepository,
                               TrainingRepository trainingRepository, AnalyticsSender sender) {
        this.traineeRepository = traineeRepository;
        this.trainerRepository = trainerRepository;
        this.repository = trainingRepository;
        this.sender = sender;
    }

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

        Training  savedTraining= repository.save(training);
        sender.processWorkload(savedTraining, "ADD");

        return savedTraining;
    }
}
