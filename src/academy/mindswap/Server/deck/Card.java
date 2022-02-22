package academy.mindswap.Server.deck;

import java.util.Objects;

public class Card {

    private CardRank cardRank;
    private CardSuit cardSuit;


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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Card)) return false;
        Card card = (Card) o;
        return getCardSuit() == card.getCardSuit() && getCardRank() == card.getCardRank();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getCardSuit(), getCardRank());
    }

    @Override
    public String toString() {
        return cardRank.getCardRankDescription() + " of " + cardSuit.getSuitDescription() + "(" + cardSuit.getSuit() + ")" + "\n";
    }
}
