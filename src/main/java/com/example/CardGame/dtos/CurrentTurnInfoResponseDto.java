package com.example.CardGame.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Getter
public class CurrentTurnInfoResponseDto {
    private String cardType;
    private String actionCardType;
    private String cardName;
    private int cardValue;
}
