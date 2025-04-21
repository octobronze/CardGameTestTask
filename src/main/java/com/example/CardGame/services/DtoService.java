package com.example.CardGame.services;

import com.example.CardGame.dtos.GameSessionResponseDto;
import com.example.CardGame.dtos.PlayerResponseDto;
import com.example.CardGame.dtos.TurnResponseDto;
import com.example.CardGame.exceptions.BadRequestException;
import com.example.CardGame.tables.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.example.CardGame.consts.ExceptionMessagesConsts.UNEXPECTED_BEHAVIOR;

@RequiredArgsConstructor
@Service
public class DtoService {

    public GameSessionResponseDto gameSessionToDto(GameSession gameSession) {
        GameSessionResponseDto gameSessionResponseDto = new GameSessionResponseDto();

        gameSessionResponseDto.setId(gameSession.getId());
        gameSessionResponseDto.setState(gameSession.getState().ordinal());
        switch (gameSession.getState()) {
            case WAIT_FOR_PLAYERS -> {
                gameSessionResponseDto.setPlayers(
                        gameSession.getGameSession_users().stream()
                                .map(this::user_GameSessionToPlayerDto).toList()
                );
            }
            case FINISHED, IN_PROGRESS -> {
                gameSessionResponseDto.setPlayers(
                        gameSession.getGameSessionStarted_Users().stream()
                                .map(this::user_GameSessionStartedToPlayerDto).toList()
                );
                int currentCardNumOptional = gameSession.getGameSessionStarted_Cards().stream()
                        .filter(x -> x.getTurnOrder().isCurrent())
                        .map(x -> x.getTurnOrder().getOrderNum())
                        .findFirst().orElseThrow(() -> new BadRequestException(UNEXPECTED_BEHAVIOR));
                gameSessionResponseDto.setCardsLeft(gameSession.getCardsNum() - currentCardNumOptional + 1);
                gameSessionResponseDto.setTurns(gameSession.getTurns().stream().map(this::turnToDto).toList());
            }
        }

        return gameSessionResponseDto;
    }

    public PlayerResponseDto user_GameSessionToPlayerDto(User_GameSession user_gameSession) {
        PlayerResponseDto playerResponseDto = new PlayerResponseDto();
        playerResponseDto.setId(user_gameSession.getUser().getId());
        playerResponseDto.setName(user_gameSession.getUser().getName());

        return playerResponseDto;
    }

    public PlayerResponseDto user_GameSessionStartedToPlayerDto(User_GameSessionStarted user_gameSessionStarted) {
        PlayerResponseDto playerResponseDto = new PlayerResponseDto();
        playerResponseDto.setId(user_gameSessionStarted.getUser().getId());
        playerResponseDto.setName(user_gameSessionStarted.getUser().getName());
        playerResponseDto.setPoints(user_gameSessionStarted.getPoints());
        playerResponseDto.setIsCurrentTurn(user_gameSessionStarted.getTurnOrder().isCurrent());
        playerResponseDto.setTurnOrderNum(user_gameSessionStarted.getTurnOrder().getOrderNum());

        return playerResponseDto;
    }

    public TurnResponseDto turnToDto(Turn turn) {
        TurnResponseDto turnResponseDto = new TurnResponseDto();

        turnResponseDto.setId(turn.getId());
        turnResponseDto.setTurnNum(turn.getTurnNum());
        turnResponseDto.setPointsDiff(turn.getPointsDifference());
        turnResponseDto.setUserId(turn.getUser().getId());
        turnResponseDto.setSessionId(turn.getGameSession().getId());
        turnResponseDto.setTargetUserId(Optional.ofNullable(turn.getTargetUser()).map(User::getId).orElse(null));

        return turnResponseDto;
    }
}
