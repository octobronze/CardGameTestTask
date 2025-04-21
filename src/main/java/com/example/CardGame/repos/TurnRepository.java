package com.example.CardGame.repos;

import com.example.CardGame.tables.Turn;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TurnRepository extends JpaRepository<Turn, Integer> {
    Optional<Turn> findFirstByGameSession_IdOrderByTurnNumDesc(int sessionId);
}
