/*
 * @(#)HandAnalyzer.java        1.0 26/02/2022
 *
 * Copyright (c) MindSwap Academy - David Millasseau, Tiago Correia & João Gonçalves
 * All rights reserved.
 *
 * This software was produced to become our first group project.
 */


package academy.mindswap.Server.deck;
import academy.mindswap.utils.ColorCodes;
import java.util.*;

/** Hand Analyzer Class
 * This class takes in lists of Card objects and returns either your best hand or the points of your best hand.
 * Use either analyzeHand to get the points of your hand, or makeFinalHand to get the list of cards with your best hand
 *
 *
 */

public class HandAnalyzer {


    /**This method takes in players cards and the table cards and returns a numerical value associated with how good
     * the hand is
     *
     * @param playerHand A list of cards provided by the player
     * @param tableCards A set of cards in the table currently
     * @return Returns the total points the player has with his hand + table's cards.
     */


    public static int analyzeHand(ArrayList<Card> playerHand, Collection<Card> tableCards) {

        int points = 0;

        int highestCard = Math.max(playerHand.get(0)
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

            points += 1500 + cardValue;
            return points;
        }

        if(hasFlush(suitCount)) {
            ArrayList<Card> hand = getFlushHand(suitCount, fullHand);

            points += 1000 + highestCard;
            return points;

        }

        if(hasStraight(fullHand)) {
            ArrayList<Card> hand = getStraightHand(fullHand);
            points += 750 + highestCard;
            return points;
        }

        if(hasTriple(rankCount)) {
            ArrayList<Card> hand = getTripleHand(rankCount, fullHand);
            int cardValue = rankCount
                    .keySet()
                    .stream()
                    .filter(value -> rankCount.get(value) == 3)
                    .findFirst()
                    .get()
                    .getCardRankPoints();

            points += 500 + cardValue + highestCard;
            return points;
        }

        if(hasDoublePair(rankCount)) {
            ArrayList<Card> hand = getPairsHand(rankCount, fullHand);
            int cardValue = rankCount
                    .keySet()
                    .stream()
                    .filter(value -> rankCount.get(value) == 2)
                    .map(CardRank::getCardRankPoints)
                    .reduce(0, Math::max);

            points += 300 + cardValue + highestCard;
            return points;
        }

        if(hasPair(rankCount)) {
            ArrayList<Card> hand = getPairsHand(rankCount, fullHand);
            int cardValue = rankCount
                    .keySet()
                    .stream()
                    .filter(value -> rankCount.get(value) == 2)
                    .findFirst()
                    .get()
                    .getCardRankPoints();

            points += 150 + cardValue + highestCard;
            return points;

        }
        return highestCard;
    }

    /**
     * This method makes an array list of cards based on the best hand possible that the player has with the table
     * cards
     * @param points The points that the player hand has
     * @param playerHand List of player hand cards
     * @param tableCards List of table cards
     * @return Returns the player's best hand
     */

    public static ArrayList<Card> makeFinalHand(int points, ArrayList<Card> playerHand, Set<Card> tableCards) {

        ArrayList<Card> fullHand = new ArrayList<>(7);

        fullHand.addAll(playerHand);
        fullHand.addAll(tableCards);

        HashMap<CardRank, Integer> rankCount = rankCounter(fullHand);
        HashMap<CardSuit, Integer> suitCount = suitCounter(fullHand);

        if(points > 2000) {
            return get4ofAKindHand(rankCount);
        }
        if(points > 1500) {
            return getFullHouseHand(rankCount, fullHand);
        }
        if(points > 1000) {
            return getFlushHand(suitCount, fullHand);
        }
        if(points > 750) {
            return getStraightHand(fullHand);
        }
        if(points > 500) {
            return getTripleHand(rankCount, fullHand);
        }
        if(points > 150) {
            return getPairsHand(rankCount, fullHand);
        }

        int result = Card.whichCardIsHigher(playerHand.get(0), playerHand.get(1));

        if(result == 1) {
            fullHand = new ArrayList<>(1);
            fullHand.add(playerHand.get(0));
            return fullHand;
        }
        fullHand = new ArrayList<>(1);
        fullHand.add(playerHand.get(1));

        return fullHand;
    }

    private static boolean hasFourOfKind(HashMap<CardRank, Integer> rankCount) {
        return rankCount.containsValue(4);
    }

    private static ArrayList<Card> getStraightHand(ArrayList<Card> hand) {

        ArrayList<Card> finalHand = new ArrayList<>();
        hand.sort(new Comparator<Card>() {
            @Override
            public int compare(Card o1, Card o2) {
                return Integer.compare(o1.getCardRank().getCardRankPoints(),o2.getCardRank().getCardRankPoints());
            }

        });

        printCards(finalHand);

        for (int i = 0; i < hand.size() - 1; i++) {

            int cardValue1 = hand.get(i).getCardRank().getCardRankPoints();
            int cardValue2 = hand.get(i + 1).getCardRank().getCardRankPoints();
            if(finalHand.size() >= 5 && cardValue1 != cardValue2 + 1) break;
            if(cardValue1 == cardValue2 - 1) {
                finalHand.add(hand.get(i));
                if(finalHand.size() == 4 && i == hand.size() - 2) {
                    finalHand.add(hand.get(i + 1));
                    break;
                }
                continue;
            }
            finalHand = new ArrayList<>(5);
        }
        return finalHand;
    }

    private static ArrayList<Card> getFlushHand(HashMap<CardSuit, Integer> suitCount, ArrayList<Card> hand) {

        CardSuit suit = suitCount.keySet().stream()
                    .filter(cardSuit -> suitCount.get(cardSuit) >= 5)
                    .findFirst()
                    .get();

        ArrayList<Card> finalHand = new ArrayList<>();

        hand.stream()
                .filter(card -> card.getCardSuit().equals(suit))
                .forEach(finalHand::add);

        return finalHand;
    }

    private static ArrayList<Card> getPairsHand(HashMap<CardRank, Integer> rankCount, ArrayList<Card> hand) {
        ArrayList<Card> finalHand = new ArrayList<>();

        hand.stream().filter(card -> rankCount.get(card.getCardRank()) == 2).forEach(finalHand::add);

        return finalHand;
    }

    private static ArrayList<Card> getTripleHand(HashMap<CardRank, Integer> rankCount, ArrayList<Card> hand) {
        ArrayList<Card> finalHand = new ArrayList<>(3);
        hand.stream().filter(card -> rankCount.get(card.getCardRank()) == 3).forEach(finalHand::add);
        return finalHand;
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

    private static boolean hasTriple(HashMap<CardRank, Integer> rankCount) {
        return rankCount.containsValue(3) && !rankCount.containsValue(2);
    }

    private static boolean hasDoublePair(HashMap<CardRank, Integer> rankCount) {
        return rankCount.values().stream().filter(v -> v == 2).count() >= 2;
    }

    private static boolean hasPair(HashMap<CardRank, Integer> rankCount) {
        return rankCount.values().stream().filter(v -> v == 2).count() == 1;
    }

    private static boolean hasFlush(HashMap<CardSuit, Integer> suitCount) {
        return suitCount.values().stream().anyMatch(v -> v >= 5);
    }

    private static boolean hasStraight(ArrayList<Card> hand) {
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

            if(card1Value == card2Value - 1) {
                sequentialCounter++;
            } else {
                sequentialCounter = 0;
            }
        }
        return sequentialCounter >= 4;
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

    public static String printCards(Collection<Card> cardList) {
        StringBuilder cardString = new StringBuilder();

        String whiteBG = ColorCodes.WHITE_BACKGROUND_BRIGHT;
        String black = ColorCodes.BLACK_BOLD;
        String red = ColorCodes.RED_BOLD_BRIGHT;
        String reset = ColorCodes.RESET;



        for(Card card : cardList) {

            int isTen = card.getCardRank().equals(CardRank.TEN) ? 1 : 0;
            String color;
            cardString.append(whiteBG);

            if(card.getCardSuit().equals(CardSuit.SPADES) || card.getCardSuit().equals(CardSuit.CLUBS)) {
                color = black;
            } else {
                color = red;
            }

            cardString.append(color);
            cardString.append(card.getCardRank().getCardRankDigit());
            cardString.append(whiteBG);
            cardString.append(" ".repeat(3 - isTen));
            cardString.append(whiteBG);
            cardString.append(color);
            cardString.append(card.getCardSuit().getSuit());
            cardString.append(reset);
            cardString.append(" ".repeat(3));

        }

        cardString.append("\n");

        for(Card card : cardList) {

            String color;
            cardString.append(whiteBG);

            if(card.getCardSuit().equals(CardSuit.SPADES) || card.getCardSuit().equals(CardSuit.CLUBS)) {
                color = black;
            } else {
                color = red;
            }

            cardString.append(whiteBG);
            cardString.append("  ");
            cardString.append(whiteBG);
            cardString.append(color);
            cardString.append(card.getCardSuit().getSuit());
            cardString.append(whiteBG);
            cardString.append("  ");
            cardString.append(reset);
            cardString.append(" ".repeat(3));

        }

        cardString.append("\n");

        for(Card card : cardList) {
            String color;
            int isTen = card.getCardRank().equals(CardRank.TEN) ? 1 : 0;
            cardString.append(whiteBG);

            if(card.getCardSuit().equals(CardSuit.SPADES) || card.getCardSuit().equals(CardSuit.CLUBS)) {
                color = black;
            } else {
                color = red;
            }
            cardString.append(whiteBG);
            cardString.append(color);
            cardString.append(card.getCardSuit().getSuit());
            cardString.append(whiteBG);
            cardString.append(" ".repeat(3 - isTen));
            cardString.append(whiteBG);
            cardString.append(color);
            cardString.append(card.getCardRank().getCardRankDigit());
            cardString.append(reset);
            cardString.append(" ".repeat(3));

        }

        cardString.append("\n");

        return cardString.toString();
    }

}
