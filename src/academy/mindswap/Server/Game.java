package academy.mindswap.Server;
import academy.mindswap.Server.deck.*;
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
    private final boolean[] verification;
    private double pot;
    private List<Integer> playerHands;

    public Game(int tableLimit) {

        this.listOfPlayers = Collections.synchronizedList(new ArrayList<>());
        this.userLimit = tableLimit;
        this.deck = DeckFactory.createFullDeck();
        this.tableCards = Collections.synchronizedSet(new HashSet<>());
        this.verification = new boolean[userLimit];
        this.playerHands = Collections.synchronizedList(new ArrayList<>());
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

    private synchronized void dealTableCards() {

        for (int i = 0; i < 5; i++) {
            Card[] cardArray = deck.getDeck().toArray(new Card[deck.getDeckSize()]);
            Card bufferCard = cardArray[(int) (Math.random() * deck.getDeckSize())];
            deck.removeCard(bufferCard);
            tableCards.add(bufferCard);
        }
    }

    public class PlayerHandler implements Runnable {

        private Socket socket;
        private BufferedWriter out;
        private BufferedReader in;
        private String message;
        private String username;
        private double credits;
        private ArrayList<Card> playerCards;
        private double bet;
        private int index;

        private PlayerHandler(Socket socket) {
            this.playerCards = new ArrayList<>(2);
            this.socket = socket;
            this.index = -1;
        }

        private String getUsername() {
            return username;
        }

        protected boolean didIWin() {

            int myPoints = playerHands.get(index);

            for(Integer i : playerHands) {
                if(i > myPoints) return false;
            }

            return true;
        }

        public int getIndex() {
            return index;
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
                    if(index == -1) {
                        synchronized (playerHands) {
                            addPlayer(this);
                            playerHands.add(0);
                            index = playerHands.size() - 1;
                        }

                    }


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

//                    while(isGameUnderWay()) {
//                        if(counter == 0) {
//                            System.out.println(Messages.WAITING_FOR_ROUND);
//                            counter++;
//                        }
//                    }

                    out.write(Messages.STARTING_ROUND);
                    out.newLine();
                    out.flush();
                    givePlayerCards();

                    synchronized (tableCards){
                        if(tableCards.isEmpty()) {
                            dealTableCards();
                        }
                    }

                    System.out.println(tableCardsToString());

                    out.write(playerCardsToString());
                    out.newLine();
                    out.flush();
                    System.out.println("Waiting for player choices...");
                    String playerChoice = in.readLine();

                    if(playerChoice != null) {


                        if(playerChoice.equalsIgnoreCase("bet")) {
                            System.out.println("Waiting for player bet...");
                            String betStr = in.readLine();
                            bet += Double.parseDouble(betStr);
                            pot += bet;
                            synchronized (verification) {
                                System.out.println("I'm here");
                                verification[index] = true;
                            }
                        }
                    }

                    while(!checkIfPlayersMadeDecision()) {
                        if(counter == 0) {
                            String message = "Waiting for players to make decision";
                            System.out.println(message);
                            out.write(message);
                            out.newLine();
                            out.flush();
                            counter++;
                        }
                    }

                    System.out.println("Players made their decision.");

                    out.write("Cards in table: \n" + tableCardsToString());
                    out.newLine();
                    out.flush();

                    int points = analyzePLayerHand();

                    playerHands.set(index, points);

                    System.out.println(username + " has " + points);

                    out.write("You got a " +  getStringHand(points) + "!");
                    out.newLine();
                    out.flush();

                    if(didIWin()) {

                    }

                    wait();

                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        public synchronized void givePlayerCards() {
            for (int i = 0; i < 2; i++) {
                Card[] cardArray = deck.getDeck().toArray(new Card[deck.getDeckSize()]);
                Card bufferCard = cardArray[(int) (Math.random() * deck.getDeckSize())];
                deck.removeCard(bufferCard);
                playerCards.add(bufferCard);
            }
        }

        private String playerCardsToString() {

            StringBuilder cardString = new StringBuilder(Messages.PLAYER_CARDS);
            this.playerCards.forEach(card -> cardString.append(card.toString()));
            return  cardString.toString();

        }

        private String tableCardsToString() {
            StringBuilder cardString = new StringBuilder(Messages.TABLE_CARDS);
            tableCards.forEach(card -> cardString.append(card.toString()));
            return  cardString.toString();
        }

        private synchronized boolean checkIfPlayersMadeDecision() {
            int trues = 0;
            for(boolean b : verification) {
                if(b) trues++;
            }
            return trues == currentPlayersConnected();
        }

        private synchronized void setVerificationsToFalse() {
            for(boolean b : verification) {
                b = false;
            }
        }

        private int analyzePLayerHand() {
            int points = Math.max(this.playerCards.get(0)
                    .getCardRank()
                    .getCardRankPoints(), this.playerCards.get(1)
                    .getCardRank()
                    .getCardRankPoints());

            this.playerCards.addAll(tableCards);

            points += countCards(this.playerCards);

            if(points < 400) {
                points += checkForFlush(this.playerCards);
            }

            if(points < 200) {
                points += checkForSequential(this.playerCards);
            }
            return points;
        }

        private int countCards(ArrayList<Card> playerCards) {
            int points = 0;
            HashMap<CardRank, Integer> cardsCount = new HashMap<>();

            List<CardRank> cardRanks = playerCards.stream()
                    .map(Card::getCardRank).toList();

            for(CardRank cardRank : cardRanks) {
                if(cardsCount.containsKey(cardRank)) {
                    cardsCount.put(cardRank, cardsCount.get(cardRank) + 1);
                } else {
                    cardsCount.put(cardRank, 1);
                }
            }

            int triples = 0;
            int pairs = 0;

            for(CardRank c : cardsCount.keySet()) {
                if(cardsCount.get(c) == 4) {
                    return c.getCardRankPoints() + 1000;
                }

                if(cardsCount.get(c) == 3) {
                    points += c.getCardRankPoints();
                    triples++;
                    continue;
                }

                if(cardsCount.get(c) == 2) {
                    points += c.getCardRankPoints();
                    pairs++;
                }
            }

            if(pairs > 0 && triples == 0) {
                return points + (pairs * 10);
            }

            if(pairs == 1 && triples == 1) {
                return points + 500;
            }
            return 0;
        }

        private int checkForSequential(ArrayList<Card> playerCards ) {
            playerCards.sort(new Comparator<Card>() {
                @Override
                public int compare(Card o1, Card o2) {
                    return Integer.compare(o1.getCardRank().getCardRankPoints(), o2.getCardRank().getCardRankPoints()) ;
                }
            });

            int sequentialCounter = 0;
            for (int i = 0; i < playerCards.size() - 1; i++) {
                int card1Value = playerCards.get(i).getCardRank().getCardRankPoints();
                int card2Value = playerCards.get(i + 1).getCardRank().getCardRankPoints();
                if(card1Value == card2Value + 1) {
                    sequentialCounter++;
                } else {
                    sequentialCounter = 0;
                }
            }
            return sequentialCounter >= 5 ? 300 : 0;
        }

        private int checkForFlush(ArrayList<Card> playerCards) {
            int points = 0;
            HashMap<CardSuit, Integer> cardSuitCount = new HashMap<>();

            List<CardSuit> cardSuits = playerCards.stream()
                    .map(Card::getCardSuit).toList();

            for(CardSuit cardSuit : cardSuits) {
                if(cardSuitCount.containsKey(cardSuits)) {
                    cardSuitCount.put(cardSuit, cardSuitCount.get(cardSuits) + 1);
                } else {
                    cardSuitCount.put(cardSuit, 1);
                }
            }

            if(cardSuitCount.values().stream().noneMatch(value -> value >= 5)) {
                return 0;
            }
            return 400;
        }

        private String getStringHand(int points) {

            if(points > 1000) {
                return "Four of a kind";
            }
            if(points > 500) {
                return "Full house";
            }
            if(points > 400) {
                return "Flush";
            }
            if(points > 300) {
                return "Straight";
            }
            if(points > 100) {
                return "Triplet";
            }
            if(points > 40) {
                return "Double pair";
            }
            if(points > 14) {
                return "Pair";
            }
            return "High card";
        }

    }
}
