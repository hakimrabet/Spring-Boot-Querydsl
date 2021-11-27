package com.hamrasta.springbootquerydsl.querydsl;

import com.querydsl.core.types.dsl.BooleanExpression;

import java.util.ArrayList;
import java.util.List;

public class UserPredicatesBuilder {

    private final List<SearchCriteria> params;

    public UserPredicatesBuilder() {
        this.params = new ArrayList<>();
    }

    public UserPredicatesBuilder with(String key, String operation, Object value) {
        params.add(SearchCriteria.builder()
                .key(key)
                .operation(operation)
                .value(value)
                .build());
        return this;
    }

    public BooleanExpression build() {
        if (params.size() == 0)
            return null;

        List<BooleanExpression> predicates = new ArrayList<>();
        UserPredicate predicate;
        for (SearchCriteria param : params) {
            predicate = new UserPredicate(param);
            BooleanExpression exp = predicate.getPredicate();
            if (exp != null)
                predicates.add(exp);
        }
        BooleanExpression result = predicates.get(0);
        for (int i = 1; i < predicates.size(); i++) {
            result = result.and(predicates.get(i));
        }
        return result;
    }
}
