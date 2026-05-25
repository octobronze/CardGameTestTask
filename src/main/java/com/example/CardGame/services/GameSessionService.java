package com.example.CardGame.services;

import com.example.CardGame.dtos.GameSessionResponseDto;
import com.example.CardGame.exceptions.BadRequestException;
import com.example.CardGame.repos.*;
import com.example.CardGame.specifications.fetch.Chain;
import com.example.CardGame.specifications.fetch.FetchList;
import com.example.CardGame.specifications.GameSessionSpecification;
import com.example.CardGame.tables.*;
import com.example.CardGame.tables.embeddable.TurnData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Для избежания data race - lock делается на GameSession.
 */
@Service
@RequiredArgsConstructor
@Transactional(isolation = Isolation.READ_COMMITTED)
public class GameSessionService {
    private final GameSessionRepository gameSessionRepository;
    private final UserRepository userRepository;
    private final User_GameSessionRepository user_gameSessionRepository;
    private final Card_GameSessionRepository cardGameSessionRepository;
    private final CardRepository cardRepository;
    private final DtoService dtoService;

    public int createGameSession(int userId) {
        var user = userRepository.getReferenceById(userId);
        GameSession gameSession = new GameSession();
        gameSession.setState(GameSession.State.WAIT_FOR_PLAYERS);
        gameSession.setCreatedBy(user);
        return gameSessionRepository.save(gameSession).getId();
    }

    /**
     * Присоединяет пользователя к сессии.
     *
     * @param userId  пользователь
     * @param gameSessionId  id сессии
     */
    public void joinGameSession(int userId, int gameSessionId) {
        var user = userRepository.getReferenceById(userId);
        // check
        if (user_gameSessionRepository.existsByGameSession_IdAndUser(gameSessionId, user)) {
            throw new BadRequestException(User_GameSession.ExceptionMessages.EXISTS);
        }
        // lock
        var gameSession = gameSessionRepository.findWithLockForUpdate(
                GameSessionSpecification.builder()
                        .id(gameSessionId)
                        .state(GameSession.State.WAIT_FOR_PLAYERS)
                        .isFull(false).build(),
                GameSession.class
        ).orElseThrow(() -> new BadRequestException(GameSession.ExceptionMessages.NOT_FOUND));
        var user_gameSession = new User_GameSession(user, gameSession);
        // Может выкидывать исключение в случае если запись уже существует(возможно из-за гонки).
        // Но ситуация остается валидной, так как транзакция откатится в случае исключения, как и должна.
        user_gameSessionRepository.save(user_gameSession);
        gameSession.setUsersNumber(gameSession.getUsersNumber() + 1);
        gameSessionRepository.save(gameSession);
        // unlock
    }

    /**
     * блабла
     * <p>
     *
     *
     * @param userId
     * @param gameSessionId
     */
    public void startGameSession(int userId, int gameSessionId) {
        var user = userRepository.getReferenceById(userId);
        // lock
        var gameSession = gameSessionRepository.findWithLockForUpdate(
                GameSessionSpecification.builder()
                        .id(gameSessionId)
                        .createdBy(user)
                        .state(GameSession.State.WAIT_FOR_PLAYERS)
                        .isEnoughPlayers(true).build(),
                GameSession.class
        ).orElseThrow(() -> new BadRequestException(GameSession.ExceptionMessages.NOT_FOUND));
        var gameCards  = initCardsForGame(gameSession);
        var gameUsers = initUsersForGame(gameSession);
        cardGameSessionRepository.saveAll(gameCards);
        user_gameSessionRepository.saveAll(gameUsers);
        gameSession.setState(GameSession.State.IN_PROGRESS);
        gameSession.setCardsNumber(gameCards.size());
        gameSessionRepository.save(gameSession);
        // unlock
    }

    public void finishGameSession(GameSession gameSession) {
        gameSession.setState(GameSession.State.FINISHED);
        gameSessionRepository.save(gameSession);
    }

    public GameSessionResponseDto getGameSession(int gameSessionId) {
        var gameSession = gameSessionRepository.findOne(
                GameSessionSpecification.builder()
                        .id(gameSessionId)
                        .fetchList(FetchList.of(Chain.of(GameSession_.gameSession_users, User_GameSession_.user))).build()
        ).orElseThrow(() -> new BadRequestException(GameSession.ExceptionMessages.NOT_FOUND));
        // Повторный вызов для fetch
        gameSessionRepository.findOne(
                GameSessionSpecification.builder()
                        .id(gameSessionId)
                        .fetchList(FetchList.of(
                                Chain.of(GameSession_.turns, Turn_.card),
                                Chain.of(GameSession_.turns, Turn_.user),
                                Chain.of(GameSession_.turns, Turn_.gameSession)
                        )).build()
        ).orElseThrow(() -> new BadRequestException(GameSession.ExceptionMessages.NOT_FOUND));
        // Повторный вызов для fetch
        gameSessionRepository.findOne(
                GameSessionSpecification.builder()
                        .id(gameSessionId)
                        .fetchList(FetchList.of(Chain.of(GameSession_.gameSession_Cards))).build()
        ).orElseThrow(() -> new BadRequestException(GameSession.ExceptionMessages.NOT_FOUND));
        return dtoService.gameSessionToDto(gameSession);
    }

    private List<Card_GameSession> initCardsForGame(GameSession gameSession) {
        var shuffledCards = getShuffledCards();
        var card_gameSessionList = new ArrayList<Card_GameSession>();
        for (int i = 0; i < shuffledCards.size(); i ++) {
            var card_gameSession = new Card_GameSession(gameSession, shuffledCards.get(i), i + 1);
            card_gameSessionList.add(card_gameSession);
        }
        card_gameSessionList.get(0).getTurnData().setIsCurrent(true);
        return card_gameSessionList;
    }

    private List<User_GameSession> initUsersForGame(GameSession gameSession) {
        var shuffledUsers = getShuffledUsers(gameSession);
        for (int i = 0; i < shuffledUsers.size(); i ++) {
            var user = shuffledUsers.get(i);
            user.setTurnData(new TurnData());
            user.getTurnData().setOrder(i + 1);
            user.setPoints(0);
        }
        shuffledUsers.get(0).getTurnData().setIsCurrent(true);
        return shuffledUsers;
    }

    private List<Card> getShuffledCards() {
        var cards = cardRepository.findAll();
        Collections.shuffle(cards);
        return cards;
    }

    private List<User_GameSession> getShuffledUsers(GameSession gameSession) {
        var users = gameSession.getGameSession_users();
        Collections.shuffle(users);
        return users;
    }
}
