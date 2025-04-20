package com.example.CardGame.repos;

import com.example.CardGame.tables.Card;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CardRepository extends JpaRepository<Card, Integer> {
}
