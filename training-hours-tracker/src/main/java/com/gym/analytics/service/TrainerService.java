package com.gym.analytics.service;

import com.gym.analytics.dto.TrainerMonthlySummaryResponse;
import com.gym.analytics.dto.TrainerWorkloadRequest;

public interface TrainerService {

    public void saveWorkload(TrainerWorkloadRequest workload);

    public TrainerMonthlySummaryResponse calculateMonthlySummary(String username);
}
