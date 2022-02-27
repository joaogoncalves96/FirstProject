package academy.mindswap.Client;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) throws IOException, InterruptedException {

        Player player1 = new Player();

<<<<<<< HEAD
        player1.connectToServer();
        player1.readDatabase();
        //Socket socket = new Socket("0.tcp.ngrok.io", 11154);
=======
        player1.connectToServer("localhost",8081);
>>>>>>> 75bfac3562001be0e6aa4d73b2b6a3b3a5c3b9bb

    }
}
