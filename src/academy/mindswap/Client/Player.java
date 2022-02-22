package academy.mindswap.Client;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class Player implements Runnable {

    private Socket socket;
    private String hostName = "localHost";
    private int portNumber = 8081;
    private String clientUsername;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;

    public Player() {
        try {
            this.socket = new Socket(hostName, portNumber);
//            this.clientUsername = bufferedReader.readLine();

        } catch (IOException e) {
            System.out.println("Couldn't connect.");
            closeAll(socket, bufferedReader, bufferedWriter);
        }
    }

        @Override
        public void run () {
            String messageFromClient;
            while (socket.isConnected()) {
                try {
                    this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                    this.bufferedReader = new BufferedReader(new InputStreamReader(System.in));

                    this.clientUsername = bufferedReader.readLine();

                } catch (IOException e) {
                    e.printStackTrace();
                }


            }
        }

        // ler mensagens, mandar mensagens,
       /* public void broadcastMessage(String messageToSend) {
            for (Player player : playerArrayList) {
                try {
                    if(!player.clientUsername.equals(clientUsername)) {
                        player.bufferedWriter.write(messageToSend);
                        player.bufferedWriter.newLine();
                        player.bufferedWriter.flush();
                    }
                } catch (IOException e) {
                   closeAll(socket, bufferedReader, bufferedWriter);
                }
            }
        }
        public void removePlayer() {
        playerArrayList.remove( this);
        broadcastMessage("SERVER: " + clientUsername + " has left the table");
        }*/

        public void closeAll(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {;
        try {
            if (bufferedReader != null) {
                bufferedReader.close();
            }
            if (bufferedWriter != null) {
                bufferedWriter.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}