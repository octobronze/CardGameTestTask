package com.example.CardGame.tables;

import com.example.CardGame.tables.composite_keys.GameSession_CardCK;
import com.example.CardGame.tables.embeddable.TurnOrder;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Table(name = "card_to_game_session_card_started")
@Entity
@Getter
@Setter
@NoArgsConstructor
@IdClass(GameSession_CardCK.class)
public class Card_GameSessionStarted {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_session_id")
    @Id
    protected GameSession gameSession;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "card_id")
    @Id
    protected Card card;

    @Embedded
    private TurnOrder turnOrder;

    public Card_GameSessionStarted(GameSession gameSession, Card card, TurnOrder turnOrder) {
        this.gameSession = gameSession;
        this.card = card;
        this.turnOrder = turnOrder;
    }

    public static class ExceptionMessage {
        public static final String CURRENT_CARD_NOT_FOUND = "Current card not found";
    }
}
