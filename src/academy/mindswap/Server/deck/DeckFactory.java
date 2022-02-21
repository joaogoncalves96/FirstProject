package academy.mindswap.Server.deck;

import java.util.HashSet;
import java.util.Set;

public class DeckFactory {


   static Deck createFullDeck() {
       CardRank[] cardRank = CardRank.values();
       CardSuit[] cardSuit = CardSuit.values();

       Set<Card> deck = new HashSet<>(52);

       for (int i = 0; i < cardRank.length; i++) {
           for (int j = 0; j < cardSuit.length; j++) {
               deck.add(new Card(cardRank[i],cardSuit[j]));
           }
       }

        return new Deck();
    }
}
