/*
 * @(#)HandTester.java        1.0 26/02/2022
 *
 * Copyright (c) MindSwap Academy - David Millasseau, Tiago Correia & João Gonçalves
 * All rights reserved.
 *
 * This software was produced to become our first group project.
 */


package academy.mindswap.Server.deck;

import academy.mindswap.utils.ColorCodes;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ExecutorService;

public class HandTester {


    /**
     * This class was made for testing hands manually
     */

    public static void main(String[] args){

//        ArrayList<Card> playerHand = new ArrayList<>(2);
////        Set<Card> tableCards = new HashSet<>(2);
////
//////        10 Q J K 7
////
//////        9 2
////
////        playerHand.add(new Card(CardRank.NINE, CardSuit.HEARTS));
////        playerHand.add(new Card(CardRank.DEUCE, CardSuit.SPADES));
////
////        tableCards.add(new Card(CardRank.TEN, CardSuit.CLUBS));
////        tableCards.add(new Card(CardRank.QUEEN, CardSuit.HEARTS));
////        tableCards.add(new Card(CardRank.JACK, CardSuit.DIAMONDS));
////        tableCards.add(new Card(CardRank.SEVEN, CardSuit.HEARTS));
////        tableCards.add(new Card(CardRank.KING, CardSuit.CLUBS));
////        System.out.println();
////        System.out.println(printCards(playerHand));
////        System.out.println();
////        System.out.println(printCards(tableCards));
////        int points = HandAnalyzer.analyzeHand(playerHand, tableCards);
////
////        ArrayList<Card> result = HandAnalyzer.makeFinalHand(points, playerHand, tableCards);
////        System.out.println("Points: " + points);
////        System.out.println();
////        System.out.println(printCards(result));
//
//
////        File file = new File("resources/intro");
////        StringBuilder sb = new StringBuilder();
////        try {
////            Scanner scanner = new Scanner(file);
////            while(scanner.hasNext()){
////                sb
//////                        .append(ColorCodes.BLACK_BACKGROUND_BRIGHT)
//////                        .append(ColorCodes.WHITE_BOLD_BRIGHT)
////                        .append(scanner.nextLine())
////                        .append("\n");
//////                        .append(ColorCodes.RESET);
////
////                try {
////                    Thread.sleep(10);
////                } catch (InterruptedException e) {
////                    e.printStackTrace();
////                }
////            }
////        } catch (FileNotFoundException e) {
////            e.printStackTrace();
////        }
////
////        String BLACK = "\033[1;90m"; // BLACK
////        String RED = "\033[1;91m";   // RED
////        String GREEN = "\033[1;92m"; // GREEN
////        String YELLOW = "\033[1;93m";// YELLOW
////        String BLUE = "\033[1;94m";  // BLUE
////        String PURPLE = "\033[1;95m";// PURPLE
////        String CYAN = "\033[1;96m";  // CYAN
////        String WHITE = "\033[1;97m"; // WHITE
////
////        String[] colorsArray = {BLACK, RED, GREEN, WHITE};
////
//////        System.out.println(sb.toString());
////
////        new Thread();
////
////        String[] introLines = sb.toString().split("\n");
////        int counter = 0;
////        long animationSpeed = 1;
////        while(true) {
////            for(String s : introLines) {
////                for (String s1 : s.split("")) {
////                    System.out.print(colorsArray[(int) (Math.random() * colorsArray.length)] + s1 + ColorCodes.RESET);
////                    Thread.sleep(animationSpeed);
////                }
////                System.out.println();
////            }
////        }
//
//        List<String> divide = Files.readAllLines(Paths.get("./resources/intro"));
//
//        String intro = String.join("\n", divide);
//
//        String intro1 = ColorCodes.RED_BOLD_BRIGHT + intro + ColorCodes.RESET;
//        String red = ColorCodes.RED_BOLD_BRIGHT;
//        String blue = ColorCodes.BLUE_BOLD_BRIGHT;
//        String reset = ColorCodes.RESET;
//
//        for (char c : intro.toCharArray()) {
//            System.out.print(c);
//            Thread.sleep(5);
//        }
//
//
//
//        }
//
//    public static void printLine(String line) {
//        String red = ColorCodes.RED_BOLD_BRIGHT;
//        String blue = ColorCodes.BLUE_BOLD_BRIGHT;
//        String reset = ColorCodes.RESET;
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                while (true) {
//                    String s = red + line + reset;
//                    System.out.print(s);
//
//                    try {
//                        Thread.sleep(250);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                    System.out.print("\b".repeat(s.length()));
//                    s = blue + line + reset;
//                    System.out.print(s);
//                    try {
//                        Thread.sleep(250);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                    System.out.print("\b".repeat(s.length()));
//                }
//
//            }
//        }).start();
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
