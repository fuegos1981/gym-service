package com.gym.analytics.service;

import com.gym.analytics.dto.TrainerMonthlySummaryResponse;
import com.gym.analytics.dto.TrainerWorkloadRequest;
import com.gym.analytics.model.Trainer;

public interface TrainerService {

    Trainer saveWorkload(TrainerWorkloadRequest workload);

    TrainerMonthlySummaryResponse getTrainer(String username);
}
