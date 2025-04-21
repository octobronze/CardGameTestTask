package com.example.CardGame.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Getter
public class TurnResponseDto {
    private int id;
    private int sessionId;
    private int userId;
    private int pointsDiff;
    private int turnNum;
    private Integer targetUserId;
}
