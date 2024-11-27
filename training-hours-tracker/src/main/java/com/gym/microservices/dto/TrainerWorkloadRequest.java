package com.gym.microservices.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TrainerWorkloadRequest {
    @NotNull
    private String username;
    @NotNull
    private String firstName;
    @NotNull
    private String lastName;
    @NotNull
    private Boolean isActive;
    @NotNull
    private LocalDate trainingDate;
    @NotNull
    private int trainingDuration;
    @NotNull
    private String actionType;
}
