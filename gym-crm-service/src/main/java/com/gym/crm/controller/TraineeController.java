package com.gym.crm.controller;

import com.gym.crm.dto.ErrorResponse;
import com.gym.crm.dto.GetTraineeResponse;
import com.gym.crm.dto.GetTrainersResponseInner;
import com.gym.crm.dto.LoginChangeRequest;
import com.gym.crm.dto.TraineeProfile;
import com.gym.crm.dto.TraineeTrainingsResponseInner;
import com.gym.crm.dto.TrainingFilter;
import com.gym.crm.dto.UpdateTraineeRequest;
import com.gym.crm.dto.UpdateTraineeResponse;
import com.gym.crm.dto.UpdateTraineesTrainersRequest;
import com.gym.crm.dto.UpdateTraineesTrainersResponseInner;
import com.gym.crm.dto.UserDetailsResponse;
import com.gym.crm.facade.ServiceFacade;
import com.gym.crm.security.TokenAuthenticator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
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

@Validated
@Tag(name = "Trainees", description = "Trainee API routes")
@RestController
@RequestMapping("${api.version}/trainee")
@AllArgsConstructor
public class TraineeController {

    private final ServiceFacade service;
    private final TokenAuthenticator tokenAuthenticator;

    @Operation(operationId = "registerTrainee",
            summary = "Register a new trainee",
            tags = {"Trainees"},
            responses = {@ApiResponse(
                    responseCode = "200",
                    description = "Registration successful",
                    content = {@Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = UserDetailsResponse.class
                            )
                    )}
            ), @ApiResponse(
                    description = "Error response",
                    content = {@Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = ErrorResponse.class
                            )
                    )}
            )}
    )
    @PostMapping(value = "/register", produces = {"application/json"}, consumes = {"application/json"})
    @ResponseStatus(HttpStatus.CREATED)
    public UserDetailsResponse registerTrainee(@RequestBody TraineeProfile request) {
        return service.createTrainee(request);
    }

    @Operation(operationId = "changeTraineeLogin",
            summary = "Change login credentials",
            tags = {"Trainees"},
            responses = {@ApiResponse(responseCode = "200", description = "Login changed successfully"),
                    @ApiResponse(description = "Error response",
                            content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}
                    )}
    )
    @PutMapping(value = "/change-login", produces = {"application/json"}, consumes = {"application/json"})
    public void changeLogin(@RequestBody LoginChangeRequest request) {
        service.changePasswordTrainee(request.getUsername(), request.getNewPassword());
    }

    @Operation(operationId = "loginTrainee",
            summary = "User login",
            description = "Authenticate the user with username and password",
            tags = {"Trainees"},
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

    @Operation(operationId = "getTrainee",
            summary = "Get Trainee profile",
            tags = {"Trainees"},
            responses = {@ApiResponse(responseCode = "200", description = "Profile retrieved successfully",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = GetTraineeResponse.class))}),
                    @ApiResponse(description = "Error response",
                            content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))})
            }
    )
    @GetMapping(value = "/{username}", produces = {"application/json"})
    public GetTraineeResponse getTraineeProfile(@PathVariable String username) {
        return service.findTraineeByUsername(username);
    }

    @Operation(operationId = "updateTrainee",
            summary = "Update Trainee profile",
            tags = {"Trainees"},
            responses = {@ApiResponse(responseCode = "200",
                    description = "Profile updated successfully",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = UpdateTraineeResponse.class))}),
                    @ApiResponse(description = "Error response",
                            content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))})
            }
    )
    @PutMapping(value = "/update", produces = {"application/json"}, consumes = {"application/json"})
    public UpdateTraineeResponse updateTraineeProfile(@RequestBody UpdateTraineeRequest request) {
        return service.updateTrainee(request);
    }

    @Operation(
            operationId = "deleteTrainee",
            summary = "Delete Trainee",
            tags = {"Trainees"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Trainee deleted successfully"),
                    @ApiResponse(description = "Error response", content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))})
            }
    )
    @DeleteMapping(value = "/delete/{username}", produces = {"application/json"})
    public void deleteTrainee(@PathVariable String username) {
        service.deleteTrainee(username);
    }

    @Operation(
            operationId = "getTrainersNotAssignedTrainee",
            summary = "Get trainers not assigned to trainee",
            tags = {"Trainees"},
            responses = {@ApiResponse(responseCode = "200",
                    description = "Trainers retrieved successfully",
                    content = {@Content(
                            mediaType = "application/json",
                            array = @ArraySchema(
                                    schema = @Schema(implementation = GetTrainersResponseInner.class)
                            )
                    )}
            ), @ApiResponse(
                    description = "Error response",
                    content = {@Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )}
            )}
    )
    @GetMapping(value = "/{username}/trainers/not-assigned", produces = {"application/json"})
    public List<GetTrainersResponseInner> getNotAssignedTrainers(@PathVariable String username) {
        return service.getTrainersNotAssignedForTrainee(username);
    }

    @Operation(
            operationId = "updateTraineesTrainers",
            summary = "Update Trainee's Trainer List",
            tags = {"Trainees"},
            responses = {@ApiResponse(
                    responseCode = "200",
                    description = "Trainers list updated successfully",
                    content = {@Content(
                            mediaType = "application/json",
                            array = @ArraySchema(
                                    schema = @Schema(implementation = UpdateTraineesTrainersResponseInner.class)
                            )
                    )}
            ), @ApiResponse(
                    description = "Error response",
                    content = {@Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )}
            )}
    )
    @PutMapping(value = "/trainers", produces = {"application/json"}, consumes = {"application/json"})
    public List<UpdateTraineesTrainersResponseInner> updateTrainers(@RequestBody UpdateTraineesTrainersRequest request) {
        return service.updateTrainersForTrainee(request.getTraineeUsername(), request);
    }

    @Operation(
            operationId = "getTraineeTrainings",
            summary = "Get Trainee Trainings List",
            tags = {"Trainees"},
            responses = {@ApiResponse(
                    responseCode = "200",
                    description = "Trainings retrieved successfully",
                    content = {@Content(
                            mediaType = "application/json",
                            array = @ArraySchema(
                                    schema = @Schema(implementation = TraineeTrainingsResponseInner.class)
                            )
                    )}
            ), @ApiResponse(
                    description = "Error response",
                    content = {@Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )}
            )}
    )
    @GetMapping(value = "/trainings", produces = {"application/json"})
    public List<TraineeTrainingsResponseInner> getTrainings(@RequestParam String username,
                                                            @RequestParam(required = false) LocalDate periodFrom,
                                                            @RequestParam(required = false) LocalDate periodTo,
                                                            @RequestParam(required = false) String trainerName,
                                                            @RequestParam(required = false) String trainingType) {
        TrainingFilter filter = new TrainingFilter()
                .traineeName(username)
                .periodFrom(periodFrom)
                .periodTo(periodTo)
                .trainerName(trainerName)
                .trainingType(trainingType);

        return service.getTrainingsByCriteriaForTrainee(filter);
    }

    @Operation(operationId = "activateTrainee",
            summary = "Activate or De-Activate Trainee",
            tags = {"Trainees"},
            responses = {@ApiResponse(responseCode = "200", description = "Activation status updated successfully"),
                    @ApiResponse(description = "Error response", content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
                    })
            }
    )
    @PatchMapping(value = "/activate", produces = {"application/json"})
    public void activate(@RequestParam String username,
                         @RequestParam boolean isActive) {
        service.changeIsActiveForTrainee(username, isActive);
    }

    @Operation(operationId = "logoutTrainee",
            summary = "logout Trainee",
            tags = {"Trainees"},
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
