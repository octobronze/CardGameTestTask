package com.example.CardGame.tables.composite_keys;

import com.example.CardGame.tables.GameSession;
import com.example.CardGame.tables.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
public class User_GameSessionCK implements Serializable {
    protected int user;
    protected int gameSession;
}
