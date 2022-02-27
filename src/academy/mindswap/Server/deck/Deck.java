/*
 * @(#)Deck.java        1.0 26/02/2022
 *
 * Copyright (c) MindSwap Academy - David Millasseau, Tiago Correia & João Gonçalves
 * All rights reserved.
 *
 * This software was produced to become our first group project.
 */


package academy.mindswap.Server.deck;
import java.util.Collections;
import java.util.Set;

/**
 * Deck class, contains a deck of 52 cards
 */

public class Deck {

    private final int DECK_MAX_SIZE = 52;
    private final Set<Card> deck;
    private final CardRank[] cardRank = CardRank.values();
    private final CardSuit[] cardSuit = CardSuit.values();

    public Deck(Set<Card> deck) {
        this.deck = Collections.synchronizedSet(deck);
    }

    public Set<Card> getDeck() {
        return deck;
    }

    public int getDeckSize() {
        return this.deck.size();
    }

    public void removeCard(Card card) {
        this.deck.remove(card);
    }
}
