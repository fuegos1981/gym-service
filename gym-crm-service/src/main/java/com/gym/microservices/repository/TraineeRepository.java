package com.gym.microservices.repository;

import com.gym.microservices.model.Trainee;
import com.gym.microservices.model.Trainer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TraineeRepository extends JpaRepository<Trainee, Long> {

    Optional<Trainee> findByUserUsername(String username);

    @Query("SELECT trainer FROM Trainer trainer WHERE trainer NOT IN " +
            "(SELECT tr FROM Trainee trainee JOIN trainee.trainers tr WHERE trainee.user.username = :username)")
    List<Trainer> getTrainersNotAssignedForTrainee(String username);
}
