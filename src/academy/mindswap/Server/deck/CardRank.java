package academy.mindswap.Server.deck;

public enum CardRank {
    ACE(14, "Ace","A"),
    KING(13, "King","K"),
    QUEEN(12, "Queen","Q"),
    JACK(11, "Jack","J"),
    TEN(10, "Ten","T"),
    NINE(9, "Nine","9"),
    EIGHT(8, "Eight","8"),
    SEVEN(7, "Seven","7"),
    SIX(6, "Six","6"),
    FIVE(5, "Five","5"),
    FOUR(4, "Four","4"),
    THREE(3, "Three","3"),
    DEUCE(2, "Deuce","2");

    private int cardRankPoints;
    private String cardRankDescription;
    private String cardRankDigit;


    CardRank(int cardRankPoints, String cardRankDescription, String cardRankDigit) {
        this.cardRankPoints = cardRankPoints;
        this.cardRankDescription = cardRankDescription;
        this.cardRankDigit = cardRankDigit;
    }

    public int getCardRankPoints() {
        return cardRankPoints;
    }

    public String getCardRankDescription() {
        return cardRankDescription;
    }

    public String getCardRankDigit() {
        return cardRankDigit;
    }
}
