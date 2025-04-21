package com.example.CardGame.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class PlayerResponseDto {
    private int id;
    private String name;
    private Integer points;
    private Boolean isCurrentTurn;
    private Integer turnOrderNum;
}
