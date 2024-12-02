package com.gym.crm.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gym.crm.GymApplication;
import com.gym.crm.dto.GetTraineeResponse;
import com.gym.crm.dto.GetTrainersResponseInner;
import com.gym.crm.dto.TraineeProfile;
import com.gym.crm.dto.TraineeTrainingsResponseInner;
import com.gym.crm.dto.TrainingFilter;
import com.gym.crm.dto.TrainingType;
import com.gym.crm.dto.UpdateTraineeRequest;
import com.gym.crm.dto.UpdateTraineeResponse;
import com.gym.crm.dto.UpdateTraineesTrainersRequest;
import com.gym.crm.dto.UpdateTraineesTrainersRequestTrainersInner;
import com.gym.crm.dto.UpdateTraineesTrainersResponseInner;
import com.gym.crm.dto.UserDetailsResponse;
import com.gym.crm.facade.ServiceFacade;
import com.gym.crm.security.JwtProvider;
import com.gym.crm.security.LogoutProvider;
import com.gym.crm.security.TokenAuthenticator;
import com.gym.crm.service.impl.CustomUserDetailsService;
import com.gym.crm.service.impl.TokenBlacklistService;
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

import java.time.LocalDate;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TraineeController.class)
@ContextConfiguration(classes = GymApplication.class)
@Import(TestSecurityConfig.class)
class TraineeControllerTest {

    private static final String API_VERSION = "/api/v1/gym-crm-service";

    @Autowired
    private MockMvc mockMvc;
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
    @MockBean
    private ServiceFacade service;
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        when(meterRegistry.counter(eq("server_errors_total"), eq("type"), eq("5xx"))).thenReturn(serverErrorCounter);
        when(meterRegistry.counter(eq("incorrect_login_total"), eq("type"), eq("detail"))).thenReturn(incorrectLoginCounter);
    }

    @Test
    void checkIfRegisterTraineeIsCalled() throws Exception {
        UserDetailsResponse mockResponse = new UserDetailsResponse()
                .password("11111111111")
                .username("Ivan.Borov");

        when(service.createTrainee(any(TraineeProfile.class))).thenReturn(mockResponse);

        TraineeProfile request = new TraineeProfile()
                .firstName("Ivan")
                .lastName("Borov")
                .address("Merefa")
                .dateOfBirth(LocalDate.of(2000, 11, 1));

        mockMvc.perform(MockMvcRequestBuilders.post(API_VERSION + "/trainee/register")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.password").value("11111111111"))
                .andExpect(jsonPath("$.username").value("Ivan.Borov"));
    }

    @Test
    void checkIfChangeLoginIsCalled() throws Exception {
        MockHttpSession session = mock(MockHttpSession.class);
        when(service.changePasswordTrainee(eq("testUser"), eq("2222222222"))).thenReturn(true);

        mockMvc.perform(MockMvcRequestBuilders.put(API_VERSION + "/trainee/change-login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\": \"testUser\", \"newPassword\": \"2222222222\"}")
                        .session(session))
                .andExpect(status().isOk());

        verify(service).changePasswordTrainee(eq("testUser"), eq("2222222222"));
    }

    @Test
    void checkIfLoginIsCalled() throws Exception {
        MockHttpSession session = mock(MockHttpSession.class);

        mockMvc.perform(get(API_VERSION + "/trainee/login")
                        .param("username", "testUser")
                        .param("password", "2222222222"))
                .andExpect(status().isOk());
    }

    @Test
    void checkIfGetTraineeProfileIsCalled() throws Exception {
        GetTraineeResponse mockResponse = new GetTraineeResponse()
                .firstName("Ivan")
                .lastName("Ivanov")
                .isActive(true)
                .trainers(Collections.emptyList());

        when(service.findTraineeByUsername(anyString())).thenReturn(mockResponse);

        mockMvc.perform(MockMvcRequestBuilders.get(API_VERSION + "/trainee/Ivan.Ivanov")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lastName").value("Ivanov"))
                .andExpect(jsonPath("$.firstName").value("Ivan"));
    }

    @Test
    void checkIfUpdateTraineeProfileIsCalled() throws Exception {
        UpdateTraineeResponse mockResponse = new UpdateTraineeResponse()
                .username("Ivan.Ivanov")
                .firstName("Ivan")
                .lastName("Ivanov")
                .address("Merefa")
                .dateOfBirth(LocalDate.of(2000, 10, 11))
                .isActive(true)
                .trainers(Collections.emptyList());

        when(service.updateTrainee(any(UpdateTraineeRequest.class))).thenReturn(mockResponse);

        UpdateTraineeRequest request = new UpdateTraineeRequest()
                .firstName("Ivan")
                .username("Ivan.Ivanov")
                .address("Merefa")
                .isActive(true)
                .dateOfBirth(LocalDate.of(2000, 10, 11))
                .lastName("Ivanov");

        mockMvc.perform(MockMvcRequestBuilders.put(API_VERSION + "/trainee/update")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("Ivan.Ivanov"));
    }

    @Test
    void checkIfDeleteTraineeIsCalled() throws Exception {
        when(service.deleteTrainee(eq("Ivan.Ivanov"))).thenReturn(true);

        mockMvc.perform(delete(API_VERSION + "/trainee/delete/Ivan.Ivanov"))
                .andExpect(status().isOk());

        verify(service).deleteTrainee(eq("Ivan.Ivanov"));
    }

    @Test
    void checkIfGetNotAssignedTrainersIsCalled() throws Exception {
        List<GetTrainersResponseInner> mockResponse = new ArrayList<>();
        GetTrainersResponseInner trainers = new GetTrainersResponseInner()
                .firstName("Oleg")
                .lastName("Burov")
                .username("Oleg.Burov")
                .specialization(TrainingType.BOX);
        mockResponse.add(trainers);

        when(service.getTrainersNotAssignedForTrainee(eq("Ivan.Ivanov"))).thenReturn(mockResponse);

        mockMvc.perform(MockMvcRequestBuilders.get(API_VERSION + "/trainee/Ivan.Ivanov/trainers/not-assigned")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].lastName").value("Burov"))
                .andExpect(jsonPath("$[0].firstName").value("Oleg"));
    }

    @Test
    void checkIfUpdateTrainersIsCalled() throws Exception {
        List<UpdateTraineesTrainersResponseInner> mockResponse = new ArrayList<>();
        UpdateTraineesTrainersResponseInner trainer = new UpdateTraineesTrainersResponseInner()
                .firstName("Oleg")
                .lastName("Burov")
                .username("Oleg.Burov")
                .specialization(TrainingType.BOX);
        mockResponse.add(trainer);

        UpdateTraineesTrainersRequestTrainersInner trainerRequest = new UpdateTraineesTrainersRequestTrainersInner()
                .trainerUsername("Oleg.Burov");

        UpdateTraineesTrainersRequest request = new UpdateTraineesTrainersRequest().traineeUsername("Ivan.Ivanov").trainers(List.of(trainerRequest));

        when(service.updateTrainersForTrainee(eq("Ivan.Ivanov"), any(UpdateTraineesTrainersRequest.class))).thenReturn(mockResponse);

        mockMvc.perform(MockMvcRequestBuilders.put(API_VERSION + "/trainee/trainers")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("Oleg.Burov"));
    }

    @Test
    void checkIfGetTrainingsIsCalled() throws Exception {
        List<TraineeTrainingsResponseInner> mockResponse = new ArrayList<>();
        TraineeTrainingsResponseInner training = new TraineeTrainingsResponseInner();
        training.setTrainingName("Mock Training");
        mockResponse.add(training);

        when(service.getTrainingsByCriteriaForTrainee(any(TrainingFilter.class))).thenReturn(mockResponse);

        mockMvc.perform(get(API_VERSION + "/trainee/trainings")
                        .param("username", "testUser")
                        .param("periodFrom", "2024-10-01")
                        .param("periodTo", "2024-10-10")
                        .param("trainerName", "testTrainer"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].trainingName").value("Mock Training"));
    }

    @Test
    void checkIfActivateIsCalled() throws Exception {
        when(service.changeIsActiveForTrainee(eq("testUser"), eq(true))).thenReturn(true);

        mockMvc.perform(patch(API_VERSION + "/trainee/activate")
                        .param("username", "testUser")
                        .param("isActive", "true"))
                .andExpect(status().isOk());
    }
}