package academy.mindswap.server.deck;

public enum CardRank {
    ACE(14, "Ace"),
    KING(13, "King"),
    QUEEN(12, "Queen"),
    JACK(11, "Jack"),
    TEN(10, "Ten"),
    NINE(9, "Nine"),
    EIGHT(8, "Eight"),
    SEVEN(7, "Seven"),
    SIX(6, "Six"),
    FIVE(5, "Five"),
    FOUR(4, "Four"),
    THREE(3, "Three"),
    DEUCE(2, "Deuce");

    private int cardRankPoints;
    private String cardRankDescription;


    CardRank(int cardRankPoints, String cardRankDescription) {
        this.cardRankPoints = cardRankPoints;
        this.cardRankDescription = cardRankDescription;
    }

    public int getCardRankPoints() {
        return cardRankPoints;
    }

    public String getCardRankDescription() {
        return cardRankDescription;
    }
}
