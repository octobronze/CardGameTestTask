package com.example.CardGame.repos;

import com.example.CardGame.repos.custom.CommonCustomRepository;
import com.example.CardGame.tables.GameSession;
import com.example.CardGame.tables.User_GameSessionStarted;
import org.springframework.data.jpa.repository.JpaRepository;

public interface User_GameSessionStartedRepository extends JpaRepository<User_GameSessionStarted, Integer>, CommonCustomRepository<User_GameSessionStarted> {
    void deleteAllByGameSession(GameSession gameSession);
}
