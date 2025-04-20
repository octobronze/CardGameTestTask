package com.example.CardGame.repos;

import com.example.CardGame.tables.GameSession;
import com.example.CardGame.tables.User_GameSession;
import org.springframework.data.jpa.repository.JpaRepository;

public interface User_GameSessionRepository extends JpaRepository<User_GameSession, Integer> {
    void deleteAllByGameSession(GameSession gameSession);
}
