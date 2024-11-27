package com.gym.microservices.repository;

import com.gym.microservices.model.TrainerWorkload;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface TrainerWorkloadRepository extends JpaRepository<TrainerWorkload, Long> {
    List<TrainerWorkload> findByUsernameOrderByTrainingDateAsc(String username);
    List<TrainerWorkload> findByUsernameAndTrainingDateAndTrainingDuration(String username, LocalDate trainingDate, int Duration);
}
