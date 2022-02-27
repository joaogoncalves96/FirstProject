package academy.mindswap.Client;

import java.io.*;

public class Client {
    public static void main(String[] args) throws IOException, InterruptedException {

        Player player1 = new Player();

        player1.connectToServer("localhost",8081);

    }
}
