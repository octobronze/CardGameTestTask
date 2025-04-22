package com.example.CardGame.tables;

import com.example.CardGame.tables.composite_keys.User_GameSessionCK;
import com.example.CardGame.tables.embeddable.TurnOrder;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Table(name = "user_to_game_session_started")
@Entity
@Getter
@Setter
@NoArgsConstructor
@IdClass(User_GameSessionCK.class)
public class User_GameSessionStarted {
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_session_id")
    protected GameSession gameSession;

    @Embedded
    private TurnOrder turnOrder;

    @Column(name = "points", nullable = false, columnDefinition = "integer default 0")
    private int points;

    public User_GameSessionStarted(User user, GameSession gameSession, TurnOrder turnOrder) {
        this.user = user;
        this.gameSession = gameSession;
        this.turnOrder = turnOrder;
    }

    public static class ExceptionMessages {
        public static final String NOT_USER_TURN = "It is not user turn";
        public static final String TARGET_USER_NOT_EXISTS = "Target user not exists";
        public static final String CURRENT_USER_CANNOT_BE_TARGET = "Current user cannot be target";
        public static final String NO_USER_FOR_NEXT_TURN = "User for next turn not found";
    }
}
