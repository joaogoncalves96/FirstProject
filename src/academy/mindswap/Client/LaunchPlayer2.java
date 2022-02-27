package academy.mindswap.Client;

import academy.mindswap.utils.Messages;

import java.io.IOException;

public class LaunchPlayer2 {

    public static void main(String[] args) {
        Player player2 = new Player();

        try {
            player2.connectToServer("localhost",8081);
        } catch (IOException e) {
            System.out.println(Messages.CANT_CONNECT);
            System.exit(1);
        }
    }
}
