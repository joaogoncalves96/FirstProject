package academy.mindswap.Server;

import academy.mindswap.Server.deck.Deck;

public class Main {

    public static void main(String[] args) {

    Deck deck = new Deck();
    deck.getDeck().forEach(System.out::println);

    }
}
