package com.gym.microservices.controller;

import com.gym.microservices.dto.TrainerWorkloadRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "training-hours-tracker")
public interface TrainingHoursTrackerClient {
    @PostMapping("/api/v1/training-hours-tracker/trainers/workload")
    ResponseEntity<String> sendWorkload(@RequestBody TrainerWorkloadRequest workload);
}
