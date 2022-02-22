package academy.mindswap.Client;

import java.io.IOException;

public class LaunchPlayer2 {
    public static void main(String[] args) {
        Player player2 = new Player();

        try {
            player2.connectToServer();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
