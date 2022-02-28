package academy.mindswap.Client;

import java.io.*;

public class LaunchPlayer1 {
    public static void main(String[] args) throws IOException, InterruptedException {

        /**
         * Instance of a new player
         * the Player will connect to the server
         */

        Player player2 = new Player();

        try {
            player2.connectToServer("localhost", 8081);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
