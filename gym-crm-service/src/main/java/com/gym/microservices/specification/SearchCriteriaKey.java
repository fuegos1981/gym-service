package com.gym.microservices.specification;

public enum SearchCriteriaKey {

    TRAINEE_NAME("trainee.user.username"),
    TRAINING_DATE("trainingDate"),
    TRAINER_NAME("trainer.user.username"),
    TRAINING_TYPE("trainingType.name");

    private final String key;

    SearchCriteriaKey(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}

