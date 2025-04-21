package com.example.CardGame.tables;

import com.example.CardGame.listeners.UsersNumForGameSessionManager;
import com.example.CardGame.tables.composite_keys.User_GameSessionCK;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Table(name = "user_to_game_session")
@Entity
@EntityListeners(value = {UsersNumForGameSessionManager.class})
@Getter
@Setter
@NoArgsConstructor
@IdClass(User_GameSessionCK.class)
public class User_GameSession {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @Id
    protected User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_session_id")
    @Id
    protected GameSession gameSession;

    public User_GameSession(User user, GameSession gameSession) {
        this.user = user;
        this.gameSession = gameSession;
    }

    public static class ExceptionMessages {
        public static final String USER_EXISTS = "User exists in current game session";
    }
}
