package com.example.CardGame.controllers;

import com.example.CardGame.dtos.UserLoginRequestDto;
import com.example.CardGame.dtos.UserLoginResponseDto;
import com.example.CardGame.dtos.UserRegistrationRequestDto;
import com.example.CardGame.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/registration")
    public ResponseEntity<Integer> registerUser(@RequestBody UserRegistrationRequestDto requestDto) {
        return ResponseEntity.ok(userService.registerUser(requestDto));
    }

    @PostMapping("/login")
    public ResponseEntity<UserLoginResponseDto> loginUser(@RequestBody UserLoginRequestDto requestDto) {
        return ResponseEntity.ok(userService.loginUser(requestDto));
    }
}
