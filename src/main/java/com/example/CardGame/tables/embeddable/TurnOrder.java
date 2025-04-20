package com.example.CardGame.tables.embeddable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@NoArgsConstructor
@Setter
@Getter
@Embeddable
public class TurnOrder implements Serializable {
    @Column(name = "order_num", nullable = false)
    private int orderNum;

    @Column(name = "is_current")
    private boolean isCurrent;

    public TurnOrder(int orderNum, boolean isCurrent) {
        this.orderNum = orderNum;
        this.isCurrent = isCurrent;
    }
}
