package academy.mindswap.Client;
import academy.mindswap.utils.Messages;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class Player implements Runnable {

    private Socket socket;
    private String hostName = "localHost";
    private int portNumber = 8081;
    private String clientUsername;
    private double credits;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;

    public Player() {
        try {
            this.socket = new Socket(hostName, portNumber);
            askForUserNameAndCredits();

        } catch (IOException e) {
            System.out.println("Couldn't connect.");
            closeAll(socket, bufferedReader, bufferedWriter);
        }
    }


    private void askForUserNameAndCredits() throws IOException {

        Pattern regex = Pattern.compile("[^0-9]");

        this.bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        System.out.println(Messages.ENTER_USERNAME);
        this.clientUsername = bufferedReader.readLine();
        System.out.println(Messages.ENTER_CREDITS);

        while(credits == 0.0) {
            String strCredits = bufferedReader.readLine();
            if(regex.matcher(strCredits).lookingAt()) {
                System.out.println(Messages.VALID_CREDITS);
                continue;
            }
            this.credits = Double.parseDouble(strCredits);
        }
    }



    @Override
    public void run () {
        String messageFromClient;
        while (socket.isConnected()) {
            try {

                this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                bufferedWriter.write(this.clientUsername);
                bufferedWriter.newLine();
                bufferedWriter.flush();

                bufferedWriter.write(String.valueOf(this.credits));
                bufferedWriter.newLine();
                bufferedWriter.flush();

                String status =  bufferedReader.readLine();
                System.out.println(status);

                while(!socket.isClosed()) {

                    String serverMessage =  bufferedReader.readLine();
                    String cards =  bufferedReader.readLine();

                    System.out.println(serverMessage);
                    System.out.println(cards);
                    this.bufferedReader = new BufferedReader(new InputStreamReader(System.in));
                    String call = bufferedReader.readLine();

                    if(!checkForValidCommand(call)) {
                        System.out.println("I'm here");
                        continue;
                    }

                    bufferedWriter.write(call);
                    bufferedWriter.newLine();
                    bufferedWriter.flush();

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void closeAll(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {

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

    private boolean checkForValidCommand(String command) {

        return command.equalsIgnoreCase("call") ||
                command.equalsIgnoreCase("bet") ||
                command.equalsIgnoreCase("fold") ||
                command.equalsIgnoreCase("all in");
    }


}