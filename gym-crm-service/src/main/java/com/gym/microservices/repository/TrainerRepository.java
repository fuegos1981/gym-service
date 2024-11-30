package com.gym.microservices.repository;

import com.gym.microservices.model.Trainer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TrainerRepository extends JpaRepository<Trainer, Long> {

    List<Trainer> findByUserUsernameIn(List<String> trainerUsernames);

    Optional<Trainer> findByUserUsername(String username);
}
