package com.gym.microservices.controller;

import com.gym.microservices.dto.AddTrainingsRequest;
import com.gym.microservices.dto.ErrorResponse;
import com.gym.microservices.facade.ServiceFacade;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${api.version}/training")
@AllArgsConstructor
public class TrainingController {

    private final ServiceFacade service;

    @Operation(operationId = "addTrainings",
            summary = "Add Training",
            tags = {"Trainings"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Training added successfully"),
                    @ApiResponse(description = "Error response", content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
                    })
            }
    )
    @PostMapping(value = "/create", produces = {"application/json"})
    @ResponseStatus(HttpStatus.CREATED)
    public void createTraining(@RequestBody AddTrainingsRequest request) {
        service.createTraining(request);
    }
}
