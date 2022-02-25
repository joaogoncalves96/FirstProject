package academy.mindswap.Server.deck;

import academy.mindswap.utils.ColorCodes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class HandTester {

    public static void main(String[] args) {

        ArrayList<Card> playerHand = new ArrayList<>(2);
        Set<Card> tableCards = new HashSet<>(2);

//        10 Q J K 7

//        9 2

        playerHand.add(new Card(CardRank.NINE, CardSuit.HEARTS));
        playerHand.add(new Card(CardRank.DEUCE, CardSuit.SPADES));

        tableCards.add(new Card(CardRank.TEN, CardSuit.CLUBS));
        tableCards.add(new Card(CardRank.QUEEN, CardSuit.HEARTS));
        tableCards.add(new Card(CardRank.JACK, CardSuit.DIAMONDS));
        tableCards.add(new Card(CardRank.SEVEN, CardSuit.HEARTS));
        tableCards.add(new Card(CardRank.KING, CardSuit.CLUBS));
        System.out.println();
        System.out.println(printCards(playerHand));
        System.out.println();
        System.out.println(printCards(tableCards));
        int points = HandAnalyzer.analyzeHand(playerHand, tableCards);

        ArrayList<Card> result = HandAnalyzer.makeFinalHand(points, playerHand, tableCards);
        System.out.println("Points: " + points);
        System.out.println();
        System.out.println(printCards(result));

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
