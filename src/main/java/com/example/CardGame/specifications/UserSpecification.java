package com.example.CardGame.specifications;

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
public class UserSpecification implements Specification<User> {
    private static final String ID = "id";
    private static final String LOGIN = "login";

    private FetchService<User> fetchService;
    private Integer id;
    private String login;

    @Override
    public Predicate toPredicate(Root<User> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        Predicate predicate = criteriaBuilder.and();

        if (id != null) {
            predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get(ID), getId()));
        }
        if (login != null) {
            predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get(LOGIN), getLogin()));
        }

        if (fetchService != null) {
            fetchService.fetch(root);
        }

        return predicate;
    }
}
