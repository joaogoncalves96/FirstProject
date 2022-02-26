package academy.mindswap.Server;
import academy.mindswap.Server.GameExceptions.PlayerDisconnectedException;
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
 * Poker Game v1.01
 * Creates a server that can run poker games
 * BUGS TO FIX:
 *
 * - There's a line between lines and player commands
 * - PLayers can play with credits < 1
 *
 */

public class Game {

    private final List<PlayerHandler> listOfPlayers;
    private final int PORT = 8081;
    private ExecutorService service;
    private final int userLimit;
    private Deck deck;
    private Set<Card> tableCards;
    private boolean[] gameDecisionsVerification;
    private boolean[] roundOverVerification;
    private double pot;
    private final List<Integer> playerHands;
    private int playerHandCount;
    private volatile double lastBet;
    private int TURN_DECIDER;
    private int LAST_ROUND_STARTER;
    private int TURNS_LEFT;
    private final static double TABLE_FEE = 100.00;



    public Game(int tableLimit) {

        this.listOfPlayers = Collections.synchronizedList(new ArrayList<>());
        this.userLimit = tableLimit;
        this.deck = DeckFactory.createFullDeck();
        this.tableCards = Collections.synchronizedSet(new HashSet<>());
        this.gameDecisionsVerification = new boolean[userLimit];
        this.roundOverVerification = new boolean[userLimit];
        this.playerHands = Collections.synchronizedList(new ArrayList<>());
        this.TURN_DECIDER = 0;
        this.TURNS_LEFT = 2;

    }

    public void startServer() throws IOException {

        ServerSocket serverSocket = new ServerSocket(PORT);

        System.out.println("Server initiated. Waiting for users.txt to connect.");

        service = Executors.newCachedThreadPool();

        while(listOfPlayers.size() < userLimit) {
            service.submit(new PlayerHandler(serverSocket.accept()));
        }
    }

    public double getLastBet() {
        return lastBet;
    }

    private void addPlayer(PlayerHandler player) {
        this.listOfPlayers.add(player);
    }

    private void removePlayer(PlayerHandler player) {
        this.listOfPlayers.remove(player);
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
            } catch (IOException | PlayerDisconnectedException e) {
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

    private synchronized boolean haveAllOtherPlayersFolded() {
        return listOfPlayers.stream().filter(player -> !player.hasPlayerFolded).count() == 1;
    }

    private void startNewRound() {

        deck = DeckFactory.createFullDeck();
        pot = 0;
        lastBet = 0;
        tableCards = Collections.synchronizedSet(new HashSet<>(5));
        gameDecisionsVerification = new boolean[userLimit];
        roundOverVerification = new boolean[userLimit];
        playerHandCount = 0;
        TURN_DECIDER = LAST_ROUND_STARTER;


        for (int i = 0; i < playerHands.size(); i++) {
            playerHands.set(i,0);
        }

    }

    public void setLastBet(double bet) {
        this.lastBet = bet;
    }

    public static double getTABLE_FEE() {
        return TABLE_FEE;
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
        private boolean hasAllIn;
        private double playerLastBet;
        private boolean mustDoAction;


        private PlayerHandler(Socket socket) {
            this.playerCards = new ArrayList<>(2);
            this.socket = socket;
            this.index = -1;
        }

        public String getUsername() {
            return username;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof PlayerHandler that)) return false;
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

                    LAST_ROUND_STARTER = TURN_DECIDER;

                    // Get player username
                    if(message == null && username == null) {
                        message = in.nextLine();
                        System.out.printf("User: %s has connected.%n", message);
                        username = message;
                        message = null;
                    }
                    // Get player credits
                    if(message == null && credits == 0.0) {
                        message = in.nextLine();
                        credits = Double.parseDouble(message);
                        System.out.printf(Messages.PLAYER_CREDITS_ENTER, credits);
                        message = null;
                    }

                    int counter = 0;

                    // This while locks players that connect during a round
                    while(isGameUnderWay()) {
                        if(counter == 0) {
                            sendMessage(Messages.WAITING_FOR_ROUND);
                            counter++;
                        }
                    }

                    //Add player to list

                    if(index == -1) {
                        synchronized (playerHands) {
                            addPlayer(this);
                            playerHands.add(0);
                            index = playerHands.size() - 1;
                        }
                    }

                    // Only starts round if there's enough players connected
                    while (currentPlayersConnected() <= 1) {
                        if(counter == 0){
                            System.out.println(Messages.WAITING_FOR_PLAYERS);
                            sendMessage(Messages.WAITING_FOR_PLAYERS);
                            counter++;
                        }
                        loading();
                    }

                    counter = 0;

                    sendMessage(Messages.STARTING_ROUND);

                    Thread.sleep((long) (1000 * Math.random()));

                    this.credits -= TABLE_FEE;

                    this.bet += TABLE_FEE;

                    pot += TABLE_FEE;

                    writePlayersInTable();

                    sendMessage(Messages.TAX_PAY);


                    givePlayerCards();

                    playerHandCount += 2;

                    //Only one player performs the method to deal the cards to the table.

                    if(playersHaveCards()) {
                        synchronized (tableCards){
                            if(tableCards.isEmpty()) {
                                dealTableCards();
                            }
                        }
                    }

                    Thread.sleep(100);

                    System.out.println(printCards(tableCards));

                    sendMessage(printCards(playerCards));

                    System.out.println("Waiting for player choices...");

                    Thread.sleep((long) (Math.random() * 100));

                    // Game turns
                    while(TURNS_LEFT != -2) {

                        String playerChoice = null;

                        // Traps players waiting for their turn.

                        while(!canIdoMyTurn()) {
                            if(counter == 0) {
                                sendMessage(whichPlayerIsDeciding() + Messages.CURRENT_PLAYER_DECIDING);
                                counter++;
                            }

                            Thread.sleep(10);
                        }

                        Thread.sleep(10);

                        // Breaks loop if the player has folded, or if other players folded
                        if(haveAllOtherPlayersFolded() || hasPlayerFolded) {
                            sendMessage(Messages.ALL_PLAYERS_FOLDED);
                            break;
                        }

                        Thread.sleep(100);


                        //Prevents All in players from making turns
                        if(!hasAllIn) {
                            sendMessage(Messages.PLAYER_TURN);
                            Thread.sleep(100);
                            sendMessage(Messages.PLAYER_CALL);
                        }

                        // Send a message saying that the player is all in
                        if(hasAllIn) {
                            sendMessage(Messages.ALL_IN);
                        }


                        // Verification to ask the user for input and checking whether he matched the bet.
                        if(!hasPlayerFolded && !hasAllIn) {
                            if(!hasPlayerMatchedBet()) {
                                sendMessage(Messages.PLAYER_HAS_TO_BET);
                                sendMessage(Messages.PLEASE_BET);
                                gameDecisionsVerification[index] = false;
                            }
                            playerChoice = getUserInput();
                            mustDoAction = true;
                        }

                        // Traps player until he does a valid action that keeps the game going
                        while(!gameDecisionsVerification[index]) {
                            if(playerChoice != null) {
                                dealWithCommand(playerChoice);

                                if(mustDoAction) {
                                    playerChoice = getUserInput();
                                    continue;
                                }
                                System.out.println("Player decided.");
                            }
                            gameDecisionsVerification[index] = true;
                        }

                        // Making sure player matches bet
                        if(!hasPlayerMatchedBet()) {
                            gameDecisionsVerification[index] = false;
                            continue;
                        }

                        // Making sure the player stays in all in.
                        if(hasAllIn) {
                            System.out.println(username + " is all in.");
                            gameDecisionsVerification[index] = true;
                        }

                        // Folded players still use TURN_DECIDER and then leave the loop
                        if(hasPlayerFolded) {
                            decideTurn();
                            break;
                        }

                        Thread.sleep(50);

                        counter = 0;

                        // Traps players waiting for other to make decision
                        while(!checkIfPlayersMadeDecision() || !otherPlayersMatchedBet()) {

                            if(counter == 0) {
                                decideTurn();
                                sendMessage(Messages.WAITING_FOR_NEXT_ROUND);
                                counter++;
                            }

                            if(!hasPlayerMatchedBet() || haveAllOtherPlayersFolded()) {
                                Thread.sleep(150);
                                break;
                            }
                        }

                        if(!hasPlayerMatchedBet()) {
                            continue;
                        }

                        if(haveAllOtherPlayersFolded()) {
                            break;
                        }

                        // Sends the cards to the players
                        if(TURNS_LEFT >= 0) {
                            sendMessage("Cards in table: \n" +
                                    printCards(tableCards.stream().toList().subList(0,tableCards.size() - TURNS_LEFT)));
                            sendMessage(Messages.PLAYER_CARDS);
                            sendMessage(printCards(playerCards));
                        } else {
                            sendMessage("Cards in table: \n" +
                                    printCards(tableCards));
                            sendMessage(Messages.PLAYER_CARDS);
                            sendMessage(printCards(playerCards));
                        }


                        sendMessage(Messages.NEXT);

                        Thread.sleep(500);

                        // Iterates to next turn
                        if(!(TURN_DECIDER == index)) {
                            Thread.sleep(100);
                            this.playerLastBet = 0;

                        } else {
                            TURNS_LEFT--;
                            TURN_DECIDER = LAST_ROUND_STARTER;
                            gameDecisionsVerification = new boolean[userLimit];
                            this.playerLastBet = 0;
                            lastBet = 0;
                        }
                    }


//////////////////// SHOW CARDS

                    int points = 0;

                    // Only count points if player hasn't folded
                    if(!hasPlayerFolded) {
                        points = analyzePlayerHand();
                        Thread.sleep((long) (Math.random() * 100));
                        playerHands.set(index, points);
                    }

                    seenHand++;

                    System.out.println(username + " has " + points);

                    sendMessage("You've got a " +  getStringHand(points) + "!");

                    sendMessage(printCards(getFinalHand()));

                    counter = 0;

                    // Traps players who have seen hands until others do
                    while (!havePlayersSeenHands()) {
                        if(counter == 0) {
                            sendMessage(Messages.WAITING_TO_SEE_HAND);
                            counter++;
                        }
                    }


                    // Sending message to winners and losers
                    if(getWinningPlayerIndex() == index) {

                        System.out.println(username + " won!");

                        sendMessage(Messages.WINNER + (pot - bet) + " credits.");

                        this.credits += (pot - bet);


                    } else {

                        System.out.println(username + " lost :(");
                        sendMessage(Messages.LOSER + (bet + TABLE_FEE) + " credits.");
                        this.credits -= (TABLE_FEE + bet);


                    }

                    System.out.println(Messages.CHECK_PLAYER);

                    String playerDecision = null;


                    // Check if players wants to stay in the game
                    if(in.hasNextLine()) {
                        playerDecision = in.nextLine();
                    }

                    System.out.println("Player decided: " + playerDecision);

                    if(playerDecision != null && playerDecision.equalsIgnoreCase("exit")) {
                        playerHands.remove(index);
                        removePlayer(this);
                        System.out.println(Messages.PLAYER_DISCONNECTED);
                        break;
                    }

                    Thread.sleep((long) (1000 * Math.random()));

                    roundOverVerification[index] = true;

                    counter = 0;

                    while(!havePlayersDecidedToPlay()) {
                        if(counter == 0) {
                            sendMessage(Messages.WAITING_FOR_NEXT_ROUND);
                            counter++;
                        }
                    }


                    // Reset variables to initiate new game
                    if(TURN_DECIDER == index) {
                        startNewRound();
                        restartTable();
                        continue;
                    }
                    restartTable();

                }
                if(socket.isClosed()) {
                    throw new PlayerDisconnectedException();
                }

            } catch (PlayerDisconnectedException e) {
                System.out.println("ERROR DISCONNECTED!");
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                try {
                    out.close();
                    in.close();
                    if(socket.isClosed()) {
                        throw new PlayerDisconnectedException();
                    }
                }  catch (PlayerDisconnectedException e) {
                    System.out.println(ColorCodes.RED_BOLD_BRIGHT + e.getMessage() + ColorCodes.RESET);
                    playerHands.remove(index);
                    removePlayer(this);
                } catch (IOException e) {
                    e.printStackTrace();
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

        public void wentAllIn() {
            playerLastBet = credits;
            setLastBet(playerLastBet);
            pot += playerLastBet;
            this.hasAllIn = true;
        }

        public void doAction() {
            this.mustDoAction = false;
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

        private boolean havePlayersDecidedToPlay() throws InterruptedException {
            int trues = 0;
            for(boolean b : roundOverVerification) {
                if(b) trues++;
            }
            Thread.sleep(10);
            return trues == playerHands.size();
        }


        public void sendMessage(String message) throws IOException, PlayerDisconnectedException {


            if(socket.isClosed()) {
                throw new PlayerDisconnectedException();
            }

            out.write(message);
            out.newLine();
            if(!socket.isClosed()) {
                out.flush();
            }

        }

        private boolean playersHaveCards() {
            return (playerHandCount / 2) == listOfPlayers.size();
        }

        private void dealWithCommand(String action) throws PlayerDisconnectedException, IOException {
            Command command = Command.getCommandFromDescription(action);

            assert command != null;

            command.getCommandHandler().execute(Game.this, this);
        }

        public void fold() {
            this.hasPlayerFolded = true;
        }

        public void bet(double bet) {
            this.playerLastBet = bet;
            this.bet += bet;
        }

        public double getCredits() {
            return credits;
        }


        private String whichPlayerIsDeciding() {
            return listOfPlayers.get(TURN_DECIDER).getUsername();
        }

        public void askForBet() throws IOException, PlayerDisconnectedException {

            sendMessage("Last bet: " + lastBet);
            sendMessage(Messages.INSERT_BET);

            double value = Double.parseDouble(in.nextLine());
            lastBet = value;
            playerLastBet = value;
            bet += value;
            credits -=value;
            doAction();
        }

        public double getPlayerLastBet() {
            return playerLastBet;

        }

        private void writePlayersInTable() throws PlayerDisconnectedException, IOException {

            StringBuilder message = new StringBuilder();
            message.append(ColorCodes.BLUE_BOLD_BRIGHT);

            for(PlayerHandler p : listOfPlayers) {
                message.append(p.getUsername()).append(" is playing this round!\n");
            }
            sendMessage(message.toString());
        }

        private void loading() throws IOException, InterruptedException, PlayerDisconnectedException {
            long animationSpeed = 500;

            String black = ColorCodes.BLACK_BOLD;
            String red = ColorCodes.RED_BOLD_BRIGHT;
            String reset = ColorCodes.RESET;
            while(currentPlayersConnected() <= 1) {

                Thread.sleep(animationSpeed);
                sendMessage("\b" + black + CardSuit.CLUBS.getSuit());

                Thread.sleep(animationSpeed);
                sendMessage("\b" + red + CardSuit.HEARTS.getSuit());

                Thread.sleep(animationSpeed);
                sendMessage("\b" + black + CardSuit.SPADES.getSuit());

                Thread.sleep(animationSpeed);
                sendMessage("\b" + red + CardSuit.DIAMONDS.getSuit());

            }
            sendMessage(reset);
        }

        private synchronized boolean canIdoMyTurn() {
            return index == TURN_DECIDER;
        }

        private void decideTurn() {
            if(TURN_DECIDER == listOfPlayers.size() - 1) {
                TURN_DECIDER = 0;
                return;
            }
            TURN_DECIDER++;
        }

        private synchronized void restartTable() {
            TURNS_LEFT = 2;
            this.hasAllIn = false;

            bet = 0;
            seenHand--;
            playerLastBet = 0;
            playerCards = new ArrayList<>(2);
            hasPlayerFolded = false;

        }

        public boolean hasPlayerMatchedBet() {
            return this.playerLastBet == lastBet;
        }

        private boolean otherPlayersMatchedBet() {
            return listOfPlayers.stream().filter(PlayerHandler::hasPlayerMatchedBet).count() == listOfPlayers.size();
        }

        private String getUserInput() {
           return in.nextLine();
        }
    }
}
