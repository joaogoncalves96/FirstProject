package academy.mindswap.Server.deck;

import java.util.*;

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

        HashMap<CardRank, Integer> rankCount = rankCounter(fullHand);
        HashMap<CardSuit, Integer> suitCount = suitCounter(fullHand);

        if(hasFourOfKind(rankCount)) {
            ArrayList<Card> hand = get4ofAKindHand(rankCount);
            points += 2000;
            points += hand.get(0).getCardRank().getCardRankPoints();
            return points;
        }

        if(hasFullHouse(rankCount)) {
            ArrayList<Card> hand = getFullHouseHand(rankCount, fullHand);

            int cardValue = rankCount
                    .keySet()
                    .stream()
                    .filter(value -> rankCount.get(value) == 3)
                    .findFirst()
                    .get()
                    .getCardRankPoints();

            points += 1000 + cardValue;
            return points;
        }





        return 0;
    }

    private static boolean hasFourOfKind(HashMap<CardRank, Integer> rankCount) {
        return rankCount.containsValue(4);
    }

    private static ArrayList<Card> get4ofAKindHand(HashMap<CardRank, Integer> rankCount) {
        ArrayList<Card> hand = new ArrayList<>(4);
        CardRank cardRank = null;

        for(CardRank cr : rankCount.keySet()) {
            if(rankCount.get(cr) == 4) {
                cardRank = cr;
                break;
            }
        }

        for(CardSuit cs : CardSuit.values()) {
            hand.add(new Card(cardRank, cs));
        }
        return hand;
    }

    private static ArrayList<Card> getFullHouseHand(HashMap<CardRank, Integer> rankCount, ArrayList<Card> hand) {

        CardRank cardRank1 = null;
        CardRank cardRank2 = null;

        for(CardRank cr : rankCount.keySet()) {
            if(rankCount.get(cr) == 3) {
                cardRank1 = cr;
                continue;
            }
            if(rankCount.get(cr) == 2) {
                cardRank2 = cr;
            }
            if(cardRank1 != null && cardRank2 != null) break;
        }

        ArrayList<Card> finalHand = new ArrayList<>(5);
//        hand.stream()
//                .filter(card -> card.getCardRank().equals(cardRank1) || card.getCardRank().equals(cardRank2))
//                .forEach(finalHand::add);

        for(Card card : hand) {
            if(card.getCardRank().equals(cardRank1)) {
                finalHand.add(card);
                continue;
            }
            if(card.getCardRank().equals(cardRank2)) {
                finalHand.add(card);
            }
        }

        return finalHand;
    }



    private static boolean hasFullHouse(HashMap<CardRank, Integer> rankCount) {
        return rankCount.containsValue(3) && rankCount.containsValue(2);
    }

    private boolean hasTriple(HashMap<CardRank, Integer> rankCount) {
        return rankCount.containsValue(3) && !rankCount.containsValue(2);
    }

    private boolean hasDoublePair(HashMap<CardRank, Integer> rankCount) {
        return rankCount.values().stream().filter(v -> v == 2).count() >= 2;
    }

    private boolean hasPair(HashMap<CardRank, Integer> rankCount) {
        return rankCount.values().stream().filter(v -> v == 2).count() == 1;
    }

    private boolean hasFlush(HashMap<CardSuit, Integer> suitCount) {
        return suitCount.values().stream().anyMatch(v -> v >= 5);
    }

    private boolean hasStraight(ArrayList<Card> hand) {

        hand.sort(new Comparator<Card>() {
            @Override
            public int compare(Card o1, Card o2) {
                return Integer.compare(o1.getCardRank().getCardRankPoints(), o2.getCardRank().getCardRankPoints()) ;
            }
        });

        int sequentialCounter = 0;
        for (int i = 0; i < hand.size() - 1; i++) {
            int card1Value = hand.get(i).getCardRank().getCardRankPoints();
            int card2Value = hand.get(i + 1).getCardRank().getCardRankPoints();
            if(card1Value == card2Value + 1) {
                sequentialCounter++;
            } else {
                sequentialCounter = 0;
            }
        }
        return sequentialCounter >= 5;
    }

    private static HashMap<CardRank, Integer> rankCounter(ArrayList<Card> hand) {

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

    private static HashMap<CardSuit, Integer> suitCounter(ArrayList<Card> hand) {

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