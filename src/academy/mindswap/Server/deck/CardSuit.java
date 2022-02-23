package academy.mindswap.Server.deck;

import academy.mindswap.utils.ColorCodes;

public enum CardSuit {
    SPADES(ColorCodes.BLACK_BRIGHT +  "♠" + ColorCodes.RESET, "Spades"),
    CLUBS(ColorCodes.BLACK_BRIGHT + "♣" + ColorCodes.RESET, "Clubs"),
    HEARTS(ColorCodes.RED_BRIGHT + "♥" + ColorCodes.RESET, "Hearts"),
    DIAMONDS(ColorCodes.RED_BRIGHT +  "♦" + ColorCodes.RESET, "Diamonds");

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
