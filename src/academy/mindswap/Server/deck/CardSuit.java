package academy.mindswap.Server.deck;

import academy.mindswap.utils.ColorCodes;

public enum CardSuit {
    SPADES(ColorCodes.BLACK +  "♠" + ColorCodes.RESET),
    CLUBS("♣"),
    HEARTS(ColorCodes.RED + "♥" + ColorCodes.RESET),
    DIAMONDS("♦");

    private String suit;

    CardSuit(String s) {
        this.suit = s;
    }

    public String getSuit() {
        return this.suit;
    }
}
