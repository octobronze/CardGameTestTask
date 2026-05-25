package com.example.CardGame.services;

import com.example.CardGame.dtos.CurrentTurnInfoResponseDto;
import com.example.CardGame.exceptions.BadRequestException;
import com.example.CardGame.repos.Card_GameSessionRepository;
import com.example.CardGame.repos.GameSessionRepository;
import com.example.CardGame.repos.TurnRepository;
import com.example.CardGame.repos.User_GameSessionRepository;
import com.example.CardGame.specifications.Card_GameSessionSpecification;
import com.example.CardGame.specifications.GameSessionSpecification;
import com.example.CardGame.specifications.User_GameSessionSpecification;
import com.example.CardGame.tables.*;
import jakarta.annotation.Nullable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Для избежания data race - lock делается на GameSession.
 */
@Service
@RequiredArgsConstructor
@Transactional(isolation = Isolation.READ_COMMITTED)
public class TurnService {
    private final TurnRepository turnRepository;
    private final Card_GameSessionRepository card_gameSessionRepository;
    private final GameSessionService gameSessionService;
    private final GameSessionRepository gameSessionRepository;
    private final User_GameSessionRepository user_gameSessionRepository;

    public CurrentTurnInfoResponseDto getCurrentTurnInfo(int userId, int gameSessionId) {
        if (!gameSessionRepository.existsById(gameSessionId)) {
            throw new BadRequestException(GameSession.ExceptionMessages.NOT_FOUND);
        }
        if (!user_gameSessionRepository.exists(
                User_GameSessionSpecification.builder()
                        .userId(userId)
                        .gameSessionId(gameSessionId)
                        .isCurrent(true).build()
        )) {
            throw new BadRequestException(User_GameSession.ExceptionMessages.NOT_USER_TURN);
        }
        var card = card_gameSessionRepository.findOne(
                Card_GameSessionSpecification.builder()
                        .gameSessionId(gameSessionId)
                        .isCurrent(true).build()
        ).orElseThrow(() -> new BadRequestException(Card_GameSession.ExceptionMessage.CURRENT_CARD_NOT_FOUND)).getCard();

        var responseDto = new CurrentTurnInfoResponseDto();
        responseDto.setCardType(card.getType().getName());
        responseDto.setActionCardType(
                Optional.ofNullable(card.getActionCardType()).map(Card.ActionCardType::getName).orElse(null)
        );
        responseDto.setCardName(card.getName());
        responseDto.setCardValue(card.getValue());

        return responseDto;
    }

    public boolean doTurn(int userId, int sessionId, @Nullable Integer targetUserId) {
        // Сразу делаем лок на сессии, чтобы не скрывать его в кишках других методов.
        if (!gameSessionRepository.existsWithLockForUpdate(GameSessionSpecification.builder().id(sessionId).build(), GameSession.class)) {
            throw new BadRequestException(GameSession.ExceptionMessages.NOT_FOUND);
        }
        var turnInfo = getTurnInfo(userId, sessionId, targetUserId);
        var applyEffectsInfo = applyEffects(turnInfo.getUser(), turnInfo.getCard().getCard(), turnInfo.getTarget());
        commitTurn(turnInfo, applyEffectsInfo);
        unsetCurrentTurnEntries(turnInfo);
        if (isGameFinished(turnInfo)) {
            gameSessionService.finishGameSession(turnInfo.getGameSession());
            return true;
        } else {
            setNextTurnEntries(turnInfo, applyEffectsInfo);
            return false;
        }
    }

    private void commitTurn(TurnInfo turnInfo, ApplyEffectsInfo applyEffectsInfo) {
        var turn = initTurn(turnInfo, applyEffectsInfo);
        turnRepository.save(turn);
    }

    private Turn initTurn(TurnInfo turnInfo, ApplyEffectsInfo applyEffectsInfo) {
        var turn = new Turn();
        turn.setCard(turnInfo.getCard().getCard());
        turn.setUser(turnInfo.getUser().getUser());
        turn.setGameSession(turnInfo.getGameSession());
        turn.setOrder(turnInfo.getTurnOrder());
        turn.setGainedPoints(applyEffectsInfo.getGainedPoints());
        turn.setTarget(Optional.ofNullable(turnInfo.getTarget()).map(User_GameSession::getUser).orElse(null));
        return turn;
    }

    private boolean isGameFinished(TurnInfo turnInfo) {
        return turnInfo.getUser().getPoints() == GameSession.Consts.MAX_POINTS
                || turnInfo.getTurnOrder() == turnInfo.getGameSession().getCardsNumber();
    }

    private void unsetCurrentTurnEntries(TurnInfo turnInfo) {
        turnInfo.getUser().getTurnData().setIsCurrent(false);
        user_gameSessionRepository.save(turnInfo.getUser());
        turnInfo.getCard().getTurnData().setIsCurrent(false);
        card_gameSessionRepository.save(turnInfo.getCard());
        if (turnInfo.getTarget() != null) {
            user_gameSessionRepository.save(turnInfo.getTarget());
        }
    }

    private void setNextTurnEntries(TurnInfo prevTurnInfo, ApplyEffectsInfo applyEffectsInfo) {
        int nextUserTurnOrder = ((prevTurnInfo.getUserOrder() + (applyEffectsInfo.skipTurn ? 1 : 0)) % prevTurnInfo.getGameSession().getUsersNumber()) + 1;
        int nextCardTurnOrder = (prevTurnInfo.getCardOrder() % prevTurnInfo.getGameSession().getCardsNumber()) + 1;
        int sessionId = prevTurnInfo.getGameSession().getId();

        var user = user_gameSessionRepository.findOne(
                User_GameSessionSpecification.builder()
                        .gameSessionId(sessionId)
                        .order(nextUserTurnOrder).build()
        ).orElseThrow(() -> new BadRequestException(User_GameSession.ExceptionMessages.NO_USER_FOR_NEXT_TURN));
        user.getTurnData().setIsCurrent(true);
        user_gameSessionRepository.save(user);

        var card = card_gameSessionRepository.findOne(
                Card_GameSessionSpecification.builder()
                        .gameSessionId(sessionId)
                        .order(nextCardTurnOrder).build()
        ).orElseThrow(() -> new BadRequestException(Card_GameSession.ExceptionMessage.CURRENT_CARD_NOT_FOUND));
        card.getTurnData().setIsCurrent(true);
        card_gameSessionRepository.save(card);
    }

    private TurnInfo getTurnInfo(int userId, int sessionId, @Nullable Integer targetId) {
        var gameSession = gameSessionRepository.findOne(
                GameSessionSpecification.builder().id(sessionId).build()
        ).orElseThrow(() -> new BadRequestException(GameSession.ExceptionMessages.NOT_FOUND));
        if (gameSession.getState().equals(GameSession.State.FINISHED)) {
            throw new BadRequestException(GameSession.ExceptionMessages.ALREADY_FINISHED);
        }
        var user = user_gameSessionRepository.findOne(
                User_GameSessionSpecification.builder()
                        .userId(userId)
                        .gameSessionId(sessionId)
                        .isCurrent(true).build()
        ).orElseThrow(() -> new BadRequestException(User_GameSession.ExceptionMessages.NOT_USER_TURN));
        var card = card_gameSessionRepository.findOne(
                Card_GameSessionSpecification.builder()
                        .gameSessionId(sessionId)
                        .isCurrent(true).build()
        ).orElseThrow(() -> new BadRequestException(Card_GameSession.ExceptionMessage.CURRENT_CARD_NOT_FOUND));
        var target = Optional.ofNullable(targetId).map(id -> user_gameSessionRepository.findOne(
                User_GameSessionSpecification.builder()
                        .userId(id)
                        .gameSessionId(sessionId).build())
                        .orElseThrow(() -> new BadRequestException(User_GameSession.ExceptionMessages.TARGET_USER_NOT_EXISTS))
        ).orElse(null);
        var previousTurn = turnRepository.findFirstByGameSession_IdOrderByOrderDesc(sessionId).orElse(null);

        TurnInfo turnInfo = new TurnInfo();
        turnInfo.setCard(card);
        turnInfo.setUser(user);
        turnInfo.setTarget(target);
        turnInfo.setTurnOrder(Optional.ofNullable(previousTurn).map(Turn::getOrder).orElse(0) + 1);
        turnInfo.setGameSession(gameSession);
        turnInfo.setUserOrder(user.getTurnData().getOrder());
        turnInfo.setCardOrder(card.getTurnData().getOrder());

        return turnInfo;
    }

    private ApplyEffectsInfo applyEffects(User_GameSession user, Card card, User_GameSession target) {
        var applyEffectsInfo = new ApplyEffectsInfo();
        switch (card.getType()) {
            case ACTION_CARD -> {
                switch (card.getActionCardType()) {
                    case BLOCK -> {
                        applyEffectsInfo.setSkipTurn(true);
                    }
                    case STEAL -> {
                        int targetFinalPoints = Math.max(target.getPoints() - card.getValue(), 0);
                        int userFinalPoints = Math.min(user.getPoints() + (target.getPoints() - targetFinalPoints), GameSession.Consts.MAX_POINTS);
                        applyEffectsInfo.setGainedPoints(userFinalPoints - user.getPoints());
                        target.setPoints(targetFinalPoints);
                        user.setPoints(userFinalPoints);
                    }
                    case DOUBLE_DOWN -> {
                        int finalPoints = Math.min(user.getPoints() * 2, GameSession.Consts.MAX_POINTS);
                        applyEffectsInfo.setGainedPoints(finalPoints - user.getPoints());
                        user.setPoints(finalPoints);
                    }
                }
            }
            case POINTS_CARD -> {
                int finalPoints = Math.min(user.getPoints() + card.getValue(), GameSession.Consts.MAX_POINTS);
                applyEffectsInfo.setGainedPoints(finalPoints - user.getPoints());
                user.setPoints(finalPoints);
            }
        }
        return applyEffectsInfo;
    }

    @NoArgsConstructor
    @Setter
    @Getter
    private static class TurnInfo {
        User_GameSession user;
        User_GameSession target;
        Card_GameSession card;
        GameSession gameSession;
        int turnOrder;
        int cardOrder;
        int userOrder;
    }

    @NoArgsConstructor
    @Setter
    @Getter
    private static class ApplyEffectsInfo {
        boolean skipTurn = false;
        int gainedPoints = 0;
    }
}
