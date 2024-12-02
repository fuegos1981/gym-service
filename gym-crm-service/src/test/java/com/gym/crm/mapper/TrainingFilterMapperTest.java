package com.gym.crm.mapper;

import com.gym.crm.dto.TrainingFilter;
import com.gym.crm.specification.SearchCriteria;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TrainingFilterMapperTest {

    private static final TrainingFilterMapper mapper = new TrainingFilterMapper();

    @Test
    void checkIfGetSearchCrittersForTrainingIsWork() {
        TrainingFilter filter = new TrainingFilter()
                .traineeName("Ivan.Petrov")
                .periodTo(LocalDate.of(2024, 12, 12)).periodFrom(null);

        List<SearchCriteria> actual = mapper.getSearchCrittersForTraining(filter);

        assertEquals(2, actual.size());
        assertEquals("Ivan.Petrov", actual.get(0).getValue());
    }
}