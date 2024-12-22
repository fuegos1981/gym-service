package com.gym.analytics.service.impl;

import com.gym.analytics.dto.TrainerMonthlySummaryResponse;
import com.gym.analytics.dto.TrainerWorkloadRequest;
import com.gym.analytics.exception.EntityNotFoundException;
import com.gym.analytics.mapper.TrainerMapper;
import com.gym.analytics.model.Trainer;
import com.gym.analytics.repository.TrainerReportRepository;
import com.gym.analytics.service.TrainerService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class TrainerServiceImpl implements TrainerService {

    private final static String ADD_ACTION = "ADD";

    private TrainerReportRepository repository;
    private TrainerMapper mapper;
    private TrainingSummaryManager manager;

    public Trainer saveWorkload(TrainerWorkloadRequest request) {
        String username = request.getUsername();
        Double trainingDuration = (request.getActionType().toString().equalsIgnoreCase(ADD_ACTION) ? 1.00 : -1.00) * request.getTrainingDuration();

        Trainer trainer = repository.findByUsername(username).orElse(Trainer.builder().build());

        trainer.setUsername(username);
        trainer.setFirstName(request.getFirstName());
        trainer.setLastName(request.getLastName());
        trainer.setStatus(request.getIsActive());
        manager.addDurationToYearlySummary(trainer, request.getTrainingDate(), trainingDuration);

        return repository.save(trainer);
    }

    public TrainerMonthlySummaryResponse getTrainer(String username) {
        Trainer trainer = repository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Trainer not found in database"));

        return mapper.toGetTrainerMonthlySummaryResponse(trainer);
    }
}
