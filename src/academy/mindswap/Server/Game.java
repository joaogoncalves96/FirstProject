package academy.mindswap.Server;
import academy.mindswap.Server.deck.Card;
import academy.mindswap.Server.deck.Deck;
import academy.mindswap.Server.deck.DeckFactory;
import academy.mindswap.utils.Messages;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * Poker Game v0.01
 * Creates a server that can run poker games
 */

public class Game {


    private List<PlayerHandler> listOfPlayers;
    private final int PORT = 8081;
    private ExecutorService service;
    private ServerSocket serverSocket;
    private final int userLimit;
    private Deck deck;
    private Set<Card> tableCards;

    public Game(int tableLimit) {

        this.listOfPlayers = Collections.synchronizedList(new ArrayList<>());
        this.userLimit = tableLimit;
        this.deck = DeckFactory.createFullDeck();
        this.tableCards = Collections.synchronizedSet(new HashSet<>());
    }

    public void startServer() throws IOException {

        this.serverSocket = new ServerSocket(PORT);

        System.out.println("Server initiated. Waiting for users to connect.");

        service = Executors.newCachedThreadPool();

        while(listOfPlayers.size() < userLimit) {
            service.submit(new PlayerHandler(serverSocket.accept()));
        }
    }

    private void addPlayer(PlayerHandler player) {
        this.listOfPlayers.add(player);
    }

    private void removePlayer(PlayerHandler player) {
        this.listOfPlayers.remove(player);
    }

    private boolean checkIfPlayerExists(PlayerHandler player) {
        return listOfPlayers.contains(player);
    }

    private int currentPlayersConnected() {
        return listOfPlayers.size();
    }

    private boolean isGameUnderWay() {
        return this.deck.getDeckSize() < 52;
    }

    private void dealTableCards() {
        this.deck.getDeck()
                .stream()
                .limit(5)
                .forEach(tableCards::add); // card -> tableCard.add(card);
    }

    public class PlayerHandler implements Runnable {

        private Socket socket;
        private BufferedWriter out;
        private BufferedReader in;
        private String message;
        private String username;
        private double credits;
        private HashSet<Card> playerCards;

        private PlayerHandler(Socket socket) {
            this.playerCards = new HashSet<>(2);
            this.socket = socket;
        }

        private String getUsername() {
            return username;
        }


        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof PlayerHandler)) return false;
            PlayerHandler that = (PlayerHandler) o;
            return username.equals(that.username);
        }

        @Override
        public int hashCode() {
            return Objects.hash(username);
        }

        @Override
        public void run() {

            addPlayer(this);

            try {

                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

                System.out.println(Messages.CONNECTING);

                while(!socket.isClosed()) {

                    // Get player username
                    while (message == null && username == null) {
                        message = in.readLine();
                        System.out.printf("User: %s has connected.%n", message);
                        username = message;
                        message = null;
                        break;
                    }
                    // Get player credits
                    while (message == null && credits == 0.0) {
                        message = in.readLine();
                        credits = Double.parseDouble(message);
                        System.out.printf(Messages.PLAYER_CREDITS_ENTER, credits);
                        message = null;
                        break;
                    }

                    System.out.println("Placing player in table...");
                    int counter = 0;
                    while (currentPlayersConnected() <= 1) {
                        if(counter == 0){
                            System.out.println(Messages.WAITING_FOR_PLAYERS);
                            out.write(Messages.WAITING_FOR_PLAYERS);
                            out.newLine();
                            out.flush();
                            counter++;
                        }
                    }
                    counter = 0;

                    while(isGameUnderWay()) {
                        if(counter == 0) {
                            System.out.println(Messages.WAITING_FOR_ROUND);
                            counter++;
                        }
                    }

                    out.write(Messages.STARTING_ROUND);
                    out.newLine();
                    out.flush();

                    dealTableCards();
                    givePlayerCards();

                    out.write(cardsToString());
                    out.newLine();
                    out.flush();

                    String playerChoice = in.readLine();



                }





            } catch (IOException e) {

                e.printStackTrace();

            }


        }



        public void givePlayerCards() {
          this.playerCards.add(deck.getDeck().stream().findAny().get());
          this.playerCards.add(deck.getDeck().stream().findAny().get());

          for(Card card : playerCards) {
              deck.removeCard(card);
          }

        }

        private String cardsToString() {

            StringBuilder cardString = new StringBuilder();
            cardString.append(Messages.PLAYER_CARDS);
            this.playerCards.forEach(card -> cardString.append(card.toString()));
            return  cardString.toString();

        }
    }
}
