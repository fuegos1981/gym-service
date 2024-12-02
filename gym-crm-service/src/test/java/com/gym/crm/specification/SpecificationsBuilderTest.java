package com.gym.crm.specification;

import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SpecificationsBuilderTest {

    @Test
    void checkIfBuildWithoutParams() {
        List<SearchCriteria> params = new ArrayList<>();
        SpecificationsBuilder builder = new SpecificationsBuilder(params);

        Specification<?> specification = builder.build();

        assertNull(specification);
    }

    @Test
    void checkIfBuildWithSingleParam() {
        SearchCriteria criteria = new SearchCriteria("key", SearchOperation.EQUALITY, "value");
        List<SearchCriteria> params = List.of(criteria);
        SpecificationsBuilder builder = new SpecificationsBuilder(params);

        SearchSpecification<?> mockSpec = mock(SearchSpecification.class);
        when(mockSpec.toPredicate(any(), any(), any())).thenReturn(null);

        Specification<Object> specification = builder.build();

        assertNotNull(specification);
        verify(mockSpec, times(0)).toPredicate(any(), any(), any());
    }

    @Test
    void build_WithMultipleParams_ShouldReturnChainedSpecifications() {
        SearchCriteria criteria1 = new SearchCriteria("key1", SearchOperation.EQUALITY, "value1");
        SearchCriteria criteria2 = new SearchCriteria("key2", SearchOperation.EQUALITY, "value2");
        List<SearchCriteria> params = List.of(criteria1, criteria2);
        SpecificationsBuilder builder = new SpecificationsBuilder(params);

        SearchSpecification<?> mockSpec1 = mock(SearchSpecification.class);
        SearchSpecification<?> mockSpec2 = mock(SearchSpecification.class);
        when(mockSpec1.toPredicate(any(), any(), any())).thenReturn(null);
        when(mockSpec2.toPredicate(any(), any(), any())).thenReturn(null);

        Specification<Object> specification = builder.build();

        assertNotNull(specification);
    }

}