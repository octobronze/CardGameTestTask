package com.example.CardGame.tables.composite_keys;

import com.example.CardGame.tables.Card;
import com.example.CardGame.tables.GameSession;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.sql.results.graph.embeddable.internal.EmbeddableFetchImpl;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
public class GameSession_CardCK implements Serializable {
    protected int gameSession;
    protected int card;
}
