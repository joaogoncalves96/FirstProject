/*
 * @(#)DeckFactory.java        1.0 26/02/2022
 *
 * Copyright (c) MindSwap Academy - David Millasseau, Tiago Correia & João Gonçalves
 * All rights reserved.
 *
 * This software was produced to become our first group project.
 */

package academy.mindswap.Server.deck;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class DeckFactory {

    public static Deck createFullDeck() {

       CardRank[] cardRank = CardRank.values();
       CardSuit[] cardSuit = CardSuit.values();

       Set<Card> deck = Collections.synchronizedSet(new HashSet<>(52));

         for (CardRank rank : cardRank) {
         for (CardSuit suit : cardSuit) {
             deck.add(new Card(rank, suit));
         }}

        return new Deck(deck);

    }
}
