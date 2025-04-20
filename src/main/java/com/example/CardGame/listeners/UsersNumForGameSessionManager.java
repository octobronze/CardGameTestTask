package com.example.CardGame.listeners;

import com.example.CardGame.tables.GameSession;
import com.example.CardGame.tables.User_GameSession;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreRemove;

public class UsersNumForGameSessionManager {

    @PreRemove
    private void decNumOfPlayers(User_GameSession userGameSession) {
        GameSession gameSession = userGameSession.getGameSession();
        gameSession.decUsersNum();
    }

    @PrePersist
    private void incNumOfPlayers(User_GameSession userGameSession) {
        GameSession gameSession = userGameSession.getGameSession();
        gameSession.incUsersNum();
    }
}
