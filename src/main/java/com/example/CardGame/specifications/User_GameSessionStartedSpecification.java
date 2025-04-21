package com.example.CardGame.specifications;

import com.example.CardGame.tables.User;
import com.example.CardGame.tables.User_GameSessionStarted;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.parameters.P;

@Builder
@Getter
public class User_GameSessionStartedSpecification implements Specification<User_GameSessionStarted> {
    private static final String TURN_ORDER = "turnOrder";
    private static final String IS_CURRENT = "isCurrent";
    private static final String USER = "user";
    private static final String GAME_SESSION = "gameSession";
    private static final String ID = "id";
    private static final String ORDER_NUM = "orderNum";

    private FetchService<User_GameSessionStarted> fetchService;
    private Integer userId;
    private Integer gameSessionId;
    private Boolean isCurrent;
    private Integer orderNum;
    private User_GameSessionStarted userGameSessionStarted;

    @Override
    public Predicate toPredicate(Root<User_GameSessionStarted> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        Predicate predicate = criteriaBuilder.and();

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
            predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get(TURN_ORDER).get(IS_CURRENT), isCurrent));
        }
        if (orderNum != null) {
            predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get(TURN_ORDER).get(ORDER_NUM), orderNum));
        }

        if (fetchService != null) {
            fetchService.fetch(root);
        }

        return predicate;
    }
}
