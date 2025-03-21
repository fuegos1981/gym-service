package com.gym.analytics.controller;

import com.gym.analytics.dto.TrainerMonthlySummaryResponse;
import com.gym.analytics.dto.TrainerWorkloadRequest;
import com.gym.analytics.service.TrainerService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("${api.version}/trainers")
public class TrainerController {

    private final TrainerService service;

    @PostMapping("/workload")
    public ResponseEntity<String> handleWorkload(@Valid @RequestBody TrainerWorkloadRequest workload) {
        service.saveWorkload(workload);
        return ResponseEntity.ok("Workload processed successfully.");
    }

    @GetMapping("/{username}/summary")
    public ResponseEntity<TrainerMonthlySummaryResponse> getMonthlySummary(
            @PathVariable @Pattern(regexp = "^[A-Z][a-z]*\\.[A-Z][a-z]*[0-9]*$") String username) {
        return ResponseEntity.ok(service.getTrainer(username));
    }

}
