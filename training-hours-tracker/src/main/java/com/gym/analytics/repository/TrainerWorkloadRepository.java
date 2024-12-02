package com.gym.analytics.repository;

import com.gym.analytics.model.TrainerWorkload;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TrainerWorkloadRepository extends JpaRepository<TrainerWorkload, Long> {
    List<TrainerWorkload> findByUsernameOrderByTrainingDateAsc(String username);
}
