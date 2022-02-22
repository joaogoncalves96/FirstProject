package academy.mindswap.Client;

import java.io.IOException;

public class LaunchPlayer3 {

    public static void main(String[] args) {
        Player player3 = new Player();

        try {
            player3.connectToServer();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
