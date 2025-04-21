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
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

        return ResponseEntity.ok(gameSessionService.createGameSession(userPrincipal.getId()));
    }

    @PostMapping("/enter/{id}")
    public ResponseEntity<String> enterGameSession(Authentication authentication,
                                                   @PathVariable(name = "id") int sessionId) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        gameSessionService.enterGameSession(userPrincipal.getId(), sessionId);

        return ResponseEntity.ok("ok");
    }

    @PostMapping("/start/{id}")
    public ResponseEntity<String> startGameSession(Authentication authentication,
                                                   @PathVariable(name = "id") int sessionId) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        gameSessionService.startGameSession(sessionId, userPrincipal.getId());

        return ResponseEntity.ok("ok");
    }

    @GetMapping("/{id}")
    public ResponseEntity<GameSessionResponseDto> getGameSession(@PathVariable(name = "id") int sessionId) {
        return ResponseEntity.ok(gameSessionService.getGameSession(sessionId));
    }
}
