package com.example.CardGame.specifications;

import com.example.CardGame.specifications.fetch.FetchList;
import com.example.CardGame.specifications.fetch.FetchUtil;
import com.example.CardGame.tables.User_GameSession;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

@Builder
@Getter
public class User_GameSessionSpecification implements Specification<User_GameSession> {
    private static final String TURN_DATA = "turnData";
    private static final String IS_CURRENT = "isCurrent";
    private static final String USER = "user";
    private static final String GAME_SESSION = "gameSession";
    private static final String ID = "id";
    private static final String ORDER = "order";

    private Integer userId;
    private Integer gameSessionId;
    private Boolean isCurrent;
    private Integer order;
    private User_GameSession userGameSessionStarted;
    private FetchList fetchList;

    @Override
    public Predicate toPredicate(Root<User_GameSession> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        var predicate = criteriaBuilder.and();

        if (userGameSessionStarted != null) {
            predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root, userGameSessionStarted));
        }
        if (userId != null) {
            predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get(USER).get(ID), userId));
        }
        if (gameSessionId != null) {
            predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get(GAME_SESSION).get(ID), gameSessionId));
        }
        if (isCurrent != null) {
            predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get(TURN_DATA).get(IS_CURRENT), isCurrent));
        }
        if (order != null) {
            predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get(TURN_DATA).get(ORDER), order));
        }
        if (fetchList != null) {
            FetchUtil.fetch(root, fetchList);
        }

        return predicate;
    }
}
