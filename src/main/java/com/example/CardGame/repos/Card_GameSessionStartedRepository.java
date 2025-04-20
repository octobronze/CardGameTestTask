package com.example.CardGame.repos;

import com.example.CardGame.repos.custom.CommonCustomRepository;
import com.example.CardGame.tables.Card_GameSessionStarted;
import com.example.CardGame.tables.GameSession;
import org.springframework.data.jpa.repository.JpaRepository;

public interface Card_GameSessionStartedRepository extends JpaRepository<Card_GameSessionStarted, Integer>, CommonCustomRepository<Card_GameSessionStarted> {
    void deleteAllByGameSession(GameSession gameSession);
}
