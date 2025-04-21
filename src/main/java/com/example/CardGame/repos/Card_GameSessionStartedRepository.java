package com.example.CardGame.repos;

import com.example.CardGame.repos.custom.CommonCustomRepository;
import com.example.CardGame.tables.Card_GameSessionStarted;
import com.example.CardGame.tables.GameSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface Card_GameSessionStartedRepository extends JpaRepository<Card_GameSessionStarted, Integer>, JpaSpecificationExecutor<Card_GameSessionStarted>, CommonCustomRepository<Card_GameSessionStarted> {
    void deleteAllByGameSession(GameSession gameSession);
}
