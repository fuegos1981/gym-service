package com.gym.crm.specification;

import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public class SpecificationsBuilder {

    public final List<SearchCriteria> params;

    public SpecificationsBuilder(List<SearchCriteria> params) {
        this.params = params;
    }

    public <Entity> Specification<Entity> build() {
        if (params.size() == 0) {
            return null;
        }

        Specification<Entity> result = new SearchSpecification<>(params.get(0));

        for (int i = 1; i < params.size(); i++) {
            result = Specification.where(result).and(new SearchSpecification<>(params.get(i)));
        }

        return result;
    }
}
