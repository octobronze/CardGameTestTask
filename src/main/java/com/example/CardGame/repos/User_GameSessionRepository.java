package com.example.CardGame.repos;

import com.example.CardGame.repos.custom.CommonCustomRepository;
import com.example.CardGame.tables.GameSession;
import com.example.CardGame.tables.User;
import com.example.CardGame.tables.User_GameSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface User_GameSessionRepository extends JpaRepository<User_GameSession, Integer>, JpaSpecificationExecutor<User_GameSession>, CommonCustomRepository<User_GameSession> {
    boolean existsByGameSession_IdAndUser(int gameSessionId, User user);
    void deleteAllByGameSession(GameSession gameSession);
}
