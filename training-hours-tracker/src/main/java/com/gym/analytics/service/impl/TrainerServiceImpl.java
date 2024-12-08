package com.gym.analytics.service.impl;

import com.gym.analytics.dto.TrainerMonthlySummaryResponse;
import com.gym.analytics.dto.TrainerWorkloadRequest;
import com.gym.analytics.mapper.TrainerWorkloadMapper;
import com.gym.analytics.model.TrainerWorkload;
import com.gym.analytics.repository.TrainerWorkloadRepository;
import com.gym.analytics.service.TrainerService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class TrainerServiceImpl implements TrainerService {

    private final TrainerWorkloadRepository repository;
    private final TrainerWorkloadMapper mapper;

    public void saveWorkload(TrainerWorkloadRequest workload) {
        repository.save(mapper.toGetTrainerWorkload(workload));
    }

    public TrainerMonthlySummaryResponse calculateMonthlySummary(String username) {
        List<TrainerWorkload> workloads = repository.findByUsernameOrderByTrainingDateAsc(username);
        TrainingSummaryManager manager = new TrainingSummaryManager();
        if (workloads.size() == 0) {
            return null;
        }

        TrainerWorkload lastWorkload = workloads.get(workloads.size() - 1);
        TrainerMonthlySummaryResponse response = new TrainerMonthlySummaryResponse(
                lastWorkload.getUsername(),
                lastWorkload.getFirstName(),
                lastWorkload.getLastName(),
                lastWorkload.getIsActive() ? TrainerMonthlySummaryResponse.StatusEnum.ACTIVE : TrainerMonthlySummaryResponse.StatusEnum.NOT_ACTIVE,
        null);

       workloads.forEach(w -> manager.addDurationToYearlySummary(response, w.getTrainingDate(), w.getTrainingDuration()));

        return response;
    }




}
