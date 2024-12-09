package com.gym.analytics.service;

import com.gym.analytics.dto.TrainerMonthlySummaryResponse;
import com.gym.analytics.dto.TrainerWorkloadRequest;

public interface TrainerService {

    void saveWorkload(TrainerWorkloadRequest workload);

    TrainerMonthlySummaryResponse calculateMonthlySummary(String username);
}
