package com.gym.crm.mapper;

import com.gym.crm.dto.TrainingFilter;
import com.gym.crm.specification.SearchCriteria;
import com.gym.crm.specification.SearchCriteriaKey;
import com.gym.crm.specification.SearchOperation;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import static com.gym.crm.specification.SearchCriteriaKey.TRAINEE_NAME;
import static com.gym.crm.specification.SearchCriteriaKey.TRAINING_DATE;
import static com.gym.crm.specification.SearchCriteriaKey.TRAINING_TYPE;
import static com.gym.crm.specification.SearchOperation.EQUALITY;
import static com.gym.crm.specification.SearchOperation.GREATER_THAN;
import static com.gym.crm.specification.SearchOperation.LESS_THAN;

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
