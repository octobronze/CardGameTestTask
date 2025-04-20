package com.example.CardGame.tables;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Table(name = "turn")
@Entity
@Getter
@Setter
@NoArgsConstructor
public class Turn {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @JoinColumn(name = "user_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @JoinColumn(name = "target_user_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private User targetUser;

    @JoinColumn(name = "game_session_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private GameSession gameSession;

    @JoinColumn(name = "card_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Card card;

    @Column(name = "turn_num", nullable = false)
    private int turnNum;

    @Column(name = "points_difference", nullable = false)
    private int pointsDifference;
}
