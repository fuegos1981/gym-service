package com.gym.analytics.repository;

import com.gym.analytics.model.Trainer;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface TrainerReportRepository extends MongoRepository<Trainer, String> {

    Optional<Trainer> findByUsername(String username);
}
