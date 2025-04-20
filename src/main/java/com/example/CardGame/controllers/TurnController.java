package com.example.CardGame.controllers;

import com.example.CardGame.dtos.CurrentTurnInfoResponseDto;
import com.example.CardGame.security.UserPrincipal;
import com.example.CardGame.services.TurnService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/turn")
public class TurnController {
    private final TurnService turnService;

    @GetMapping("/info/session/{sessionId}")
    public ResponseEntity<CurrentTurnInfoResponseDto> getCurrentTurnInfo(Authentication authentication,
                                                                         @PathVariable(name = "sessionId") int gameSessionId) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

        return ResponseEntity.ok(turnService.getCurrentTurnInfo(userPrincipal.getId(), gameSessionId));
    }

    @PostMapping("/session/{sessionId}")
    public ResponseEntity<Boolean> doTurnAndReturnIsGameSessionFinished(Authentication authentication,
                                                                        @RequestParam(name = "targetUserId", required = false) Integer targetUserId,
                                                                        @PathVariable(name = "sessionId") int gameSessionId) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

        return ResponseEntity.ok(turnService.doTurnAndReturnIsGameSessionFinished(userPrincipal.getId(), gameSessionId, targetUserId));
    }
}
