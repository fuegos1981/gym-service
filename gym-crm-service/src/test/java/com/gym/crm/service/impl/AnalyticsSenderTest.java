package com.gym.crm.service.impl;

import com.gym.analytics.dto.TrainerWorkloadRequest;
import com.gym.crm.constants.GlobalConstants;
import com.gym.crm.mapper.TrainingMapper;
import com.gym.crm.model.Trainee;
import com.gym.crm.model.Trainer;
import com.gym.crm.model.Training;
import com.gym.crm.model.TrainingType;
import com.gym.crm.model.User;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.MDC;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessagePostProcessor;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AnalyticsSenderTest {

    private static final String DESTINATION = "training-hours-queue-test";
    private static final User TRAINEE_USER = buildUser("Bogdan", "Petrov", "3333333333");
    private static final User TRAINER_USER = buildUser("Oleg", "Bodov", "4444444444");
    private static final Trainee TRAINEE = buildTrainee();
    private static final Trainer TRAINER = buildTrainer();
    private static final Training TRAINING = buildTraining();
    private static final TrainerWorkloadRequest TRAINER_WORKLOAD = buildTrainerWorkloadRequest();

    @Mock
    private TrainingMapper mapper;

    @Mock
    private JmsTemplate jmsTemplate;

    @InjectMocks
    private AnalyticsSender analyticsSender;

    @Captor
    private ArgumentCaptor<MessagePostProcessor> messagePostProcessorCaptor;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(analyticsSender, "destination", DESTINATION);
    }

    @Test
    void checkIfProcessWorkloadForSingleTrainingIsCorrect() throws JMSException {
        String action = "ADD";
        String transactionId = "12345";

        MockedStatic<MDC> mdcMock = mockStatic(MDC.class);
        mdcMock.when(() -> MDC.get(GlobalConstants.TRANSACTION_ID)).thenReturn(transactionId);

        when(mapper.toTrainerWorkloadRequest(TRAINING, action)).thenReturn(TRAINER_WORKLOAD);

        String result = analyticsSender.processWorkload(TRAINING, action);

        assertEquals("Message sent to queue successfully.", result);
        verify(jmsTemplate).convertAndSend(eq(DESTINATION), eq(TRAINER_WORKLOAD), messagePostProcessorCaptor.capture());

        MessagePostProcessor postProcessor = messagePostProcessorCaptor.getValue();
        Message mockMessage = mock(Message.class);
        postProcessor.postProcessMessage(mockMessage);

        verify(mockMessage).setStringProperty("transactionId", transactionId);
        verifyNoMoreInteractions(jmsTemplate);

    }

    @Test
    void checkIfProcessWorkloadForListTrainingIsCorrect() {
        List<Training> trainings = List.of(TRAINING);
        String action = "ADD";

        when(mapper.toTrainerWorkloadRequest(TRAINING, action)).thenReturn(TRAINER_WORKLOAD);

        String result = analyticsSender.processWorkload(trainings, action);

        assertEquals("Message sent to queue successfully.", result);
        verify(mapper).toTrainerWorkloadRequest(TRAINING, action);
        verify(jmsTemplate).convertAndSend(eq(DESTINATION), eq(TRAINER_WORKLOAD), messagePostProcessorCaptor.capture());
    }

    private static TrainerWorkloadRequest buildTrainerWorkloadRequest() {
        return new TrainerWorkloadRequest()
                .firstName("Oleg")
                .lastName("Bodov")
                .username("Oleg.Bodov")
                .trainingDate(LocalDate.of(2024, 10, 10))
                .trainingDuration(2.0)
                .actionType(TrainerWorkloadRequest.ActionTypeEnum.ADD)
                .isActive(true);
    }

    private static User buildUser(String firstName, String lastname, String password) {
        return User.builder()
                .firstName(firstName)
                .lastName(lastname)
                .username(firstName + "." + lastname)
                .password(password)
                .isActive(true).build();
    }

    private static Training buildTraining() {
        return Training.builder()
                .trainee(TRAINEE)
                .trainer(TRAINER)
                .trainingType(new TrainingType(1L, "Box"))
                .trainingDate(LocalDate.of(2024, 10, 10))
                .duration(2.0)
                .build();
    }

    private static Trainee buildTrainee() {
        return Trainee.builder()
                .dateOfBirth(LocalDate.of(1980, 11, 10))
                .address("Merefa")
                .user(TRAINEE_USER)
                .build();
    }

    private static Trainer buildTrainer() {
        return Trainer.builder().user(TRAINER_USER).specialization(new TrainingType(1L, "Box")).build();
    }
}