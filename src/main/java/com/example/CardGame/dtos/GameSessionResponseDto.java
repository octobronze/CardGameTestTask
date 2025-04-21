package com.example.CardGame.dtos;

import com.example.CardGame.tables.GameSession;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@Getter
@Setter
public class GameSessionResponseDto {
    private int id;
    private List<PlayerResponseDto> players;
    private List<TurnResponseDto> turns;
    private Integer cardsLeft;
    private int state;
}
