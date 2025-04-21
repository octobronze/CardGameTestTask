package com.example.CardGame.security;


public class SecurityEndpoints {
    public static final String[] SECURED_ENDPOINTS = {
            "/game-session/**", "/turn/**"
    };
}
