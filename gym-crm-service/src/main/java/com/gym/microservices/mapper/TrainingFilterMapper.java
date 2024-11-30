package com.gym.microservices.mapper;

import com.gym.microservices.dto.TrainingFilter;
import com.gym.microservices.specification.SearchCriteria;
import com.gym.microservices.specification.SearchCriteriaKey;
import com.gym.microservices.specification.SearchOperation;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import static com.gym.microservices.specification.SearchCriteriaKey.TRAINEE_NAME;
import static com.gym.microservices.specification.SearchCriteriaKey.TRAINING_DATE;
import static com.gym.microservices.specification.SearchCriteriaKey.TRAINING_TYPE;
import static com.gym.microservices.specification.SearchOperation.EQUALITY;
import static com.gym.microservices.specification.SearchOperation.GREATER_THAN;
import static com.gym.microservices.specification.SearchOperation.LESS_THAN;

@Component
public class TrainingFilterMapper {
    public List<SearchCriteria> getSearchCrittersForTraining(TrainingFilter filter) {
        return Stream.of(buildCriteria(filter.getTraineeName(), TRAINEE_NAME, EQUALITY),
                        buildCriteria(filter.getPeriodFrom(), TRAINING_DATE, GREATER_THAN),
                        buildCriteria(filter.getPeriodTo(), TRAINING_DATE, LESS_THAN),
                        buildCriteria(filter.getTrainingType(), TRAINING_TYPE, EQUALITY))
                .filter(Objects::nonNull).toList();
    }

    private SearchCriteria buildCriteria(Object fieldValue, SearchCriteriaKey criteriaKey, SearchOperation operation) {
        if (fieldValue == null) {
            return null;
        }

        return new SearchCriteria(criteriaKey.getKey(), operation, fieldValue);
    }

}
