/*
 * @(#)Player.java        1.0 26/02/2022
 *
 * Copyright (c) MindSwap Academy - David Millasseau, Tiago Correia & João Gonçalves
 * All rights reserved.
 *
 * This software was produced to become our first group project.
 */

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

    /**
     * Start player on specified host and port
     * Ask for username
     * @throws IOException when unable to connect to server
     */

    public Player() {


        try {
            askForUserNameAndCredits();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    /**
     *Connects the server through the same socket.
     * While the server does not close, read the messages from the server
     */
    public void connectToServer (String host, int port) throws IOException{

        socket = new Socket(host, port);
        Scanner in = new Scanner(socket.getInputStream());
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        new Thread(new ConnectionHandler(this.socket, out)).start();

        while(!socket.isClosed()) {
            readServerMessage(in);
        }

    }
    /**
     * It serves to validate the double
     * Pattern compile only accepts from 0 to 9
     * Find returns boolean value that shows whether a substring of the input string matches the pattern of that match
     */
    public boolean checkIfStringIsValidDouble(String doubleString) {
        Pattern regex = Pattern.compile("[^0-9]");
        return regex.matcher(doubleString).find();
    }

    /**
     *Reads a txt file from users
     * Divides the list of user in a String and the credits in another String
     */

    private void readDatabase() {
        try {
            List<String> listOfUsers = Files.readAllLines(Paths.get("resources/users.txt"));
            existingAccounts = new HashMap<>();
            listOfUsers.forEach(s -> existingAccounts.put(s.split("::")[0], Double.parseDouble(s.split("::")[1])));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     *If someone connects to the server and does not have an account in the users txt
     * the txt creates a new String with the username
     * @throws IOException
     */

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

    /**
     * Reads and interprets server messages
     * If the message contains place a bet the player can bet
     *If you hold back, it's your turn, it's the player's turn
     *If the message is next, decrement the remaining turns
     *If the message is loser, players lose credits and the round ends
     *If the message is of congrats, it increases the player's credits
     */

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

        /**
         * Constructor method to initialize the properties
         * @param socket send the client's request to the server
         * @param out serve for write.
         * */

        private ConnectionHandler(Socket socket, BufferedWriter out) {
            this.socket = socket;
            this.bufferedWriter = out;
        }

        @Override
        public void run () {

            /**
             *Here we synchronize the object
             *If the socket is connected, continue, if not, stop.
             * While the socket is connected the player can write
             * Save the credits for user.
             */
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

                        /**
                         *If the player writes a command while a game is in progress,
                         *they will not be allowed to play that round
                         */

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

                            /**
                             * This is the game
                             * How many turns have the game, is always the same. 1,2,3,4
                             *
                             */
                            input = new Scanner(System.in);

                            while(turnsLeft != -2) {

                                while (!isMyTurn) {
                                    if(isRoundOver) break;
                                    Thread.sleep((long) (1000 * Math.random()));
                                }
                                if(isRoundOver) break;

                                String call = input.nextLine();

                                /**
                                 * Only put input when is your turn
                                 * Check if command is valid
                                 */

                                if(!checkForValidCommand(call)) {
                                    if(turnsLeft == -2) break;
                                    System.out.println(Messages.VALID_COMMAND);
                                    continue;
                                }

                                if(turnsLeft == -2) break;

                                bufferedWriter.write(call);
                                bufferedWriter.newLine();
                                bufferedWriter.flush();

                                /**
                                 * if this boolean is true, you have make another command
                                 * If not, you complete your turn.
                                 */

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
                                /**
                                 *If the missing turns and the turns that took place are the same
                                 put the thread to have a time slot
                                 *Booleans of server control, but is from player
                                 */

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

                            /**
                             * After the player insert 'exit', the player socket will close and left the game
                             */

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
    /**
     * Checks for a valid username with more than 3 chars
     * If username is null returns false
     * If true, finds the contents of the String object
     */

    private boolean checkForValidUserName(String username){
        String regex = "[a-zA-Z0-9_-]{3,18}";

        Pattern pattern = Pattern.compile(regex);

        if(username == null) {
            return false;
        }

        Matcher matcher = pattern.matcher(username);

        return matcher.matches();

    }


    /**
     * to check if the bet is valid
     * Bearing in mind that it has to be double
     */


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

    /**
     *Ask the player the name, the username can be valid or invalid
     * If the account exists, we obtain the name and credits of the same
     * If this account does not exist, it creates a new one and gives the default credits (10000)
     */

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
    /**
     * This method we use to properly shut down socket,
     * @throws IOException when unable to connect to server
     */
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

    /**
     *Confirms if the command was the chosen command, ignores CAPS
     */

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