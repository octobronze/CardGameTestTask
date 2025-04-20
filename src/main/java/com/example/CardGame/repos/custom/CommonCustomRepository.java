package com.example.CardGame.repos.custom;

import com.example.CardGame.tables.GameSession;
import org.springframework.data.jpa.domain.Specification;

import java.util.Optional;

public interface CommonCustomRepository<T> {
    Optional<T> findWithLockForUpdate(Specification<T> spec, Class<T> type);
    boolean existsWithLockForUpdate(Specification<T> spec, Class<T> type);
}
