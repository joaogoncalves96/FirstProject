package academy.mindswap.Client;

import academy.mindswap.commands.Command;
import academy.mindswap.utils.ColorCodes;

import academy.mindswap.utils.Messages;

import java.io.*;
import java.net.Socket;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Player {

    private Socket socket;
    private final String hostName = "localhost";
    private final int portNumber = 8081;
    private String clientUsername;
    private double credits;
    private Scanner input;
    private volatile boolean isRoundOver;
    private volatile boolean hasRoundStarted;
    private HashMap<String,Double> existingAccounts;
    private int turnsLeft;
    private int previousTurn;
    private boolean isMyTurn;
    private double betToMatch;
    private boolean playerHasToBet;
    private boolean mustDoAction;


    public Player() {
        try {
            this.socket = new Socket(hostName, portNumber);
            askForUserNameAndCredits();

        } catch (IOException e) {
            System.out.println("Couldn't connect.");
            closeAll();
        }
    }

    public void connectToServer () throws IOException{

        Scanner in = new Scanner(socket.getInputStream());
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        new Thread(new ConnectionHandler(this.socket, out)).start();

        while(!socket.isClosed()) {
            readServerMessage(in);
        }

    }

    public boolean checkIfStringIsValidDouble(String doubleString) {
        Pattern regex = Pattern.compile("[^0-9]");
        return regex.matcher(doubleString).find();
    }

    private void readDatabase() {
        try {
            List<String> listOfUsers = Files.readAllLines(Paths.get("resources/users.txt"));
            existingAccounts = new HashMap<>();
            listOfUsers.forEach(s -> existingAccounts.put(s.split("::")[0], Double.parseDouble(s.split("::")[1])));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateDatabase(){

            StringBuilder userString = new StringBuilder();
            existingAccounts
                    .forEach((k,v) -> userString.append(k).append("::").append(v).append("\n"));
        try {

            FileWriter writer = new FileWriter("resources/users.txt");
            writer.write(userString.toString());
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void readServerMessage(Scanner in) throws IOException {
        while (in.hasNextLine()) {
            String serverMessage = in.nextLine();

            if(serverMessage.contains("can't")
                    || serverMessage.contains(Messages.DO_ACTION)) {

                mustDoAction = true;

                if(serverMessage.equals(Messages.DO_ACTION)) {
                    continue;
                }
            }

            if(serverMessage.contains(Messages.PLEASE_BET)) {
                playerHasToBet = true;
                continue;
            }

            if(serverMessage.contains("Last bet: ")) {
                serverMessage = serverMessage.replace("Last bet: ","");
                betToMatch = Double.parseDouble(serverMessage);
                continue;
            }

            if(serverMessage.equals(Messages.PLAYER_TURN)) {
                isMyTurn = true;
            }

            if(serverMessage.equals(Messages.ALL_PLAYERS_FOLDED)) {
                turnsLeft = -2;
                continue;
            }

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
        System.out.println(ColorCodes.RED_BOLD_BRIGHT + "Server disconnected" + ColorCodes.RESET);
        socket.close();
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
                        isMyTurn = false;



                        while(!socket.isClosed()) {

                            existingAccounts.put(clientUsername, credits);

                            updateDatabase();

                            int counter = 0;
                            while (!hasRoundStarted) {
                                if(counter == 0) {
                                    System.out.println(Messages.WAITING_FOR_ROUND);
                                    counter++;
                                }
                            }


                            input = new Scanner(System.in);

                            while(turnsLeft != -2) {

                                while (!isMyTurn) {
                                    if(isRoundOver) break;
                                    Thread.sleep((long) (1000 * Math.random()));
                                }
                                if(isRoundOver) break;

                                String call = input.nextLine();

                                if(!checkForValidCommand(call)) {
                                    if(turnsLeft == -2) break;
                                    System.out.println(Messages.VALID_COMMAND);
                                    continue;
                                }

                                if(turnsLeft == -2) break;

                                bufferedWriter.write(call);
                                bufferedWriter.newLine();
                                bufferedWriter.flush();

                                mustDoAction = false;

                                if(call.contains(Command.BET.getDescription())) {
                                    String bet;
                                    while(!isValidBet(bet = input.nextLine())) {
                                        if(isValidBet(bet)) {
                                            credits -= Double.parseDouble(bet);
                                            break;
                                        }
                                        System.out.println("INVALID BET");
                                    }
                                    bufferedWriter.write(bet);
                                    bufferedWriter.newLine();
                                    bufferedWriter.flush();

                                    playerHasToBet = false;
                                    betToMatch = 0;

                                }
                                if(call.contains(Command.FOLD.getDescription())) {
                                    break;
                                }

                                while (turnsLeft == previousTurn) {
                                    Thread.sleep(100);
//                                    System.out.println("stucj" );
                                    if(playerHasToBet || mustDoAction || isRoundOver) {
                                        break;
                                    }
                                }
                                if(playerHasToBet || mustDoAction) {
                                    isMyTurn = true;
                                    continue;
                                }

                                previousTurn = turnsLeft;
                                isMyTurn = false;
                            }

                            System.out.println(Messages.CONTINUE_PLAYING);
                            String decision = input.nextLine();

                            if(decision.equalsIgnoreCase("exit")) {
                                bufferedWriter.write(decision);
                                bufferedWriter.newLine();
                                bufferedWriter.flush();
                                closeAll();
                                System.out.println(Messages.PLAYER_DISCONNECTED + clientUsername);
                                existingAccounts.put(clientUsername, credits);
                                updateDatabase();
                                break;
                            }

                            bufferedWriter.write(decision);
                            bufferedWriter.newLine();
                            bufferedWriter.flush();
                            isRoundOver = false;

                            isMyTurn = false;

                            turnsLeft = 2;

                        }
                    } catch (IOException | InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                try {
                    closeAll();
                } finally {
                    updateDatabase();
                }
            }
        }
    }

    private boolean checkForValidUserName(String username){
        String regex = "[a-zA-Z0-9_-]{3,18}";

        Pattern pattern = Pattern.compile(regex);

        if(username == null) {
            return false;
        }

        Matcher matcher = pattern.matcher(username);

        return matcher.matches();

    }

    private boolean isValidBet(String bet) throws InterruptedException {
        if(bet == null) {
            return false;
        }

        if(checkIfStringIsValidDouble(bet)) {
            return false;
        }

        if(Double.parseDouble(bet) < 0 || Double.parseDouble(bet) > credits) {
            return false;
        }

        Thread.sleep(10);

        return !(Double.parseDouble(bet) < betToMatch);
    }

    private void askForUserNameAndCredits() throws IOException {

        readDatabase();


        this.input = new Scanner(System.in);
        System.out.println(Messages.ENTER_USERNAME);
        String enteredUsername = input.nextLine();

        if(!checkForValidUserName(enteredUsername)) {
            System.out.println(Messages.USERNAME_INVALID);
            askForUserNameAndCredits();
            return;
        }


        if (existingAccounts.containsKey(enteredUsername)) {
            this.clientUsername = enteredUsername;
            this.credits = existingAccounts.get(enteredUsername);
            System.out.printf(Messages.USERNAME_ALREADY_EXISTS, this.clientUsername, this.credits);
        } else {
            this.clientUsername = enteredUsername;
            this.credits = 10000;
            existingAccounts.put(this.clientUsername,this.credits);
            updateDatabase();
            }
        }

    public void closeAll() {

        try {
            if (input != null) {
                input.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean checkForValidCommand(String command) {

        return  command.equalsIgnoreCase("/call") ||
                command.equalsIgnoreCase("/bet") ||
                command.equalsIgnoreCase("/fold") ||
                command.equalsIgnoreCase("/check") ||
                command.equalsIgnoreCase("/help") ||
                command.equalsIgnoreCase("/raise") ||
                command.equalsIgnoreCase("/allin");
    }
}