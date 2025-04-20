package com.example.CardGame.services;

import com.example.CardGame.exceptions.BadRequestException;
import com.example.CardGame.repos.*;
import com.example.CardGame.specifications.FetchService;
import com.example.CardGame.specifications.GameSessionSpecification;
import com.example.CardGame.tables.*;
import com.example.CardGame.tables.embeddable.TurnOrder;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GameSessionService {
    private final GameSessionRepository gameSessionRepository;
    private final UserRepository userRepository;
    private final User_GameSessionRepository user_gameSessionRepository;
    private final User_GameSessionStartedRepository userGameSessionStartedRepository;
    private final Card_GameSessionStartedRepository cardGameSessionStartedRepository;
    private final CardService cardService;

    @Transactional
    public int createGameSession(int userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new BadRequestException(User.ExceptionMessages.USER_NOT_FOUND));
        GameSession gameSession = new GameSession();
        gameSession.setState(GameSession.StateEnum.WAIT_FOR_PLAYERS);
        gameSession.setCreatedBy(user);
        int gameSessionId = gameSessionRepository.save(gameSession).getId();
        User_GameSession user_gameSession = new User_GameSession(user, gameSession);
        user_gameSessionRepository.save(user_gameSession);

        return gameSessionId;
    }

    @Transactional
    public void enterGameSession(int userId, int gameSessionId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new BadRequestException(User.ExceptionMessages.USER_NOT_FOUND));
        GameSession gameSession = gameSessionRepository.findByIdWithLockForUpdate(gameSessionId)
                .orElseThrow(() -> new BadRequestException(GameSession.ExceptionMessages.NOT_FOUND));
        if (gameSession.getUsersNum() >= GameSession.Consts.MAX_PLAYERS_PER_SESSION) {
            throw new BadRequestException(GameSession.ExceptionMessages.IS_FULL);
        }
        gameSessionRepository.save(gameSession);
        User_GameSession user_gameSession = new User_GameSession(user, gameSession);
        try {
            user_gameSessionRepository.save(user_gameSession);
        } catch (DataIntegrityViolationException e) {
            throw new BadRequestException(GameSession.ExceptionMessages.ALREADY_HAS_PLAYER);
        }
    }

    @Transactional
    public void startGameSession(int gameSessionId, int userId) {
        GameSession gameSession = gameSessionRepository.findWithLockForUpdate(
                GameSessionSpecification.builder()
                        .id(gameSessionId)
                        .fetchService(new FetchService<>(
                                List.of(FetchService.Field.GAME_SESSION_USERS, FetchService.Field.USER),
                                List.of(FetchService.Field.CREATED_BY)
                        ))
                        .build(),
                GameSession.class
                )
                .orElseThrow(() -> new BadRequestException(GameSession.ExceptionMessages.NOT_FOUND));
        if (!gameSession.getState().equals(GameSession.StateEnum.WAIT_FOR_PLAYERS)) {
            throw new BadRequestException(GameSession.ExceptionMessages.HAS_BEEN_STARTED);
        }
        if (gameSession.getUsersNum() < GameSession.Consts.MIN_PLAYERS_TO_START) {
            throw new BadRequestException(GameSession.ExceptionMessages.NOT_ENOUGH_PLAYERS);
        }
        if (gameSession.getCreatedBy().getId() != userId) {
            throw new BadRequestException(GameSession.ExceptionMessages.CAN_BE_STARTED_ONLY_BY_CREATOR);
        }
        int deckSize = createDeckAndApplyCardOrderAndReturnDeckSize(gameSession);
        applyUserOrder(gameSession);
        gameSession.setState(GameSession.StateEnum.IN_PROGRESS);
        gameSession.setCardsNum(deckSize);
        gameSessionRepository.save(gameSession);
    }

    @Transactional
    public void finishGameSession(GameSession gameSession) {
        gameSession.setState(GameSession.StateEnum.FINISHED);
        userGameSessionStartedRepository.deleteAllByGameSession(gameSession);
        cardGameSessionStartedRepository.deleteAllByGameSession(gameSession);
        user_gameSessionRepository.deleteAllByGameSession(gameSession);
        gameSessionRepository.save(gameSession);
    }

    private int createDeckAndApplyCardOrderAndReturnDeckSize(GameSession gameSession) {
        List<Card> deck = cardService.getDeckForGameSessionStart();

        List<Card_GameSessionStarted> card_gameSessionStartedList = new ArrayList<>();
        for (int i = 0; i < deck.size(); i ++) {
            TurnOrder turnOrder = new TurnOrder(i + 1, false);
            Card_GameSessionStarted _cardGameSessionStarted = new Card_GameSessionStarted(gameSession, deck.get(i), turnOrder);

            card_gameSessionStartedList.add(_cardGameSessionStarted);
        }
        card_gameSessionStartedList.get(0).getTurnOrder().setCurrent(true);
        cardGameSessionStartedRepository.saveAll(card_gameSessionStartedList);

        return deck.size();
    }

    private void applyUserOrder(GameSession gameSession) {
        List<User> users = gameSession.getGameSession_users().stream()
                .map(User_GameSession::getUser).collect(Collectors.toList());
        Collections.shuffle(users);

        List<User_GameSessionStarted> user_gameSessionStartedList = new ArrayList<>();
        for (int i = 0; i < users.size(); i ++) {
            TurnOrder turnOrder = new TurnOrder(i + 1, false);
            User_GameSessionStarted user_gameSessionStarted = new User_GameSessionStarted(users.get(i), gameSession, turnOrder);

            user_gameSessionStartedList.add(user_gameSessionStarted);
        }
        user_gameSessionStartedList.get(0).getTurnOrder().setCurrent(true);
        userGameSessionStartedRepository.saveAll(user_gameSessionStartedList);
    }
}
