package com.example.CardGame.repos;

import com.example.CardGame.repos.custom.CommonCustomRepository;
import com.example.CardGame.tables.GameSession;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface GameSessionRepository extends JpaRepository<GameSession, Integer>, JpaSpecificationExecutor<GameSession>, CommonCustomRepository<GameSession> {
    @Query("select gs from GameSession gs where gs.id = :id")
    @Lock(value = LockModeType.PESSIMISTIC_WRITE)
    Optional<GameSession> findByIdWithLockForUpdate(@Param("id") int id);
}
