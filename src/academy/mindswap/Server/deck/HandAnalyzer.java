package academy.mindswap.Server.deck;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class HandAnalyzer {


    public static int AnalyzeHand(ArrayList<Card> playerHand, Collection<Card> tableCards) {
        int points = 0;

        int highestCard = Math.max(playerHand.get(0)
                            .getCardRank()
                            .getCardRankPoints(), playerHand.get(1)
                            .getCardRank()
                            .getCardRankPoints());

        int lowestCard = Math.min(playerHand.get(0)
                            .getCardRank()
                            .getCardRankPoints(), playerHand.get(1)
                            .getCardRank()
                            .getCardRankPoints());

        ArrayList<Card> fullHand = new ArrayList<>(7);

        fullHand.addAll(playerHand);
        fullHand.addAll(tableCards);







        return 0;
    }

    private boolean hasFourOfKind(HashMap<CardRank, Integer> rankCount) {
        return rankCount.containsValue(4);
    }

    private boolean hasFullHouse(HashMap<CardRank, Integer> rankCount) {
        return rankCount.containsValue(3) && rankCount.containsValue(2);
    }

    private boolean hasTriple(HashMap<CardRank, Integer> rankCount) {
        return rankCount.containsValue(3) && !rankCount.containsValue(2);
    }

    private boolean hasDoublePair(HashMap<CardRank, Integer> rankCount) {
        return rankCount.containsValue(3) && !rankCount.containsValue(2);
    }

    private HashMap<CardRank, Integer> rankCounter(ArrayList<Card> hand) {

        HashMap<CardRank, Integer> cardsCount = new HashMap<>();

        List<CardRank> cardRanks = hand.stream()
                .map(Card::getCardRank).toList();

        for(CardRank cardRank : cardRanks) {
            if(cardsCount.containsKey(cardRank)) {
                cardsCount.put(cardRank, cardsCount.get(cardRank) + 1);
            } else {
                cardsCount.put(cardRank, 1);
            }
        }
        return cardsCount;
    }

    private HashMap<CardSuit, Integer> suitCounter(ArrayList<Card> hand) {

        HashMap<CardSuit, Integer> cardSuitCount = new HashMap<>();

        List<CardSuit> cardSuits = hand.stream()
                .map(Card::getCardSuit).toList();

        for(CardSuit cardSuit : cardSuits) {
            if(cardSuitCount.containsKey(cardSuit)) {
                cardSuitCount.put(cardSuit, cardSuitCount.get(cardSuit) + 1);
            } else {
                cardSuitCount.put(cardSuit, 1);
            }
        }
        return cardSuitCount;
    }


}
