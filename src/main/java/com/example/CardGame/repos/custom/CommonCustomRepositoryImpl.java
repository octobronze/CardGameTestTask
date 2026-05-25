package com.example.CardGame.repos.custom;

import jakarta.persistence.*;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CommonCustomRepositoryImpl<T> implements CommonCustomRepository<T> {
    @PersistenceContext
    private final EntityManager entityManager;

    @Override
    public Optional<T> findWithLockForUpdate(Specification<T> spec, Class<T> type) {
        var cb = entityManager.getCriteriaBuilder();
        var cq = cb.createQuery(type);
        var root = cq.from(type);
        cq.where(spec.toPredicate(root, cq, cb)).select(root);
        var query = entityManager.createQuery(cq);
        query.setLockMode(LockModeType.PESSIMISTIC_WRITE);
        try {
            return Optional.ofNullable(query.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Override
    public boolean existsWithLockForUpdate(Specification<T> spec, Class<T> type) {
        return findWithLockForUpdate(spec, type).isPresent();
    }
}
