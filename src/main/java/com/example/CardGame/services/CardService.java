package com.example.CardGame.services;

import com.example.CardGame.repos.CardRepository;
import com.example.CardGame.tables.Card;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CardService {
    private final CardRepository cardRepository;

    public List<Card> getDeckForGameSessionStart() {
        List<Card> deck = cardRepository.findAll();
        Collections.shuffle(deck);
        return deck;
    }
}
