package academy.mindswap.Server.deck;

public enum CardRank {
    ACE(14),
    KING(13),
    QUEEN(12),
    JACK(11),
    TEN(10),
    NINE(9),
    EIGHT(8),
    SEVEN(7),
    SIX(6),
    FIVE(5),
    FOUR(4),
    THREE(3),
    DEUCE(2);

    private int cardRankPoints;

    CardRank(int cardRankPoints) {
        this.cardRankPoints = cardRankPoints;
    }

    public int getCardRankPoints() {
        return cardRankPoints;
    }
}
