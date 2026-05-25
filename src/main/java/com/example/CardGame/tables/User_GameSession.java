package com.example.CardGame.tables;

import com.example.CardGame.tables.composite_keys.User_GameSessionCK;
import com.example.CardGame.tables.embeddable.TurnData;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Table(name = "user_to_game_session")
@Entity
@Getter
@Setter
@NoArgsConstructor
@IdClass(User_GameSessionCK.class)
public class User_GameSession {
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_session_id")
    private GameSession gameSession;

    @Embedded
    private TurnData turnData;

    @Column(name = "points", columnDefinition = "integer default 0")
    private Integer points;


    public User_GameSession(User user, GameSession gameSession) {
        this.user = user;
        this.gameSession = gameSession;
    }

    public static class ExceptionMessages {
        public static final String EXISTS = "User exists in current game session";
        public static final String NOT_USER_TURN = "It is not user turn";
        public static final String TARGET_USER_NOT_EXISTS = "Target user not exists";
        public static final String CURRENT_USER_CANNOT_BE_TARGET = "Current user cannot be target";
        public static final String NO_USER_FOR_NEXT_TURN = "User for next turn not found";
    }
}
