package academy.mindswap.Client;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) throws IOException, InterruptedException {

        Player player1 = new Player();

        player1.connectToServer("localhost",8081);

    }
}
