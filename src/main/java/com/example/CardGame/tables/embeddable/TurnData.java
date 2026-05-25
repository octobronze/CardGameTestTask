package com.example.CardGame.tables.embeddable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Getter
@Embeddable
public class TurnData {
    @Column(name = "\"order\"")
    private Integer order;

    @Column(name = "is_current")
    private Boolean isCurrent;

    public TurnData(Integer order, boolean isCurrent) {
        this.order = order;
        this.isCurrent = isCurrent;
    }
}
