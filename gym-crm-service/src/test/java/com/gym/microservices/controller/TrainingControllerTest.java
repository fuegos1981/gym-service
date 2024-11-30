package com.gym.microservices.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gym.microservices.dto.AddTrainingsRequest;
import com.gym.microservices.facade.ServiceFacade;
import com.gym.microservices.model.Training;
import com.gym.microservices.security.JwtProvider;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TrainingController.class)
@Import(TestSecurityConfig.class)
class TrainingControllerTest {

    private static final String API_VERSION = "/api/v1/gym-crm-service";

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ServiceFacade service;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private MeterRegistry meterRegistry;
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

    @BeforeEach
    void setUp() {
        when(meterRegistry.counter(eq("server_errors_total"), eq("type"), eq("5xx"))).thenReturn(serverErrorCounter);
        when(meterRegistry.counter(eq("incorrect_login_total"), eq("type"), eq("detail"))).thenReturn(incorrectLoginCounter);
    }

    @Test
    void checkIfCreateTrainingIsCalled() throws Exception {
        AddTrainingsRequest request = new AddTrainingsRequest()
                .traineeUsername("Ivan.Ivanov")
                .trainingDate(LocalDate.of(2024, 10, 10))
                .trainerUsername("Oleg.Burov")
                .trainingName("Box in our life")
                .trainingDuration(2.0);

        when(service.createTraining(any(AddTrainingsRequest.class))).thenReturn(new Training());

        mockMvc.perform(MockMvcRequestBuilders.post(API_VERSION + "/training/create")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        verify(service).createTraining(any(AddTrainingsRequest.class));
    }
}