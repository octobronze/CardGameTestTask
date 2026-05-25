package com.example.CardGame.controllers;

import com.example.CardGame.dtos.GameSessionResponseDto;
import com.example.CardGame.security.UserPrincipal;
import com.example.CardGame.services.GameSessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/game-session")
@RequiredArgsConstructor
public class GameSessionController {
    private final GameSessionService gameSessionService;

    @PostMapping
    public ResponseEntity<Integer> createGameSession(Authentication authentication) {
        var userPrincipal = (UserPrincipal) authentication.getPrincipal();

        return ResponseEntity.ok(gameSessionService.createGameSession(userPrincipal.getId()));
    }

    @PostMapping("/join/{id}")
    public ResponseEntity<String> joinGameSession(Authentication authentication,
                                                   @PathVariable(name = "id") int sessionId) {
        var userPrincipal = (UserPrincipal) authentication.getPrincipal();
        gameSessionService.joinGameSession(userPrincipal.getId(), sessionId);

        return ResponseEntity.ok("ok");
    }

    @PostMapping("/start/{id}")
    public ResponseEntity<String> startGameSession(Authentication authentication,
                                                   @PathVariable(name = "id") int sessionId) {
        var userPrincipal = (UserPrincipal) authentication.getPrincipal();
        gameSessionService.startGameSession(userPrincipal.getId(), sessionId);

        return ResponseEntity.ok("ok");
    }

    @GetMapping("/{id}")
    public ResponseEntity<GameSessionResponseDto> getGameSession(@PathVariable(name = "id") int sessionId) {
        return ResponseEntity.ok(gameSessionService.getGameSession(sessionId));
    }
}
