package com.example.CardGame.repos;

import com.example.CardGame.repos.custom.CommonCustomRepository;
import com.example.CardGame.tables.GameSession;
import com.example.CardGame.tables.User_GameSessionStarted;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface User_GameSessionStartedRepository extends JpaRepository<User_GameSessionStarted, Integer>, JpaSpecificationExecutor<User_GameSessionStarted>, CommonCustomRepository<User_GameSessionStarted> {
    void deleteAllByGameSession(GameSession gameSession);
}
