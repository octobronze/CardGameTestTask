package com.example.CardGame.services;

import com.example.CardGame.dtos.CurrentTurnInfoResponseDto;
import com.example.CardGame.dtos.GameSessionResponseDto;
import com.example.CardGame.exceptions.BadRequestException;
import com.example.CardGame.repos.Card_GameSessionStartedRepository;
import com.example.CardGame.repos.GameSessionRepository;
import com.example.CardGame.repos.TurnRepository;
import com.example.CardGame.repos.User_GameSessionStartedRepository;
import com.example.CardGame.specifications.Card_GameSessionStartedSpecification;
import com.example.CardGame.specifications.FetchService;
import com.example.CardGame.specifications.GameSessionSpecification;
import com.example.CardGame.specifications.User_GameSessionStartedSpecification;
import com.example.CardGame.tables.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static com.example.CardGame.consts.ExceptionMessagesConsts.UNEXPECTED_BEHAVIOR;

@Service
@RequiredArgsConstructor
public class TurnService {
    private final TurnRepository turnRepository;
    private final User_GameSessionStartedRepository userGameSessionStartedRepository;
    private final Card_GameSessionStartedRepository cardGameSessionStartedRepository;
    private final GameSessionService gameSessionService;
    private final GameSessionRepository gameSessionRepository;

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public CurrentTurnInfoResponseDto getCurrentTurnInfo(int userId, int sessionId) {
        if (!userGameSessionStartedRepository.exists(
                User_GameSessionStartedSpecification.builder()
                        .userId(userId)
                        .gameSessionId(sessionId)
                        .isCurrent(true).build()
        )) {
            throw new BadRequestException(User_GameSessionStarted.ExceptionMessages.NOT_USER_TURN);
        }
        Card card =
                cardGameSessionStartedRepository.findOne(
                        Card_GameSessionStartedSpecification.builder()
                                .gameSessionId(sessionId)
                                .isCurrent(true)
                                .fetchService(new FetchService<>(
                                        List.of(FetchService.Field.CARD)
                                )).build()
                ).orElseThrow(() -> new BadRequestException(Card_GameSessionStarted.ExceptionMessage.CURRENT_CARD_NOT_FOUND))
                        .getCard();

        CurrentTurnInfoResponseDto responseDto = new CurrentTurnInfoResponseDto();
        responseDto.setCardType(card.getType().getName());
        responseDto.setActionCardType(
                Optional.ofNullable(card.getActionCardType())
                        .map(Card.ActionCardTypeEnum::getName).orElse(null)
        );
        responseDto.setCardName(card.getName());
        responseDto.setCardValue(card.getValue());

        return responseDto;
    }

    @Transactional
    public boolean doTurnAndReturnIsGameSessionFinished(int userId, int sessionId, Integer targetUserId) {
        TurnInfo turnInfo = getTurnInfo(userId, sessionId, targetUserId);
        ApplyEffectsInfo applyEffectsInfo = applyEffects(
                turnInfo.getUserGameSessionStarted(),
                turnInfo.cardGameSessionStarted.getCard(),
                turnInfo.getTargetUserGameSessionStarted()
        );
        Turn turn = initTurn(turnInfo, applyEffectsInfo);
        turnRepository.save(turn);

        if (applyEffectsInfo.isReachedMaxPoints()
                || turn.getTurnNum() == turn.getGameSession().getCardsNum()) {
            gameSessionService.finishGameSession(turnInfo.getGameSession());
            return true;
        } else {
            commitTurnEntries(turnInfo);
            setNextTurnEntries(turnInfo, applyEffectsInfo);
            return false;
        }
    }

    private Turn initTurn(TurnInfo turnInfo, ApplyEffectsInfo applyEffectsInfo) {
        Turn turn = new Turn();
        turn.setCard(turnInfo.getCardGameSessionStarted().getCard());
        turn.setUser(turnInfo.getUserGameSessionStarted().getUser());
        turn.setGameSession(turnInfo.getGameSession());
        turn.setTurnNum(turnInfo.getTurnNum());
        turn.setPointsDifference(applyEffectsInfo.pointsDifference);
        turn.setTargetUser(
                Optional.ofNullable(turnInfo.getTargetUserGameSessionStarted())
                        .map(User_GameSessionStarted::getUser).orElse(null)
        );

        return turn;
    }

    private void commitTurnEntries(TurnInfo turnInfo) {
        turnInfo.getUserGameSessionStarted().getTurnOrder().setCurrent(false);
        userGameSessionStartedRepository.save(turnInfo.getUserGameSessionStarted());
        turnInfo.getCardGameSessionStarted().getTurnOrder().setCurrent(false);
        cardGameSessionStartedRepository.save(turnInfo.getCardGameSessionStarted());
        if (turnInfo.getTargetUserGameSessionStarted() != null) {
            userGameSessionStartedRepository.save(turnInfo.getTargetUserGameSessionStarted());
        }
    }

    private void setNextTurnEntries(TurnInfo prevTurnInfo, ApplyEffectsInfo applyEffectsInfo) {
        int nextUserTurnNum = (prevTurnInfo.getPlayerOrderNum() % prevTurnInfo.getPlayersNum()) + 1
                + (applyEffectsInfo.skipTurn ? 1 : 0);
        int nextCardTurnNum = (prevTurnInfo.getCardOrderNum() % prevTurnInfo.getCardsNum()) + 1;
        int sessionId = prevTurnInfo.getGameSession().getId();

        User_GameSessionStarted userGameSessionStarted =
                userGameSessionStartedRepository.findWithLockForUpdate(
                        User_GameSessionStartedSpecification.builder()
                                .gameSessionId(sessionId)
                                .orderNum(nextUserTurnNum).build(),
                        User_GameSessionStarted.class
                ).orElseThrow(() -> new BadRequestException(User_GameSessionStarted.ExceptionMessages.NOT_USER_TURN));
        userGameSessionStarted.getTurnOrder().setCurrent(true);
        userGameSessionStartedRepository.save(userGameSessionStarted);
        Card_GameSessionStarted cardGameSessionStarted =
                cardGameSessionStartedRepository.findWithLockForUpdate(
                        Card_GameSessionStartedSpecification.builder()
                                .gameSessionId(sessionId)
                                .orderNum(nextCardTurnNum).build(),
                        Card_GameSessionStarted.class
                ).orElseThrow(() -> new BadRequestException(Card_GameSessionStarted.ExceptionMessage.CURRENT_CARD_NOT_FOUND));
        cardGameSessionStarted.getTurnOrder().setCurrent(true);
        cardGameSessionStartedRepository.save(cardGameSessionStarted);
    }

    private TurnInfo getTurnInfo(int userId, int sessionId, Integer targetUserId) {
        GameSession gameSession = gameSessionRepository.findWithLockForUpdate(
                GameSessionSpecification.builder()
                        .id(sessionId).build(),
                GameSession.class
        ).orElseThrow(() -> new BadRequestException(GameSession.ExceptionMessages.NOT_FOUND));
        if (gameSession.getState().equals(GameSession.StateEnum.FINISHED)) {
            throw new BadRequestException(GameSession.ExceptionMessages.ALREADY_FINISHED);
        }
        User_GameSessionStarted userGameSessionStarted =
                userGameSessionStartedRepository.findWithLockForUpdate(
                        User_GameSessionStartedSpecification.builder()
                                .userId(userId)
                                .gameSessionId(sessionId)
                                .isCurrent(true).build(),
                        User_GameSessionStarted.class
                ).orElseThrow(() -> new BadRequestException(User_GameSessionStarted.ExceptionMessages.NOT_USER_TURN));
        userGameSessionStarted = userGameSessionStartedRepository.findOne(
                User_GameSessionStartedSpecification.builder()
                        .userGameSessionStarted(userGameSessionStarted)
                        .fetchService(
                                new FetchService<>(
                                        List.of(FetchService.Field.USER)
                                )
                        ).build()
        ).orElseThrow(() -> new BadRequestException(User_GameSessionStarted.ExceptionMessages.NOT_USER_TURN));
        Card_GameSessionStarted cardGameSessionStarted =
                cardGameSessionStartedRepository.findWithLockForUpdate(
                        Card_GameSessionStartedSpecification.builder()
                                .gameSessionId(sessionId)
                                .isCurrent(true)
                                .fetchService(new FetchService<>(
                                        List.of(FetchService.Field.CARD)
                                )).build(),
                        Card_GameSessionStarted.class
                ).orElseThrow(() -> new BadRequestException(Card_GameSessionStarted.ExceptionMessage.CURRENT_CARD_NOT_FOUND));
        User_GameSessionStarted targetUserGameSessionStarted = null;
        if (targetUserId != null) {
            targetUserGameSessionStarted = userGameSessionStartedRepository.findWithLockForUpdate(
                    User_GameSessionStartedSpecification.builder()
                            .userId(targetUserId)
                            .gameSessionId(sessionId).build(),
                    User_GameSessionStarted.class
            ).orElseThrow(() -> new BadRequestException(User_GameSessionStarted.ExceptionMessages.TARGET_USER_NOT_EXISTS));
            targetUserGameSessionStarted = userGameSessionStartedRepository.findOne(
                    User_GameSessionStartedSpecification.builder()
                            .userGameSessionStarted(targetUserGameSessionStarted)
                            .fetchService(
                                    new FetchService<>(
                                            List.of(FetchService.Field.USER)
                                    )
                            ).build()
            ).orElseThrow(() -> new BadRequestException(User_GameSessionStarted.ExceptionMessages.TARGET_USER_NOT_EXISTS));
        }
        Turn prevTurn = turnRepository.findFirstByGameSession_IdOrderByTurnNumDesc(sessionId).orElse(null);

        TurnInfo turnInfo = new TurnInfo();
        turnInfo.setCardGameSessionStarted(cardGameSessionStarted);
        turnInfo.setUserGameSessionStarted(userGameSessionStarted);
        turnInfo.setTargetUserGameSessionStarted(targetUserGameSessionStarted);
        turnInfo.setCardsNum(gameSession.getCardsNum());
        turnInfo.setPlayersNum(gameSession.getUsersNum());
        turnInfo.setTurnNum(Optional.ofNullable(prevTurn).map(Turn::getTurnNum).orElse(0) + 1);
        turnInfo.setGameSession(gameSession);
        turnInfo.setPlayerOrderNum(userGameSessionStarted.getTurnOrder().getOrderNum());
        turnInfo.setCardOrderNum(cardGameSessionStarted.getTurnOrder().getOrderNum());

        return turnInfo;
    }

    private ApplyEffectsInfo applyEffects(User_GameSessionStarted currentUser, Card card, User_GameSessionStarted targetUser) {
        ApplyEffectsInfo applyEffectsInfo = new ApplyEffectsInfo();

        if (targetUser != null
                && (card.getActionCardType() == null
                || !card.getActionCardType().equals(Card.ActionCardTypeEnum.STEAL))) {
            throw new BadRequestException(Card.ExceptionMessages.INVALID_CARD_FOR_TARGET);
        }

        switch (card.getType()) {
            case ACTION_CARD -> {
                switch (card.getActionCardType()) {
                    case BLOCK -> {
                        if (card.getValue() == Card.Consts.BLOCK_VALUE) {
                            applyEffectsInfo.setSkipTurn(true);
                        }
                    }
                    case STEAL -> {
                        if (targetUser == null) {
                            throw new BadRequestException(Card.ExceptionMessages.TARGET_NOT_CHOSEN);
                        }
                        if (targetUser.getUser().getId() == currentUser.getUser().getId()) {
                            throw new BadRequestException(User_GameSessionStarted.ExceptionMessages.CURRENT_USER_CANNOT_BE_TARGET);
                        }
                        int pointsToSteal = Math.min(card.getValue(), targetUser.getPoints());
                        targetUser.setPoints(targetUser.getPoints() - pointsToSteal);
                        int targetPoints = Math.min(currentUser.getPoints() + pointsToSteal, GameSession.Consts.MAX_POINTS);
                        int pointsDiff = targetPoints - currentUser.getPoints();
                        currentUser.setPoints(targetPoints);
                        applyEffectsInfo.setPointsDifference(pointsDiff);
                    }
                    case DOUBLE_DOWN -> {
                        if (card.getValue() == Card.Consts.DD_VALUE) {
                            int targetPoints = Math.min(currentUser.getPoints() * 2, GameSession.Consts.MAX_POINTS);
                            int pointsDiff = targetPoints - currentUser.getPoints();
                            currentUser.setPoints(targetPoints);
                            applyEffectsInfo.setPointsDifference(pointsDiff);
                        }
                    }
                }
            }
            case POINTS_CARD -> {
                int targetPoints = Math.min(currentUser.getPoints() + card.getValue(), GameSession.Consts.MAX_POINTS);
                int pointsDiff = targetPoints - currentUser.getPoints();
                currentUser.setPoints(targetPoints);
                applyEffectsInfo.setPointsDifference(pointsDiff);
            }
        }
        if (currentUser.getPoints() == GameSession.Consts.MAX_POINTS) {
            applyEffectsInfo.setReachedMaxPoints(true);
        }

        return applyEffectsInfo;
    }

    @NoArgsConstructor
    @Setter
    @Getter
    private static class TurnInfo {
        User_GameSessionStarted userGameSessionStarted;
        Card_GameSessionStarted cardGameSessionStarted;
        User_GameSessionStarted targetUserGameSessionStarted;
        GameSession gameSession;
        int turnNum;
        int cardsNum;
        int playersNum;
        int cardOrderNum;
        int playerOrderNum;
    }

    @NoArgsConstructor
    @Setter
    @Getter
    private static class ApplyEffectsInfo {
        boolean skipTurn = false;
        boolean reachedMaxPoints = false;
        int pointsDifference = 0;
    }
}
