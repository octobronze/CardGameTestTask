package com.example.CardGame.tables;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "card", uniqueConstraints = {
        @UniqueConstraint(columnNames = "name")
})
@Getter
@Setter
@NoArgsConstructor
public class Card {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "type", nullable = false)
    @Enumerated(value = EnumType.ORDINAL)
    private Card.TypeEnum type;

    @Column(name = "action_card_type")
    @Enumerated(value = EnumType.ORDINAL)
    private Card.ActionCardTypeEnum actionCardType;

    @Column(name = "value", nullable = false)
    private int value;

    @Column(name = "name")
    private String name;


    public enum TypeEnum {
        POINTS_CARD("Points card"), ACTION_CARD("Action card");

        private final String name;

        TypeEnum(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }
    public enum ActionCardTypeEnum {
        BLOCK("Block"), STEAL("Steal"), DOUBLE_DOWN("DoubleDown");

        private final String name;

        ActionCardTypeEnum(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    public static class Consts {
        public static final int BLOCK_VALUE = 1;
        public static final int DD_VALUE = 2;
    }
    public static class ExceptionMessages {
        public static final String INVALID_CARD_FOR_TARGET = "Target cannot be chosen with this card";
        public static final String TARGET_NOT_CHOSEN = "Target should be chosen for this card";
    }
}
