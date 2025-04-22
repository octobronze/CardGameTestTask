package com.example.CardGame;

import com.example.CardGame.repos.*;
import com.example.CardGame.services.GameSessionService;
import com.example.CardGame.services.TurnService;
import com.example.CardGame.specifications.User_GameSessionStartedSpecification;
import com.example.CardGame.tables.*;
import com.example.CardGame.tables.embeddable.TurnOrder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentMatcher;
import org.mockito.Mockito;
import org.springframework.data.jpa.domain.Specification;

import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

class TurnServiceUnitTests {
	private final TurnRepository turnRepository = Mockito.mock(TurnRepository.class);
	private final User_GameSessionStartedRepository userGameSessionStartedRepository = Mockito.mock(User_GameSessionStartedRepository.class);
	private final Card_GameSessionStartedRepository cardGameSessionStartedRepository = Mockito.mock(Card_GameSessionStartedRepository.class);
	private final GameSessionService gameSessionService = Mockito.mock(GameSessionService.class);
	private final GameSessionRepository gameSessionRepository = Mockito.mock(GameSessionRepository.class);

	private final TurnService turnService = new TurnService(turnRepository, userGameSessionStartedRepository,
			cardGameSessionStartedRepository, gameSessionService, gameSessionRepository);

	private static final int FIRST_USER_ID = 1;
	private static final int SECOND_USER_ID = 2;
	private static final int NEXT_USER_TURN_NUM = 2;
	private static final int SESSION_ID = 1;
	private static final int SESSION_CARDS_NUM = 2;
	private static final int SESSION_PLAYERS_NUM = 2;
	private static final int BLOCK_CARD_VALUE = Card.Consts.BLOCK_VALUE;
	private static final int DD_CARD_VALUE = Card.Consts.DD_VALUE;
	private static final int STEAL_CARD_VALUE = 10;
	private static final int POINT_CARD_VALUE = 15;
	private static final int BIG_POINT_CARD_VALUE = GameSession.Consts.MAX_POINTS;
	private static final int PLAYER_POINTS = 10;
	private static final int TURN_ORDER_NUM = 5;
	private static final int TARGET_PLAYER_POINTS = 10;
	private static final int CARD_TURN_ORDER_NUM = 1;

	private static final Card ACTION_BLOCK_CARD = new Card();
	private static final Card POINT_CARD = new Card();
	private static final Card ACTION_STEAL_CARD = new Card();
	private static final Card ACTION_DOUBLE_DOWN_CARD = new Card();
	private static final Card BIG_POINT_CARD = new Card();
	private static final GameSession GAME_SESSION_IN_PROGRESS = new GameSession();
	private static final User_GameSessionStarted FIRST_PLAYER = new User_GameSessionStarted();
	private static final TurnOrder PLAYER_TURN_ORDER = new TurnOrder();
	private static final User_GameSessionStarted SECOND_PLAYER = new User_GameSessionStarted();
	private static final User FIRST_USER = new User();
	private static final User SECOND_USER = new User();
	private static final Card_GameSessionStarted ACTION_BLOCK_CARD_IN_SESSION = new Card_GameSessionStarted();
	private static final Card_GameSessionStarted POINT_CARD_IN_SESSION = new Card_GameSessionStarted();
	private static final Card_GameSessionStarted ACTION_STEAL_CARD_IN_SESSION = new Card_GameSessionStarted();
	private static final Card_GameSessionStarted ACTION_DD_CARD_IN_SESSION = new Card_GameSessionStarted();
	private static final Card_GameSessionStarted BIG_POINT_CARD_IN_SESSION = new Card_GameSessionStarted();
	private static final TurnOrder CARD_TURN_ORDER = new TurnOrder();
	private static final Turn PRE_FINAL_TURN = new Turn();

	@BeforeEach
	public void initBeforeEach() {
		resetStaticVariables();
	}

	private void resetStaticVariables() {
		GAME_SESSION_IN_PROGRESS.setState(GameSession.StateEnum.IN_PROGRESS);
		GAME_SESSION_IN_PROGRESS.setCardsNum(SESSION_PLAYERS_NUM);
		GAME_SESSION_IN_PROGRESS.setCardsNum(SESSION_CARDS_NUM);
		GAME_SESSION_IN_PROGRESS.setUsersNum(SESSION_PLAYERS_NUM);

		PLAYER_TURN_ORDER.setOrderNum(TURN_ORDER_NUM);
		PLAYER_TURN_ORDER.setCurrent(true);

		FIRST_USER.setId(FIRST_USER_ID);

		SECOND_USER.setId(SECOND_USER_ID);

		FIRST_PLAYER.setPoints(PLAYER_POINTS);
		FIRST_PLAYER.setTurnOrder(PLAYER_TURN_ORDER);
		FIRST_PLAYER.setUser(FIRST_USER);

		SECOND_PLAYER.setPoints(TARGET_PLAYER_POINTS);
		SECOND_PLAYER.setUser(SECOND_USER);
		SECOND_PLAYER.setTurnOrder(PLAYER_TURN_ORDER);

		CARD_TURN_ORDER.setCurrent(true);
		CARD_TURN_ORDER.setOrderNum(CARD_TURN_ORDER_NUM);

		ACTION_BLOCK_CARD.setActionCardType(Card.ActionCardTypeEnum.BLOCK);
		ACTION_BLOCK_CARD.setType(Card.TypeEnum.ACTION_CARD);
		ACTION_BLOCK_CARD.setValue(BLOCK_CARD_VALUE);

		ACTION_BLOCK_CARD_IN_SESSION.setTurnOrder(CARD_TURN_ORDER);
		ACTION_BLOCK_CARD_IN_SESSION.setCard(ACTION_BLOCK_CARD);
		ACTION_BLOCK_CARD_IN_SESSION.setGameSession(GAME_SESSION_IN_PROGRESS);

		POINT_CARD.setType(Card.TypeEnum.POINTS_CARD);
		POINT_CARD.setValue(POINT_CARD_VALUE);

		POINT_CARD_IN_SESSION.setTurnOrder(CARD_TURN_ORDER);
		POINT_CARD_IN_SESSION.setCard(POINT_CARD);
		POINT_CARD_IN_SESSION.setGameSession(GAME_SESSION_IN_PROGRESS);

		ACTION_STEAL_CARD.setActionCardType(Card.ActionCardTypeEnum.STEAL);
		ACTION_STEAL_CARD.setType(Card.TypeEnum.ACTION_CARD);
		ACTION_STEAL_CARD.setValue(STEAL_CARD_VALUE);

		ACTION_STEAL_CARD_IN_SESSION.setTurnOrder(CARD_TURN_ORDER);
		ACTION_STEAL_CARD_IN_SESSION.setCard(ACTION_STEAL_CARD);
		ACTION_STEAL_CARD_IN_SESSION.setGameSession(GAME_SESSION_IN_PROGRESS);

		ACTION_DOUBLE_DOWN_CARD.setActionCardType(Card.ActionCardTypeEnum.DOUBLE_DOWN);
		ACTION_DOUBLE_DOWN_CARD.setType(Card.TypeEnum.ACTION_CARD);
		ACTION_DOUBLE_DOWN_CARD.setValue(DD_CARD_VALUE);

		ACTION_DD_CARD_IN_SESSION.setTurnOrder(CARD_TURN_ORDER);
		ACTION_DD_CARD_IN_SESSION.setCard(ACTION_DOUBLE_DOWN_CARD);
		ACTION_DD_CARD_IN_SESSION.setGameSession(GAME_SESSION_IN_PROGRESS);

		BIG_POINT_CARD.setType(Card.TypeEnum.POINTS_CARD);
		BIG_POINT_CARD.setValue(BIG_POINT_CARD_VALUE);

		BIG_POINT_CARD_IN_SESSION.setTurnOrder(CARD_TURN_ORDER);
		BIG_POINT_CARD_IN_SESSION.setCard(BIG_POINT_CARD);
		BIG_POINT_CARD_IN_SESSION.setGameSession(GAME_SESSION_IN_PROGRESS);

		PRE_FINAL_TURN.setTurnNum(SESSION_CARDS_NUM - 1);
	}

	@ParameterizedTest
	@MethodSource("provideData_for_test_for_doTurnAndReturnIsGameSessionFinished_session_finished")
	public void test_for_doTurnAndReturnIsGameSessionFinished_session_finished(int userId, int sessionId,
																			   GameSession gameSession,
																			   User_GameSessionStarted player,
																			   User_GameSessionStarted secondPlayer,
																			   Card_GameSessionStarted cardInSession,
																			   int nextUserTurnNum,
																			   Turn preFinalTurn) {
		Mockito.doReturn(Optional.ofNullable(gameSession))
				.when(gameSessionRepository).findWithLockForUpdate(Mockito.any(Specification.class), Mockito.any(Class.class));
		Mockito.doReturn(Optional.ofNullable(player)).when(userGameSessionStartedRepository).findWithLockForUpdate(
				Mockito.argThat(
						new PlayerSpecificationMatcher(
								User_GameSessionStartedSpecification.builder()
										.userId(userId).build()
						)),
				Mockito.any(Class.class)
		);
		Mockito.doReturn(Optional.ofNullable(player)).when(userGameSessionStartedRepository).findOne(
				Mockito.argThat(
						new PlayerSpecificationMatcher(
								User_GameSessionStartedSpecification.builder()
										.userGameSessionStarted(player).build()
						)
				)
		);
		Mockito.doReturn(Optional.ofNullable(cardInSession)).when(cardGameSessionStartedRepository)
				.findWithLockForUpdate(Mockito.any(Specification.class), Mockito.any(Class.class));
		Mockito.doReturn(Optional.ofNullable(preFinalTurn)).when(turnRepository).findFirstByGameSession_IdOrderByTurnNumDesc(sessionId);
		Mockito.doReturn(Optional.ofNullable(secondPlayer)).when(userGameSessionStartedRepository).findWithLockForUpdate(
				Mockito.argThat(
						new PlayerSpecificationMatcher(
								User_GameSessionStartedSpecification.builder()
										.orderNum(nextUserTurnNum).build()
						)
				),
				Mockito.any(Class.class)
		);

		boolean result = turnService.doTurnAndReturnIsGameSessionFinished(userId, sessionId, null);

		Mockito.verify(gameSessionService, Mockito.times(1)).finishGameSession(Mockito.any());
		Mockito.verify(turnRepository, Mockito.times(1)).save(Mockito.any());

		Assertions.assertTrue(result);
	}


	@ParameterizedTest
	@MethodSource("provideData_for_test_for_doTurnAndReturnIsGameSessionFinished_not_finished")
	public void test_for_doTurnAndReturnIsGameSessionFinished_not_finished(int userId, int sessionId, Integer targetUserId,
																				   GameSession gameSession,
																				   User_GameSessionStarted player,
																				   User_GameSessionStarted secondPlayer,
																				   Card_GameSessionStarted cardInSession,
																				   User_GameSessionStarted targetPlayer,
																				   int nextUserTurnNum) {
		Mockito.doReturn(Optional.ofNullable(gameSession))
				.when(gameSessionRepository).findWithLockForUpdate(Mockito.any(Specification.class), Mockito.any(Class.class));
		Mockito.doReturn(Optional.ofNullable(player)).when(userGameSessionStartedRepository).findWithLockForUpdate(
				Mockito.argThat(
						new PlayerSpecificationMatcher(
								User_GameSessionStartedSpecification.builder()
										.userId(userId).build()
						)),
				Mockito.any(Class.class)
		);
		Mockito.doReturn(Optional.ofNullable(player)).when(userGameSessionStartedRepository).findOne(
				Mockito.argThat(
						new PlayerSpecificationMatcher(
								User_GameSessionStartedSpecification.builder()
										.userGameSessionStarted(player).build()
						)
				)
		);
		Mockito.doReturn(Optional.ofNullable(cardInSession)).when(cardGameSessionStartedRepository)
				.findWithLockForUpdate(Mockito.any(Specification.class), Mockito.any(Class.class));
		if (targetUserId != null) {
			Mockito.doReturn(Optional.ofNullable(targetPlayer)).when(userGameSessionStartedRepository).findWithLockForUpdate(
					Mockito.argThat(
							new PlayerSpecificationMatcher(
									User_GameSessionStartedSpecification.builder()
											.userId(targetUserId).build()
							)),
					Mockito.any(Class.class)
			);
			Mockito.doReturn(Optional.ofNullable(targetPlayer)).when(userGameSessionStartedRepository).findOne(
					Mockito.argThat(
							new PlayerSpecificationMatcher(
									User_GameSessionStartedSpecification.builder()
											.userGameSessionStarted(targetPlayer).build()
							)
					)
			);
		}
		Mockito.doReturn(Optional.empty()).when(turnRepository).findFirstByGameSession_IdOrderByTurnNumDesc(sessionId);
		Mockito.doReturn(Optional.ofNullable(secondPlayer)).when(userGameSessionStartedRepository).findWithLockForUpdate(
				Mockito.argThat(
						new PlayerSpecificationMatcher(
								User_GameSessionStartedSpecification.builder()
										.orderNum(nextUserTurnNum).build()
						)
				),
				Mockito.any(Class.class)
		);

		boolean result = turnService.doTurnAndReturnIsGameSessionFinished(userId, sessionId, targetUserId);

		Mockito.verify(turnRepository, Mockito.times(1)).save(Mockito.any());
		int playerRepoCallTimes = 2 + (targetUserId == null ? 0 : 1);
		Mockito.verify(userGameSessionStartedRepository, Mockito.times(playerRepoCallTimes)).save(Mockito.any());
		Mockito.verify(cardGameSessionStartedRepository, Mockito.times(2)).save(Mockito.any());

		Assertions.assertFalse(result);
	}

	private static Stream<Arguments> provideData_for_test_for_doTurnAndReturnIsGameSessionFinished_not_finished() {
		return Stream.of(
				Arguments.of(FIRST_USER_ID, SESSION_ID, null,
						GAME_SESSION_IN_PROGRESS,
						FIRST_PLAYER,
						SECOND_PLAYER,
						POINT_CARD_IN_SESSION,
						null,
						NEXT_USER_TURN_NUM),
				Arguments.of(FIRST_USER_ID, SESSION_ID, null,
						GAME_SESSION_IN_PROGRESS,
						FIRST_PLAYER,
						SECOND_PLAYER,
						ACTION_BLOCK_CARD_IN_SESSION,
						null,
						(NEXT_USER_TURN_NUM + 1) % SESSION_CARDS_NUM
				),
				Arguments.of(FIRST_USER_ID, SESSION_ID, SECOND_USER_ID,
						GAME_SESSION_IN_PROGRESS,
						FIRST_PLAYER,
						SECOND_PLAYER,
						ACTION_STEAL_CARD_IN_SESSION,
						SECOND_PLAYER,
						NEXT_USER_TURN_NUM),
				Arguments.of(FIRST_USER_ID, SESSION_ID, null,
						GAME_SESSION_IN_PROGRESS,
						FIRST_PLAYER,
						SECOND_PLAYER,
						ACTION_DD_CARD_IN_SESSION,
						null,
						NEXT_USER_TURN_NUM)
		);
	}

	private static Stream<Arguments> provideData_for_test_for_doTurnAndReturnIsGameSessionFinished_session_finished() {
		return Stream.of(
				Arguments.of(FIRST_USER_ID, SESSION_ID,
						GAME_SESSION_IN_PROGRESS,
						FIRST_PLAYER,
						SECOND_PLAYER,
						BIG_POINT_CARD_IN_SESSION,
						NEXT_USER_TURN_NUM,
						null),
				Arguments.of(FIRST_USER_ID, SESSION_ID,
						GAME_SESSION_IN_PROGRESS,
						FIRST_PLAYER,
						SECOND_PLAYER,
						POINT_CARD_IN_SESSION,
						NEXT_USER_TURN_NUM,
						PRE_FINAL_TURN)
		);
	}


	private static class PlayerSpecificationMatcher implements ArgumentMatcher<User_GameSessionStartedSpecification> {
		private final User_GameSessionStartedSpecification left;

		public PlayerSpecificationMatcher(User_GameSessionStartedSpecification left) {
			this.left = left;
		}

		@Override
		public boolean matches(User_GameSessionStartedSpecification right) {
			return
					(
							right.getUserId() != null && left.getUserId() != null
									&& Objects.equals(left.getUserId(), right.getUserId())
					)
					||
					(
						left.getUserGameSessionStarted() != null
								&& left.getUserGameSessionStarted().getUser() != null
								&& right.getUserGameSessionStarted() != null
								&& right.getUserGameSessionStarted().getUser() != null
								&& Objects.equals(
										left.getUserGameSessionStarted().getUser().getId(),
								right.getUserGameSessionStarted().getUser().getId())
					)
					||
					(
							left.getOrderNum() != null && right.getOrderNum() != null
									&& left.getOrderNum().equals(right.getOrderNum())
					)
			;

		}
	}
}
