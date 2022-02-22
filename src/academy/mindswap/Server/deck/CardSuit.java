package academy.mindswap.Server.deck;

public enum CardSuit {
    SPADES("♠"),
    CLUBS("♣"),
    HEARTS("♥"),
    DIAMONDS("♦");

    private String suit;

    CardSuit(String s) {
        this.suit = s;
    }

    public String getSuit() {
        return this.suit;
    }
}
