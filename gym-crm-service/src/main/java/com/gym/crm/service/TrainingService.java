package com.gym.crm.service;

import com.gym.crm.dto.AddTrainingsRequest;
import com.gym.crm.model.Training;

public interface TrainingService {

    Training create(AddTrainingsRequest request);

}
