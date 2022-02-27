package academy.mindswap.Client;

import java.io.IOException;

public class LaunchPlayer2 {
    public static void main(String[] args) {
        Player player2 = new Player();

        try {
            player2.connectToServer("localhost",8081);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
