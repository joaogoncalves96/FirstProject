package academy.mindswap.Server.deck;

import java.util.HashSet;
import java.util.Set;

public class DeckFactory {

    public static Deck createFullDeck() {

       CardRank[] cardRank = CardRank.values();
       CardSuit[] cardSuit = CardSuit.values();

       Set<Card> deck = new HashSet<>(52);

         for (CardRank rank : cardRank) {
         for (CardSuit suit : cardSuit) {
             deck.add(new Card(rank, suit));
         }}

        return new Deck(deck);

    }
}
