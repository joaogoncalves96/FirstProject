package academy.mindswap.Server.deck;


import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Deck {

    private final int DECK_MAX_SIZE = 52;
    private Set<Card> deck;
    private CardRank[] cardRank = CardRank.values();
    private CardSuit[] cardSuit = CardSuit.values();


//    public Deck() {
//        this.deck = new HashSet<>(DECK_MAX_SIZE);
//
//        for (int i = 0; i < cardRank.length; i++) {
//            for (int j = 0; j < cardSuit.length; j++) {
//                deck.add(new Card(cardRank[i],cardSuit[j]));
//            }
//        }
//    }

    public Deck(Set<Card> deck) {
        this.deck = Collections.synchronizedSet(deck);
    }

    public Set<Card> getDeck() {
        return deck;
    }

    public int getDeckSize() {
        return this.deck.size();
    }

}
