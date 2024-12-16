package com.gym.crm.controller;

import com.gym.crm.dto.ErrorResponse;
import com.gym.crm.dto.GetTrainerResponse;
import com.gym.crm.dto.LoginChangeRequest;
import com.gym.crm.dto.TrainerProfile;
import com.gym.crm.dto.TrainerTrainingsResponseInner;
import com.gym.crm.dto.TrainingFilter;
import com.gym.crm.dto.TrainingTypeResponseInner;
import com.gym.crm.dto.UpdateTrainerRequest;
import com.gym.crm.dto.UpdateTrainerResponse;
import com.gym.crm.dto.UserDetailsResponse;
import com.gym.crm.facade.ServiceFacade;
import com.gym.crm.model.TrainingType;
import com.gym.crm.security.TokenAuthenticator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("${api.version}/trainer")
@AllArgsConstructor
public class TrainerController {

    private final ServiceFacade service;
    private final TokenAuthenticator tokenAuthenticator;

    @Operation(operationId = "registerTrainer",
            summary = "Register a new trainer",
            tags = {"Trainers"},
            responses = {@ApiResponse(responseCode = "200", description = "Registration successful", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = UserDetailsResponse.class))
            }),
                    @ApiResponse(description = "Error response", content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
                    })
            }
    )
    @PostMapping(value = "/register", produces = {"application/json"}, consumes = {"application/json"})
    @ResponseStatus(HttpStatus.CREATED)
    public UserDetailsResponse registerTrainer(@RequestBody TrainerProfile request) {
        return service.createTrainer(request);
    }

    @Operation(operationId = "changeTrainerLogin",
            summary = "Change login credentials",
            tags = {"Trainers"},
            responses = {@ApiResponse(responseCode = "200", description = "Login changed successfully"),
                    @ApiResponse(description = "Error response",
                            content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}
                    )}
    )
    @PutMapping(value = "/change-login", produces = {"application/json"}, consumes = {"application/json"})
    public void changeLogin(@RequestBody LoginChangeRequest request) {
        service.changePasswordTrainer(request.getUsername(), request.getNewPassword());
    }

    @Operation(operationId = "loginTrainer",
            summary = "User login",
            description = "Authenticate the user with username and password",
            tags = {"Trainers"},
            responses = {@ApiResponse(responseCode = "200", description = "Successfully authenticated"),
                    @ApiResponse(description = "Error response",
                            content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}
                    )}
    )
    @GetMapping(value = "/login", produces = {"application/json"})
    public String login(@RequestParam String username,
                        @RequestParam String password) {
        return tokenAuthenticator.authenticate(username, password);
    }

    @Operation(operationId = "getTrainer",
            summary = "Get Trainer profile",
            tags = {"Trainers"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Trainer profile retrieved successfully", content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = GetTrainerResponse.class))
                    }),
                    @ApiResponse(description = "Error response", content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
                    })
            }
    )
    @GetMapping(value = "/{username}", produces = {"application/json"})
    public GetTrainerResponse getTrainerProfile(@PathVariable String username) {
        return service.findTrainerByUsername(username);
    }

    @Operation(operationId = "updateTrainer",
            summary = "Update Trainer",
            tags = {"Trainers"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Profile updated successfully", content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = UpdateTrainerResponse.class))
                    }),
                    @ApiResponse(description = "Error response", content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
                    })
            }
    )

    @PutMapping(value = "/update", produces = {"application/json"}, consumes = {"application/json"})
    public UpdateTrainerResponse updateTraineeProfile(@RequestBody UpdateTrainerRequest request) {
        return service.updateTrainer(request);
    }

    @Operation(operationId = "getTrainerTrainings",
            summary = "Get Trainer Trainings List",
            tags = {"Trainers"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Trainings retrieved successfully", content = {
                            @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = TrainerTrainingsResponseInner.class)))
                    }),
                    @ApiResponse(description = "Error response", content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
                    })
            }
    )
    @GetMapping(value = "/trainings", produces = {"application/json"})
    public List<TrainerTrainingsResponseInner> getTrainings(@RequestParam String username,
                                                            @RequestParam(required = false) LocalDate periodFrom,
                                                            @RequestParam(required = false) LocalDate periodTo,
                                                            @RequestParam(required = false) String traineeName) {
        TrainingFilter filter = new TrainingFilter()
                .trainerName(username)
                .periodFrom(periodFrom)
                .periodTo(periodTo)
                .traineeName(traineeName);

        return service.getTrainingsByCriteriaForTrainer(filter);
    }

    @Operation(operationId = "activateTrainer",
            summary = "Activate or De-Activate Trainer",
            tags = {"Trainers"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Activation status updated successfully"),
                    @ApiResponse(description = "Error response", content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
                    })
            }
    )
    @PatchMapping(value = "/activate", produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    public void activate(@RequestParam String username,
                         @RequestParam boolean isActive) {
        service.changeIsActiveForTrainer(username, isActive);
    }

    @Operation(operationId = "getTrainingTypes",
            summary = "Get Training Types",
            tags = {"Trainers"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Training types retrieved successfully", content = {
                            @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = TrainingTypeResponseInner.class)))
                    }),
                    @ApiResponse(description = "Error response", content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
                    })
            }
    )
    @GetMapping(value = "/training-types", produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    public List<TrainingType> getTrainingTypes() {
        return service.getTrainingTypes();
    }

    @Operation(operationId = "logoutTrainer",
            summary = "logout Trainer",
            tags = {"Trainers"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful logout"),
                    @ApiResponse(description = "Error response", content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
                    })
            }
    )
    @GetMapping(value = "/logout", produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    public void logout(HttpServletRequest request) {
        SecurityContextHolder.clearContext();
    }
}
