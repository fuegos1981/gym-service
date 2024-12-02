package com.gym.crm.specification;

import com.gym.crm.model.Training;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Method;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SearchSpecificationTest {
    @Mock
    private Root<Training> root;

    @Mock
    private CriteriaQuery<Training> query;

    @Mock
    private CriteriaBuilder builder;

    @Mock
    private Predicate predicate;

    private SearchSpecification<Training> specification;

    @Mock
    private Path<Object> path;

    @Mock
    Path<LocalDate> pathDate;

    @Test
    public void checkIfEqualityPredicate() {
        SearchCriteria criteria = new SearchCriteria("username", SearchOperation.EQUALITY, "Ivan.Petrov");
        specification = new SearchSpecification<>(criteria);
        when(root.get("username")).thenReturn(path);
        when(builder.equal(path, "Ivan.Petrov")).thenReturn(predicate);

        Predicate result = specification.toPredicate(root, query, builder);

        verify(builder).equal(path, "Ivan.Petrov");
        assertEquals(predicate, result);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void checkIfGreaterThanPredicate() {
        LocalDate periodFrom = LocalDate.of(2014, 10, 10);
        SearchCriteria criteria = new SearchCriteria("periodFrom", SearchOperation.GREATER_THAN, periodFrom);

        specification = new SearchSpecification<>(criteria);

        when(root.get(eq("periodFrom"))).thenReturn((Path) pathDate);
        when(builder.greaterThan(any(pathDate.getClass()), eq(periodFrom))).thenReturn(predicate);

        Predicate result = specification.toPredicate(root, query, builder);

        verify(builder).greaterThan(any(pathDate.getClass()), eq(periodFrom));
        assertEquals(predicate, result);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void checkIfLessThanPredicate() {
        LocalDate periodFrom = LocalDate.of(2014, 10, 10);
        SearchCriteria criteria = new SearchCriteria("periodFrom", SearchOperation.LESS_THAN, periodFrom);

        specification = new SearchSpecification<>(criteria);

        when(root.get("periodFrom")).thenReturn((Path) pathDate);
        when(builder.lessThan(pathDate, periodFrom)).thenReturn(predicate);

        Predicate result = specification.toPredicate(root, query, builder);

        verify(builder).lessThan(pathDate, periodFrom);
        assertEquals(predicate, result);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void checkIfGetExpressionIsWork() throws Exception {
        SearchCriteria criteria = new SearchCriteria("trainee.user.username", SearchOperation.EQUALITY, "Inna.Petrova");
        SearchSpecification<Training> myClass = new SearchSpecification<>(criteria);

        Method method = SearchSpecification.class.getDeclaredMethod("getExpression", Root.class);
        method.setAccessible(true);

        when(root.get(anyString())).thenReturn(path);
        when(path.get(anyString())).thenReturn(path);
        Path<String> result = (Path<String>) method.invoke(myClass, root);

        verify(root).get(eq("trainee"));
        verify(path).get(eq("user"));
        verify(path).get(eq("username"));
        assertNotNull(result);
    }

}