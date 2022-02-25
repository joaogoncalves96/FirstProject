package academy.mindswap.Server.deck;

import academy.mindswap.utils.ColorCodes;

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
