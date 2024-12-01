package com.gym.microservices.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gym.microservices.GymApplication;
import com.gym.microservices.dto.GetTrainerResponse;
import com.gym.microservices.dto.TrainerProfile;
import com.gym.microservices.dto.TrainerTrainingsResponseInner;
import com.gym.microservices.dto.TrainingFilter;
import com.gym.microservices.dto.TrainingType;
import com.gym.microservices.dto.UpdateTrainerRequest;
import com.gym.microservices.dto.UpdateTrainerResponse;
import com.gym.microservices.dto.UserDetailsResponse;
import com.gym.microservices.facade.ServiceFacade;
import com.gym.microservices.security.JwtProvider;
import com.gym.microservices.security.LogoutProvider;
import com.gym.microservices.security.TokenAuthenticator;
import com.gym.microservices.service.impl.CustomUserDetailsService;
import com.gym.microservices.service.impl.TokenBlacklistService;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TrainerController.class)
@ContextConfiguration(classes = GymApplication.class)
@Import(TestSecurityConfig.class)
class TrainerControllerTest {

    private static final String API_VERSION = "/api/v1/gym-crm-service";

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ServiceFacade service;
    @MockBean
    private MeterRegistry meterRegistry;
    @MockBean
    private TokenAuthenticator tokenAuthenticator;
    @MockBean
    private JwtProvider jwtProvider;
    @MockBean
    private TokenBlacklistService tokenBlacklistService;
    @MockBean
    private CustomUserDetailsService userDetailsService;
    @Mock
    private Counter serverErrorCounter;
    @Mock
    private Counter incorrectLoginCounter;
    @MockBean
    private LogoutProvider logoutProvider;
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        when(meterRegistry.counter(eq("server_errors_total"), eq("type"), eq("5xx"))).thenReturn(serverErrorCounter);
        when(meterRegistry.counter(eq("incorrect_login_total"), eq("type"), eq("detail"))).thenReturn(incorrectLoginCounter);
    }

    @Test
    void checkIfRegisterTrainerIsCalled() throws Exception {
        UserDetailsResponse mockResponse = new UserDetailsResponse()
                .password("11111111111")
                .username("Oleg.Petrov");

        when(service.createTrainer(any(TrainerProfile.class))).thenReturn(mockResponse);

        TrainerProfile request = new TrainerProfile()
                .firstName("Oleg")
                .lastName("Petrov")
                .specialization(TrainingType.BOX);

        mockMvc.perform(MockMvcRequestBuilders.post(API_VERSION + "/trainer/register")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.password").value("11111111111"))
                .andExpect(jsonPath("$.username").value("Oleg.Petrov"));
    }

    @Test
    void checkIfChangeLoginIsCalled() throws Exception {
        MockHttpSession session = mock(MockHttpSession.class);
        when(service.changePasswordTrainer(eq("testUser"), eq("2222222222"))).thenReturn(true);

        mockMvc.perform(MockMvcRequestBuilders.put(API_VERSION + "/trainer/change-login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\": \"testUser\", \"newPassword\": \"2222222222\"}")
                        .session(session))
                .andExpect(status().isOk());

        verify(service).changePasswordTrainer(eq("testUser"), eq("2222222222"));
    }

    @Test
    void checkIfLoginIsCalled() throws Exception {
        MockHttpSession session = mock(MockHttpSession.class);

        mockMvc.perform(get(API_VERSION + "/trainer/login")
                        .param("username", "testUser")
                        .param("password", "2222222222"))
                .andExpect(status().isOk());
    }

    @Test
    void checkIfGetTrainerProfileIsCalled() throws Exception {
        GetTrainerResponse mockResponse = new GetTrainerResponse()
                .firstName("Oleg")
                .lastName("Petrov")
                .isActive(true)
                .specialization(TrainingType.BOX)
                .trainees(Collections.emptyList());

        when(service.findTrainerByUsername(anyString())).thenReturn(mockResponse);

        mockMvc.perform(MockMvcRequestBuilders.get(API_VERSION + "/trainer/Oleg.Petrov")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lastName").value("Petrov"))
                .andExpect(jsonPath("$.firstName").value("Oleg"));
    }

    @Test
    void checkIfUpdateTraineeProfileIsCalled() throws Exception {
        UpdateTrainerResponse mockResponse = new UpdateTrainerResponse()
                .username("Oleg.Petrov")
                .firstName("Oleg")
                .lastName("Petrov")
                .isActive(true)
                .specialization(TrainingType.BOX)
                .trainees(Collections.emptyList());

        when(service.updateTrainer(any(UpdateTrainerRequest.class))).thenReturn(mockResponse);

        UpdateTrainerRequest request = new UpdateTrainerRequest()
                .firstName("Oleg")
                .username("Oleg.Petrov")
                .lastName("Petrov")
                .specialization(TrainingType.BOX);

        mockMvc.perform(MockMvcRequestBuilders.put(API_VERSION + "/trainer/update")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("Oleg.Petrov"));
    }

    @Test
    void checkIfGetTrainingsIsCalled() throws Exception {
        List<TrainerTrainingsResponseInner> mockResponse = new ArrayList<>();
        TrainerTrainingsResponseInner training = new TrainerTrainingsResponseInner();
        training.setTrainingName("Mock Training");
        mockResponse.add(training);

        when(service.getTrainingsByCriteriaForTrainer(any(TrainingFilter.class))).thenReturn(mockResponse);

        mockMvc.perform(get(API_VERSION + "/trainer/trainings")
                        .param("username", "testUser")
                        .param("periodFrom", "2024-10-01")
                        .param("periodTo", "2024-10-10")
                        .param("traineeName", "testTrainee"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].trainingName").value("Mock Training"));
    }

    @Test
    void checkIfActivateIsCalled() throws Exception {
        when(service.changeIsActiveForTrainer(eq("testUser"), eq(true))).thenReturn(true);

        mockMvc.perform(patch(API_VERSION + "/trainer/activate")
                        .param("username", "testUser")
                        .param("isActive", "true"))
                .andExpect(status().isOk());
    }

    @Test
    void checkIfGetTrainingTypesIsCalled() throws Exception {
        List<com.gym.microservices.model.TrainingType> mockResponse = new ArrayList<>();
        mockResponse.add(new com.gym.microservices.model.TrainingType(1L, "Box"));

        when(service.getTrainingTypes()).thenReturn(mockResponse);

        mockMvc.perform(get(API_VERSION + "/trainer/training-types")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Box"));
    }
}