package com.example.CardGame.repos;

import com.example.CardGame.repos.custom.CommonCustomRepository;
import com.example.CardGame.tables.Card_GameSession;
import com.example.CardGame.tables.GameSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface Card_GameSessionRepository extends JpaRepository<Card_GameSession, Integer>, JpaSpecificationExecutor<Card_GameSession>, CommonCustomRepository<Card_GameSession> {
    void deleteAllByGameSession(GameSession gameSession);
}
