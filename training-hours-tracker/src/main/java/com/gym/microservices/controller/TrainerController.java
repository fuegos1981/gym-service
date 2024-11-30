package com.gym.microservices.controller;

import com.gym.microservices.dto.TrainerMonthlySummaryResponse;
import com.gym.microservices.dto.TrainerWorkloadRequest;
import com.gym.microservices.service.TrainerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${api.version}/trainers")
public class TrainerController {

    private final TrainerService service;

    public TrainerController(TrainerService service) {
        this.service = service;
    }

    @PostMapping("/workload")
    public ResponseEntity<String> handleWorkload(@RequestBody TrainerWorkloadRequest workload) {
        service.saveWorkload(workload);
        return ResponseEntity.ok("Workload processed successfully.");
    }

    @GetMapping("/{username}/summary")
    public ResponseEntity<TrainerMonthlySummaryResponse> getMonthlySummary(@PathVariable String username) {
        return ResponseEntity.ok(service.calculateMonthlySummary(username));
    }

}
