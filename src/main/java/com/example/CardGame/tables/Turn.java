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

    @JoinColumn(name = "current_user_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @JoinColumn(name = "target_user_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private User target;

    @JoinColumn(name = "game_session_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private GameSession gameSession;

    @JoinColumn(name = "card_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Card card;

    @Column(name = "\"order\"", nullable = false)
    private int order;

    @Column(name = "gained_points", nullable = false)
    private int gainedPoints;
}
