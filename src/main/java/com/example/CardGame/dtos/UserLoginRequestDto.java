package com.example.CardGame.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Getter
public class UserLoginRequestDto {
    @NotBlank(message = "login cannot be blank")
    private String login;
    @NotBlank(message = "password cannot be blank")
    private String password;
}
