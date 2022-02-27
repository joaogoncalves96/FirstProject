/*
 * @(#)CardSuit.java        1.0 26/02/2022
 *
 * Copyright (c) MindSwap Academy - David Millasseau, Tiago Correia & João Gonçalves
 * All rights reserved.
 *
 * This software was produced to become our first group project.
 */


package academy.mindswap.Server.deck;

import academy.mindswap.utils.ColorCodes;

/**
 * This is a property of class card
 */

public enum CardSuit {
    SPADES( "♠", "Spades"),
    CLUBS( "♣", "Clubs"),
    HEARTS( "♥", "Hearts"),
    DIAMONDS(  "♦", "Diamonds");

    private final String suit;
    private final String suitDescription;

    CardSuit(String s, String suitDescription) {
        this.suit = s;
        this.suitDescription = suitDescription;
    }

    public String getSuit() {
        return this.suit;
    }

    public String getSuitDescription() {
        return suitDescription;
    }
}
