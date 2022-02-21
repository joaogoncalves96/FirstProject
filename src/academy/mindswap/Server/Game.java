package academy.mindswap.Server;
import academy.mindswap.Server.deck.Card;
import academy.mindswap.Server.deck.Deck;
import academy.mindswap.Server.deck.DeckFactory;

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
    private int PORT = 8081;
    private ExecutorService service;
    private ServerSocket serverSocket;
    private int userLimit;
    private Deck deck;

    public Game(int tableLimit) {

        this.listOfPlayers = Collections.synchronizedList(new ArrayList<>());
        this.userLimit = tableLimit;
        this.deck = DeckFactory.createFullDeck();
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

    public class PlayerHandler implements Runnable {

        private Socket socket;
        private BufferedWriter out;
        private BufferedReader in;
        private String message;
        private String username;
        private double credits;
        private Set<Card> playerCards;

        private PlayerHandler(Socket socket) {
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



            } catch (IOException e) {

                e.printStackTrace();

            }


        }
    }
}
