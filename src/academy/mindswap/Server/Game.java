package academy.mindswap.Server;
import academy.mindswap.Server.deck.*;
import academy.mindswap.commands.Command;
import academy.mindswap.utils.ColorCodes;
import academy.mindswap.utils.Messages;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
    private boolean[] gameDecisionsVerification;
    private boolean[] roundOverVerification;
    private double pot;
    private List<Integer> playerHands;
    private int playerHandCount;

    public Game(int tableLimit) {

        this.listOfPlayers = Collections.synchronizedList(new ArrayList<>());
        this.userLimit = tableLimit;
        this.deck = DeckFactory.createFullDeck();
        this.tableCards = Collections.synchronizedSet(new HashSet<>());
        this.gameDecisionsVerification = new boolean[userLimit];
        this.roundOverVerification = new boolean[userLimit];
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

    public void broadCastMessage(String message) {
        for(PlayerHandler player : listOfPlayers) {
            try {
                player.sendMessage(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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
        private Scanner in;
        private String message;
        private String username;
        private double credits;
        private ArrayList<Card> playerCards;
        private double bet;
        private int index;
        private boolean hasPlayerFolded;
        private ArrayList<Card> bestHand;


        private PlayerHandler(Socket socket) {
            this.playerCards = new ArrayList<>(2);
            this.socket = socket;
            this.index = -1;
        }


        public String getUsername() {
            return username;
        }

        protected boolean didIWin() {

            if(this.hasPlayerFolded) {
                return false;
            }

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

                in = new Scanner(socket.getInputStream());
                out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

                System.out.println(Messages.CONNECTING);

                playerHands = new ArrayList<>();

                while(!socket.isClosed()) {

                    // Get player username
                    while (message == null && username == null) {
                        message = in.nextLine();
                        System.out.printf("User: %s has connected.%n", message);
                        username = message;
                        message = null;
                        break;
                    }
                    // Get player credits
                    while (message == null && credits == 0.0) {
                        message = in.nextLine();
                        credits = Double.parseDouble(message);
                        System.out.printf(Messages.PLAYER_CREDITS_ENTER, credits);
                        message = null;
                        break;
                    }

                    int counter = 0;

                    System.out.println("Placing player in table...");


                    while(isGameUnderWay()) {
                        if(counter == 0) {
                            sendMessage(Messages.WAITING_FOR_ROUND);
                            counter++;
                        }
                    }

                    if(index == -1) {
                        synchronized (playerHands) {
                            addPlayer(this);
                            playerHands.add(0);
                            index = playerHands.size() - 1;
                        }
                    }

                    while (currentPlayersConnected() <= 1) {
                        if(counter == 0){
                            System.out.println(Messages.WAITING_FOR_PLAYERS);
                            sendMessage(Messages.WAITING_FOR_PLAYERS);
                            counter++;
                        }
                    }

                    counter = 0;

                    sendMessage(Messages.STARTING_ROUND);
                    Thread.sleep(800);

                    givePlayerCards();

                    playerHandCount += 2;

                    System.out.println("Time: " + System.currentTimeMillis());

                    if(playersHaveCards()) {
                        synchronized (tableCards){
                            if(tableCards.isEmpty()) {
                                dealTableCards();
                            }
                        }
                    }

                    System.out.println(printCards(tableCards));

                    sendMessage(printCards(playerCards));

                    Thread.sleep(800);

                    sendMessage(Messages.PLAYER_CALL);

                    System.out.println("Waiting for player choices...");

                    String playerChoice = in.nextLine();

                    if(playerChoice != null) {

                        dealWithCommand(playerChoice);

                        synchronized (gameDecisionsVerification) {
                            System.out.println("Player decided.");
                            gameDecisionsVerification[index] = true;
                        }
                    }

                    while(!checkIfPlayersMadeDecision()) {
                        if(counter == 0) {
                            System.out.println(Messages.WAITING_FOR_NEXT_ROUND);
                            sendMessage(Messages.WAITING_FOR_NEXT_ROUND);
                            counter++;
                        }
                    }

                    System.out.println("Players made their decision.");

                    sendMessage("Cards in table: \n" + printCards(tableCards));

                    int points = analyzePLayerHand();
                    if(!hasPlayerFolded) {
                        playerHands.set(index, points);
                    }

                    System.out.println(username + " has " + points);

                    sendMessage("You got a " +  getStringHand(points) + "!");

                    if(didIWin()) {
                        System.out.println(username + " won!");
                        sendMessage(Messages.WINNER + (pot - bet) + " credits.");
                    } else {
                        System.out.println(username + " lost :(");
                        sendMessage(Messages.LOSER + bet + "credits.");
                    }


                    System.out.println(Messages.CHECK_PLAYER);
                    String playerDecision = null;
                    if(in.hasNextLine()) {
                        playerDecision = in.nextLine();
                    }


                    System.out.println("Player decided: " + playerDecision);

                    if(playerDecision.equalsIgnoreCase("exit")) {
                        playerHands.remove(index);
                        removePlayer(this);
                        System.out.println();
                        break;
                    }

                    roundOverVerification[index] = true;
                    counter = 0;

                    while(!havePlayersDecidedToPlay()) {
                        if(counter == 0) {
                            sendMessage(Messages.WAITING_FOR_NEXT_ROUND);
                            counter++;
                        }
                    }
                    startNewRound();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                try {
                    socket.close();
                    removePlayer(this);
                } catch (IOException e) {
                    System.out.println(Messages.PLAYER_DISCONNECTED);
                }
            }
        }

        public synchronized void givePlayerCards() {

            CardRank[] cardRank = CardRank.values();
            CardSuit[] suitSuit = CardSuit.values();

            for (int i = 0; i < 2; i++) {
                Card bufferCard = new Card(
                        cardRank[(int) (Math.random() * cardRank.length)],
                        suitSuit[(int) (Math.random() * suitSuit.length)]);

                if(deck.getDeck().contains(bufferCard)) {
                    deck.removeCard(bufferCard);
                    playerCards.add(bufferCard);
                    continue;
                }
                i--;
            }
        }

        private String printCards(Collection<Card> cardList) {

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

        private synchronized boolean checkIfPlayersMadeDecision() {
            int trues = 0;
            for(boolean b : gameDecisionsVerification) {
                if(b) trues++;
            }
            return trues == currentPlayersConnected();
        }

        private synchronized void setVerificationsToFalse() {
            for(boolean b : gameDecisionsVerification) b = false;
            for(boolean b : roundOverVerification) b = false;
        }

        private int analyzePLayerHand() {
//            int points = Math.max(this.playerCards.get(0)
//                    .getCardRank()
//                    .getCardRankPoints(), this.playerCards.get(1)
//                    .getCardRank()
//                    .getCardRankPoints());
            int points = 0;

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

        private int checkForSequential(ArrayList<Card> playerCards) {
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
                if(cardSuitCount.containsKey(cardSuit)) {
                    cardSuitCount.put(cardSuit, cardSuitCount.get(cardSuit) + 1);
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
            if(points > 27) {
                return "Double pair";
            }
            if(points > 14) {
                return "Pair";
            }
            return "High card";
        }

        private void startNewRound() {

            playerCards = new ArrayList<>(2);
            deck = DeckFactory.createFullDeck();
            bet = 0;
            pot = 0;
            tableCards = Collections.synchronizedSet(new HashSet<>());
            setVerificationsToFalse();
            playerHandCount = 0;
            hasPlayerFolded = false;

        }

        private boolean havePlayersDecidedToPlay() {
            int trues = 0;
            for(boolean b : roundOverVerification) {
                if(b) trues++;
            }

            return trues == playerHands.size();
        }

        private void sendMessage(String message) throws IOException {

            out.write(message);
            out.newLine();
            out.flush();

        }

        private boolean playersHaveCards() {
            return (playerHandCount / 2) == listOfPlayers.size();
        }

        private void dealWithCommand(String action) {

            Command command = Command.getCommandFromDescription(action);

            command.getCommandHandler().execute(Game.this, this);

        }

        public void fold() {
            this.hasPlayerFolded = true;
        }

        public void setBet(double bet) {
            this.bet = bet;
        }

        public double getCredits() {
            return credits;
        }

        public void askForBet() throws IOException {
            sendMessage(Messages.INSERT_BET);
            bet = Double.parseDouble(in.nextLine());
        }

        public double getBet() {
            return bet;
        }
    }
}
