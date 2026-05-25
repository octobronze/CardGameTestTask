package com.example.CardGame.dtos;

import lombok.*;

import java.util.List;

@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
public class GameSessionResponseDto {
    private int id;
    private List<PlayerResponseDto> players;
    private List<TurnResponseDto> turns;
    private Integer cardsLeft;
    private int state;
}
