package com.example.CardGame.tables;

import com.example.CardGame.tables.composite_keys.GameSession_CardCK;
import com.example.CardGame.tables.embeddable.TurnData;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Table(name = "card_to_game_session")
@Entity
@Getter
@Setter
@NoArgsConstructor
@IdClass(GameSession_CardCK.class)
public class Card_GameSession {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_session_id")
    @Id
    protected GameSession gameSession;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "card_id")
    @Id
    protected Card card;

    @Embedded
    private TurnData turnData;

    public Card_GameSession(GameSession gameSession, Card card, int order) {
        this.gameSession = gameSession;
        this.card = card;
        this.turnData = new TurnData(order, false);
    }

    public static class ExceptionMessage {
        public static final String CURRENT_CARD_NOT_FOUND = "Current card not found";
    }
}
