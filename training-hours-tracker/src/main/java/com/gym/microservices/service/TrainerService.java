package com.gym.microservices.service;

import com.gym.microservices.dto.TrainerMonthlySummaryResponse;
import com.gym.microservices.dto.TrainerWorkloadRequest;

public interface TrainerService {

    public void saveWorkload(TrainerWorkloadRequest workload);

    public TrainerMonthlySummaryResponse calculateMonthlySummary(String username);
}
