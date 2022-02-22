package academy.mindswap.Server.deck;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Deck class, contains a deck of 52 cards
 */

public class Deck {

    private final int DECK_MAX_SIZE = 52;
    private Set<Card> deck;
    private CardRank[] cardRank = CardRank.values();
    private CardSuit[] cardSuit = CardSuit.values();

    public Deck(Set<Card> deck) {
        this.deck = Collections.synchronizedSet(deck);
    }

    public Set<Card> getDeck() {
        return deck;
    }

    public int getDeckSize() {
        return this.deck.size();
    }

    public void removeCard(Card card) {
        this.deck.remove(card);
    }

}
