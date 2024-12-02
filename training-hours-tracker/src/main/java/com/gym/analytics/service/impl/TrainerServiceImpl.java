package com.gym.analytics.service.impl;

import com.gym.analytics.repository.TrainerWorkloadRepository;
import com.gym.analytics.dto.TrainerMonthlySummaryResponse;
import com.gym.analytics.dto.TrainerWorkloadRequest;
import com.gym.analytics.mapper.TrainerWorkloadMapper;
import com.gym.analytics.model.TrainerWorkload;
import com.gym.analytics.service.TrainerService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TrainerServiceImpl implements TrainerService {

    private final TrainerWorkloadRepository repository;
    private final TrainerWorkloadMapper mapper;

    public TrainerServiceImpl(TrainerWorkloadRepository repository, TrainerWorkloadMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public void saveWorkload(TrainerWorkloadRequest workload) {
        repository.save(mapper.toGetTrainerWorkload(workload));
    }

    public TrainerMonthlySummaryResponse calculateMonthlySummary(String username) {
        List<TrainerWorkload> workloads = repository.findByUsernameOrderByTrainingDateAsc(username);

        if (workloads.size() == 0) {
            return null;
        }

        TrainerWorkload lastWorkload = workloads.get(workloads.size() - 1);
        TrainerMonthlySummaryResponse response = TrainerMonthlySummaryResponse.builder()
                .username(lastWorkload.getUsername())
                .firstName(lastWorkload.getFirstName())
                .lastName(lastWorkload.getLastName())
                .Status(lastWorkload.getIsActive() ? TrainerMonthlySummaryResponse.Status.ACTIVE : TrainerMonthlySummaryResponse.Status.NOT_ACTIVE)
                .build();

        workloads.forEach(w -> response.addDurationToYearlySummary(w.getTrainingDate(), w.getTrainingDuration()));

        return response;
    }
}
