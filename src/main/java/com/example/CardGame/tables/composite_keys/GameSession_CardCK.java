package com.example.CardGame.tables.composite_keys;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
public class GameSession_CardCK implements Serializable {
    protected int gameSession;
    protected int card;
}
