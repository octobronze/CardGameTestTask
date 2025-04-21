package com.example.CardGame.tables;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;

import java.util.List;

@Entity
@Table(name = "game_session")
@Getter
@Setter
public class GameSession {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "users_num", nullable = false)
    @Range(max = Consts.MAX_PLAYERS_PER_SESSION)
    private int usersNum = 0;

    @Column(name = "state", nullable = false)
    @Enumerated(value = EnumType.ORDINAL)
    private GameSession.StateEnum state;

    @JoinColumn(name = "created_by", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private User createdBy;

    @Column(name = "cards_num")
    private Integer cardsNum;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "gameSession")
    private List<User_GameSession> gameSession_users;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "gameSession")
    private List<User_GameSessionStarted> gameSessionStarted_Users;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "gameSession")
    private List<Card_GameSessionStarted> gameSessionStarted_Cards;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "gameSession")
    private List<Turn> turns;

    public void incUsersNum() {
        usersNum ++;
    }

    public void decUsersNum() {
        usersNum --;
    }

    public static class Consts {
        public static final int MAX_PLAYERS_PER_SESSION = 4;
        public static final int MIN_PLAYERS_TO_START = 2;
        public static final int MAX_POINTS = 30;
    }
    public static class ExceptionMessages {
        public static final String NOT_ENOUGH_PLAYERS = "Not enough players to start game session";
        public static final String IS_FULL = "Game session is already full";
        public static final String NOT_FOUND = "Game session not found";
        public static final String HAS_BEEN_STARTED = "Game session already has been started";
        public static final String CAN_BE_STARTED_ONLY_BY_CREATOR = "Game session can be started only by a creator";
        public static final String ALREADY_FINISHED = "Game session is already finished";
        public static final String ALREADY_STARTED = "Game session is already started";
    }

    public enum StateEnum {
        WAIT_FOR_PLAYERS, IN_PROGRESS, FINISHED
    }
}
