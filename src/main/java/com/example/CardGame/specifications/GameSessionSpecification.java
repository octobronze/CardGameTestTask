package com.example.CardGame.specifications;

import com.example.CardGame.specifications.fetch.FetchList;
import com.example.CardGame.specifications.fetch.FetchUtil;
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
    private static final String CREATED_BY = "createdBy";
    private static final String STATE = "state";
    private static final String USERS_NUMBER = "usersNumber";

    private Integer id;
    private GameSession gameSession;
    private User createdBy;
    private GameSession.State state;
    private Boolean isFull;
    private Boolean isEnoughPlayers;
    private FetchList fetchList;

    @Override
    public Predicate toPredicate(Root<GameSession> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        var predicate = criteriaBuilder.and();

        if (id != null) {
            predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get(ID), id));
        }
        if (gameSession != null) {
            predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root, gameSession));
        }
        if (createdBy != null) {
            predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get(CREATED_BY), createdBy));
        }
        if (state != null) {
            predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get(STATE), state));
        }
        if (isFull != null) {
            if (isFull) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get(USERS_NUMBER), GameSession.Consts.MAX_PLAYERS_PER_SESSION));
            } else {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.notEqual(root.get(USERS_NUMBER), GameSession.Consts.MAX_PLAYERS_PER_SESSION));
            }
        }
        if (isEnoughPlayers != null) {
            if (isEnoughPlayers) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.greaterThanOrEqualTo(root.get(USERS_NUMBER), GameSession.Consts.MIN_PLAYERS_TO_START));
            } else {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.lessThan(root.get(USERS_NUMBER), GameSession.Consts.MIN_PLAYERS_TO_START));
            }
        }
        if (fetchList != null) {
            FetchUtil.fetch(root, fetchList);
        }

        return predicate;
    }
}
