package academy.mindswap.Server.deck;

import academy.mindswap.utils.ColorCodes;

import java.awt.*;

public enum CardSuit {
    SPADES(ColorCodes.BLACK +  "♠" + ColorCodes.RESET, "Spades"),
    CLUBS(ColorCodes.GREEN + "♣" + ColorCodes.RESET, "Clubs"),
    HEARTS(ColorCodes.RED + "♥" + ColorCodes.RESET, "Hearts"),
    DIAMONDS(ColorCodes.BLUE + "♦" + ColorCodes.RESET, "Diamonds");

    private String suit;
    private String suitDescription;

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
