package academy.mindswap.Client;
import academy.mindswap.utils.Messages;

import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.Scanner;
import java.util.regex.Pattern;

public class Player {

    private Socket socket;
    private final String hostName = "localhost";
    private final int portNumber = 8081;
    private String clientUsername;
    private double credits;
    private BufferedReader bufferedReader;
    private volatile boolean isRoundOver;
    private volatile boolean hasRoundStarted;
    private HashMap<String,Double> existingAccounts;
    private int turnsLeft;
    private int previousTurn;

    public Player() {
        try {
            this.socket = new Socket(hostName, portNumber);
            askForUserNameAndCredits();

        } catch (IOException e) {
            System.out.println("Couldn't connect.");
            closeAll();
        }
    }

    public void connectToServer ()  throws IOException {

//        readDatabase();
        Scanner in = new Scanner(socket.getInputStream());
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        new Thread(new ConnectionHandler(this.socket, out)).start();

        while (in.hasNextLine()) {
            String serverMessage = in.nextLine();

            if(serverMessage.contains("\b")) {
                System.out.print(serverMessage);
                continue;
            }

            if(serverMessage.equals(Messages.NEXT)) {
                turnsLeft--;
            }

            System.out.println(serverMessage);


            if(serverMessage.startsWith("You lost")) {

                serverMessage = serverMessage.replace(Messages.LOSER,"")
                        .replace(" credits.","");

                credits -= Double.parseDouble(serverMessage);
                System.out.printf(Messages.CURRENT_CREDITS, credits);
                isRoundOver = true;
                hasRoundStarted = false;
                continue;

            }

            if(serverMessage.startsWith("Congrats")) {

                serverMessage = serverMessage.replace(Messages.WINNER,"")
                        .replace(" credits.","");

                credits += Double.parseDouble(serverMessage);
                System.out.printf(Messages.CURRENT_CREDITS, credits);
                isRoundOver = true;
                hasRoundStarted = false;
                continue;
            }

            if(serverMessage.startsWith("Starting round")) {
                hasRoundStarted = true;
            }

        }
        socket.close();
    }

    public boolean checkIfStringIsValidDouble(String doubleString) {
        Pattern regex = Pattern.compile("[^0-9]");
        return regex.matcher(doubleString).find();
    }

    private void readDatabase() {
        File data = new File("C:/MindSwap/PokerGame/FirstProject/resources/users");

        try {
            FileInputStream dataInput = new FileInputStream(data);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    class ConnectionHandler implements Runnable {

        private BufferedWriter bufferedWriter;
        private final Socket socket;

        private ConnectionHandler(Socket socket, BufferedWriter out) {
            this.socket = socket;
            this.bufferedWriter = out;
        }

        @Override
        public void run () {
            synchronized (this) {
                while (socket.isConnected()) {
                    try {
                        if(socket.isClosed()) {
                            break;
                        }
                        this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

                        bufferedWriter.write(clientUsername);
                        bufferedWriter.newLine();
                        bufferedWriter.flush();

                        bufferedWriter.write(String.valueOf(credits));
                        bufferedWriter.newLine();
                        bufferedWriter.flush();

                        turnsLeft = 2;
                        previousTurn = 2;


                        while(!socket.isClosed()) {

                            int counter = 0;
                            while (!hasRoundStarted) {
                                if(counter == 0) {
                                    System.out.println(Messages.WAITING_FOR_ROUND);
                                    counter++;
                                }
                            }


                            bufferedReader = new BufferedReader(new InputStreamReader(System.in));

                            while(turnsLeft != -2) {
                                String call = bufferedReader.readLine();

                                if(!checkForValidCommand(call)) {
                                    if(turnsLeft == -2) break;
                                    System.out.println(Messages.VALID_COMMAND);
                                    continue;
                                }

                                if(turnsLeft == -2) break;
                                bufferedWriter.write(call);
                                bufferedWriter.newLine();
                                bufferedWriter.flush();

                                if(call.contains("/bet")) {
                                    String bet = bufferedReader.readLine();
                                    bufferedWriter.write(bet);
                                    bufferedWriter.newLine();
                                    bufferedWriter.flush();
                                }

                                while (turnsLeft == previousTurn) {
                                }
                                previousTurn = turnsLeft;
                            }

                            System.out.println(Messages.CONTINUE_PLAYING);
                            String decision = bufferedReader.readLine();

                            if(decision.equalsIgnoreCase("exit")) {
                                bufferedWriter.write(decision);
                                bufferedWriter.newLine();
                                bufferedWriter.flush();
                                closeAll();
                                System.out.println(Messages.PLAYER_DISCONNECTED + clientUsername);
                                break;
                            }

                            bufferedWriter.write(decision);
                            bufferedWriter.newLine();
                            bufferedWriter.flush();

                            isRoundOver = false;
                            turnsLeft = 2;

                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
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

            if(checkIfStringIsValidDouble(strCredits)) {
                System.out.println(Messages.VALID_CREDITS);
                continue;
            }
            this.credits = Double.parseDouble(strCredits);
        }
    }

    public void closeAll() {

        try {
            if (bufferedReader != null) {
                bufferedReader.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean checkForValidCommand(String command) {

        return command.equalsIgnoreCase("/call") ||
                command.equalsIgnoreCase("/bet") ||
                command.equalsIgnoreCase("/fold") ||
                command.equalsIgnoreCase("/allin");
    }
}