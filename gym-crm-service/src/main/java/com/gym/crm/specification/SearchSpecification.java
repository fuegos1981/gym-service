package com.gym.crm.specification;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.AllArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

@AllArgsConstructor
public class SearchSpecification<Entity> implements Specification<Entity> {

    private SearchCriteria criteria;

    @Override
    public Predicate toPredicate(Root<Entity> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
        return switch (criteria.getOperation()) {
            case EQUALITY -> builder.equal(getExpression(root), criteria.getValue());
            case NEGATION -> builder.notEqual(getExpression(root), criteria.getValue());
            case GREATER_THAN -> {
                if (criteria.getValue() instanceof LocalDate) {
                    yield builder.greaterThan(root.get(criteria.getKey()), (LocalDate) criteria.getValue());
                } else {
                    yield builder.greaterThan(getExpression(root), criteria.getValue().toString());
                }
            }
            case LESS_THAN -> {
                if (criteria.getValue() instanceof LocalDate) {
                    yield builder.lessThan(root.get(criteria.getKey()), (LocalDate) criteria.getValue());
                } else {
                    yield builder.lessThan(getExpression(root), criteria.getValue().toString());
                }
            }
            case LIKE -> builder.like(getExpression(root), criteria.getValue().toString());
            case STARTS_WITH -> builder.like(getExpression(root), criteria.getValue() + "%");
            case ENDS_WITH -> builder.like(getExpression(root), "%" + criteria.getValue());
            case CONTAINS -> builder.like(getExpression(root), "%" + criteria.getValue() + "%");
        };
    }

    private Path<String> getExpression(Root<Entity> root) {
        String name = criteria.getKey();

        if (!name.contains(".")) {
            return root.get(name);
        }

        String[] s = name.split("\\.");
        Path<String> res = root.get(s[0]);

        int i = 1;
        while (i < s.length) {
            res = res.get(s[i]);
            i++;
        }

        return res;
    }
}
