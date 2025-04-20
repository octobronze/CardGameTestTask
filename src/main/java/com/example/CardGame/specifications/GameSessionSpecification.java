package com.example.CardGame.specifications;

import com.example.CardGame.tables.GameSession;
import com.example.CardGame.tables.User;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.jpa.domain.Specification;

@Builder
@Getter
public class GameSessionSpecification implements Specification<GameSession> {
    private static final String ID = "id";

    private Integer id;
    private FetchService<GameSession> fetchService;

    @Override
    public Predicate toPredicate(Root<GameSession> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        Predicate predicate = criteriaBuilder.and();

        if (id != null) {
            predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get(ID), getId()));
        }
        if (fetchService != null) {
            fetchService.fetch(root);
        }

        return predicate;
    }
}
