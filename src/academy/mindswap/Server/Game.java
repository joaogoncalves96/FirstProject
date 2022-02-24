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

        ServerSocket serverSocket = new ServerSocket(PORT);

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

    private boolean havePlayersSeenHands() {
        int count = 0;
        for(PlayerHandler player : listOfPlayers) {
            count += player.seenHand;
        }

        return count == currentPlayersConnected();
    }

    protected int getWinningPlayerIndex() {

       int winningPoints = playerHands.stream()
                .reduce(0, Math::max);

       return playerHands.indexOf(winningPoints);

    }



    public class PlayerHandler implements Runnable {

        private final Socket socket;
        private BufferedWriter out;
        private Scanner in;
        private String message;
        private String username;
        private double credits;
        private ArrayList<Card> playerCards;
        private double bet;
        private int index;
        private boolean hasPlayerFolded;
        private int seenHand;

        private PlayerHandler(Socket socket) {
            this.playerCards = new ArrayList<>(2);
            this.socket = socket;
            this.index = -1;
        }

        public String getUsername() {
            return username;
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

                    Thread.sleep((long) (Math.random() * 500));

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

                    Thread.sleep((long) (Math.random() * 100));

                    System.out.println(printCards(tableCards));

                    Thread.sleep((long) (Math.random() * 100));

                    sendMessage(printCards(playerCards));

                    Thread.sleep((long) (Math.random() * 250));

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

                    Thread.sleep((long) (Math.random() * 100));

                    while(!checkIfPlayersMadeDecision()) {
                        if(counter == 0) {
                            System.out.println(Messages.WAITING_FOR_NEXT_ROUND);
                            sendMessage(Messages.WAITING_FOR_NEXT_ROUND);
                            counter++;
                        }
                    }

                    System.out.println("Players made their decision.");

                    sendMessage("Cards in table: \n" + printCards(tableCards));

                    int points = 0;

                    if(!hasPlayerFolded) {
                        points = analyzePlayerHand();
                        Thread.sleep((long) (Math.random() * 100));
                        playerHands.set(index, points);

                        pot += bet;
                    }

                    seenHand++;

                    System.out.println(username + " has " + points);

                    sendMessage("You've got a " +  getStringHand(points) + "!");

                    sendMessage(printCards(getFinalHand()));

                    counter = 0;
                    while (!havePlayersSeenHands()) {
                        if(counter == 0) {
                            sendMessage(Messages.WAITING_TO_SEE_HAND);
                            counter++;
                        }
                    }


                    if(getWinningPlayerIndex() == index) {
                        System.out.println(username + " won!");
                        sendMessage(Messages.WINNER + (pot - bet) + " credits.");
                    } else {
                        System.out.println(username + " lost :(");
                        sendMessage(Messages.LOSER + bet + " credits.");
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
                    seenHand--;
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

        private int analyzePlayerHand() {
            System.out.println("I got in analyze");
            return HandAnalyzer.analyzeHand(this.playerCards, tableCards);
        }

        private ArrayList<Card> getFinalHand() {
            return HandAnalyzer.makeFinalHand(playerHands.get(index), playerCards, tableCards);
        }

        private String getStringHand(int points) {

            if(points > 2000) {
                return "Four of a kind";
            }
            if(points > 1500) {
                return "Full house";
            }
            if(points > 1000) {
                return "Flush";
            }
            if(points > 750) {
                return "Straight";
            }
            if(points > 500) {
                return "Triplet";
            }
            if(points > 300) {
                return "Double pair";
            }
            if(points > 150) {
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
            gameDecisionsVerification = new boolean[userLimit];
            roundOverVerification = new boolean[userLimit];
            playerHandCount = 0;
            hasPlayerFolded = false;
            for (int i = 0; i < playerHands.size(); i++) {
                playerHands.set(i,0);
            }


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

        protected int getSeenHand() {
            return seenHand;
        }
    }
}
