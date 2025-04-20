package com.example.CardGame.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Getter
public class UserLoginResponseDto {
    private String token;
    private long lifeTimeMs;
}
