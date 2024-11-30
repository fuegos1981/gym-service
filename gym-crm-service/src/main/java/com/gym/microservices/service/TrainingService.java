package com.gym.microservices.service;

import com.gym.microservices.dto.AddTrainingsRequest;
import com.gym.microservices.model.Training;

public interface TrainingService {

    Training create(AddTrainingsRequest request);

}
