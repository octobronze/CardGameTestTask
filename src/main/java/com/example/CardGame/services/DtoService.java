package com.example.CardGame.services;

import com.example.CardGame.dtos.GameSessionResponseDto;
import com.example.CardGame.dtos.PlayerResponseDto;
import com.example.CardGame.dtos.TurnResponseDto;
import com.example.CardGame.exceptions.BadRequestException;
import com.example.CardGame.tables.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class DtoService {
    private static final String UNEXPECTED_BEHAVIOR = "Unexpected behavior happens";

    public GameSessionResponseDto gameSessionToDto(GameSession gameSession) {
        var gameSessionResponseDto = new GameSessionResponseDto();
        gameSessionResponseDto.setId(gameSession.getId());
        gameSessionResponseDto.setState(gameSession.getState().ordinal());
        switch (gameSession.getState()) {
            case WAIT_FOR_PLAYERS -> {
                gameSessionResponseDto.setPlayers(
                        gameSession.getGameSession_users().stream()
                                .map(this::user_GameSessionToPlayerDto).toList()
                );
            }
            case IN_PROGRESS, FINISHED -> {
                gameSessionResponseDto.setPlayers(
                        gameSession.getGameSession_users().stream()
                                .map(this::user_GameSessionStartedToPlayerDto).toList()
                );
                gameSessionResponseDto.setCardsLeft(gameSession.getCardsNumber() - gameSession.getTurns().size() + 1);
                gameSessionResponseDto.setTurns(gameSession.getTurns().stream().map(this::turnToDto).toList());
            }
        }
        return gameSessionResponseDto;
    }

    public PlayerResponseDto user_GameSessionToPlayerDto(User_GameSession user_gameSession) {
        var playerResponseDto = new PlayerResponseDto();
        playerResponseDto.setId(user_gameSession.getUser().getId());
        playerResponseDto.setName(user_gameSession.getUser().getName());
        return playerResponseDto;
    }

    public PlayerResponseDto user_GameSessionStartedToPlayerDto(User_GameSession user_gameSession) {
        var playerResponseDto = new PlayerResponseDto();
        playerResponseDto.setId(user_gameSession.getUser().getId());
        playerResponseDto.setName(user_gameSession.getUser().getName());
        playerResponseDto.setPoints(user_gameSession.getPoints());
        playerResponseDto.setIsCurrentTurn(user_gameSession.getTurnData().getIsCurrent());
        playerResponseDto.setTurnOrderNum(user_gameSession.getTurnData().getOrder());
        return playerResponseDto;
    }

    public TurnResponseDto turnToDto(Turn turn) {
        var turnResponseDto = new TurnResponseDto();
        turnResponseDto.setId(turn.getId());
        turnResponseDto.setTurnNum(turn.getOrder());
        turnResponseDto.setPointsDiff(turn.getGainedPoints());
        turnResponseDto.setUserId(turn.getUser().getId());
        turnResponseDto.setSessionId(turn.getGameSession().getId());
        turnResponseDto.setTargetUserId(Optional.ofNullable(turn.getTarget()).map(User::getId).orElse(null));
        return turnResponseDto;
    }
}
