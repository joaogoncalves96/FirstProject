package academy.mindswap.Client;
import academy.mindswap.Server.Game;
import academy.mindswap.utils.Messages;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Pattern;

public class Player {

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

    public void connectToServer ()  throws IOException {
        Scanner in = new Scanner(socket.getInputStream());
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        new Thread(new ConnectionHandler(this.socket, out)).start();

        while (in.hasNextLine()) {
            System.out.println(in.nextLine());
        }
        socket.close();
    }

    public boolean checkIfStringIsValidDouble(String doubleString) {
        Pattern regex = Pattern.compile("[^0-9]");
        return regex.matcher(doubleString).find();
    }


    class ConnectionHandler implements Runnable {

        private BufferedReader bufferedReader;
        private BufferedWriter bufferedWriter;
        private Socket socket;

        private ConnectionHandler(Socket socket, BufferedWriter out) {
            this.socket = socket;
            this.bufferedWriter = out;
        }

        @Override
        public void run () {
            synchronized (this) {
                while (socket.isConnected()) {
                    try {
                        this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                        this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                        bufferedWriter.write(clientUsername);
                        bufferedWriter.newLine();
                        bufferedWriter.flush();

                        bufferedWriter.write(String.valueOf(credits));
                        bufferedWriter.newLine();
                        bufferedWriter.flush();


                        while(!socket.isClosed()) {

                            this.bufferedReader = new BufferedReader(new InputStreamReader(System.in));
                            String call = bufferedReader.readLine();

                            if(!checkForValidCommand(call)) {
                                System.out.println(Messages.VALID_COMMAND);
                                continue;
                            }

                            bufferedWriter.write(call);
                            bufferedWriter.newLine();
                            bufferedWriter.flush();

                            while(call.equalsIgnoreCase("bet")) {

                                System.out.println("Please insert the amount you want to bet: ");
                                String strCredits = bufferedReader.readLine();

                                Pattern regex = Pattern.compile("[^0-9]");

                                if(regex.matcher(strCredits).find()) {
                                    System.out.println(Messages.VALID_CREDITS);
                                    continue;
                                }

                                bufferedWriter.write(strCredits);
                                bufferedWriter.newLine();
                                bufferedWriter.flush();

                                call = "null";

                            }

                            System.out.println("Bet placed, waiting for all the players to bet...");

                            bufferedReader.readLine();




                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private void askForUserNameAndCredits() throws IOException {

        this.bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        System.out.println(Messages.ENTER_USERNAME);
        this.clientUsername = bufferedReader.readLine();
        System.out.println(Messages.ENTER_CREDITS);

        while(credits == 0.0) {

            String strCredits = bufferedReader.readLine();

            if(!checkIfStringIsValidDouble(strCredits)) {

                System.out.println(Messages.VALID_CREDITS);
                continue;
            }
            this.credits = Double.parseDouble(strCredits);
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