package academy.mindswap.Client;

import academy.mindswap.utils.Messages;

import java.io.*;

public class Client {
    public static void main(String[] args) {

        Player player1 = new Player();


        try {
            player1.connectToServer("localhost",8081);
        } catch (IOException e) {
            System.out.println(Messages.CANT_CONNECT);
            System.exit(1);
        }

    }
}
