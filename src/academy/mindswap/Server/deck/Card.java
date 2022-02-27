/*
 * @(#)Card.java        1.0 26/02/2022
 *
 * Copyright (c) MindSwap Academy - David Millasseau, Tiago Correia & João Gonçalves
 * All rights reserved.
 *
 * This software was produced to become our first group project.
 */


package academy.mindswap.Server.deck;

import java.util.Objects;

/**Card
 *
 *
 */

public class Card {

    private final CardRank cardRank;
    private final CardSuit cardSuit;


    public Card(CardRank cardRank,CardSuit cardSuit) {
        this.cardRank = cardRank;
        this.cardSuit = cardSuit;
    }

    public CardSuit getCardSuit() {
        return cardSuit;
    }

    public CardRank getCardRank() {
        return cardRank;
    }

    public static int whichCardIsHigher(Card card1 , Card card2) {
        return card1.getCardRank().getCardRankPoints() >= card2.getCardRank().getCardRankPoints() ? 1 : -1;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Card card)) return false;
        return getCardSuit() == card.getCardSuit() && getCardRank() == card.getCardRank();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getCardSuit(), getCardRank());
    }

    @Override
    public String toString() {

        return cardRank.getCardRankDescription() + " of " + cardSuit.getSuitDescription() + "(" +cardRank.getCardRankDigit() + cardSuit.getSuit() + ")" + "\n";
    }
}
