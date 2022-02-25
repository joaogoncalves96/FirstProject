package academy.mindswap.utils;

import academy.mindswap.Server.Game;

public class Messages {

    public static final String CONNECTING = "Connecting player...";
    public static final String WAITING_FOR_PLAYERS = "Please wait for another player to connect...";
    public static final String WAITING_FOR_ROUND = "Please wait for the current round to be over.";
    public static final String WAITING_FOR_NEW_ROUND = "Waiting for new round to start...";
    public static final String PLAYER_CARDS = "You have: \n";
    public static final String PLAYER_CREDITS_ENTER = "You entered with %.2f credtis %n";
    public static final String ENTER_USERNAME = "Please enter your username: ";
    public static final String ENTER_CREDITS = "Please enter your CREDITS: ";
    public static final String VALID_CREDITS = "Please insert a valid amount: ";
    public static final String STARTING_ROUND = "Starting round, dealing cards...";
    public static final String PLAYER_DISCONNECTED = "PLayer disconnected.";
    public static final String TABLE_CARDS = "Current cards on table: \n";
    public static final String VALID_COMMAND = "Please insert a valid command: \n";
    public static final String WINNER = "Congrats you win! You've gained ";
    public static final String LOSER = "You lost better luck next time! You've lost ";
    public static final String NOT_ENOUGH_CREDITS = "You don't have enough credits to make this bet..";
    public static final String INSERT_BET = "Please insert how much you want to bet this round: ";
    public static final String PLACED_BET = "Bet placed, waiting for all the players to bet...";
    public static final String CURRENT_CREDITS = "You know have %.2f credits. %n";
    public static final String WAITING_FOR_NEXT_ROUND = "Waiting for players to decide...";
    public static final String PLAYER_CALL = "What do you want to do? \nCHECK || BET || CALL || ALL-IN || FOLD \n";
    public static final String CHECK_PLAYER = "Reading players decisions...";
    public static final String FOLDED = " has folded.\n";
    public static final String BET = " has bet ";
    public static final String ALL_IN = " went all in with ";
    public static final String CURRENT_PLAYER_DECIDING = " is deciding...";
    public static final String WAITING_TO_SEE_HAND = "Waiting for players too see their hands..";
    public static final String CREDITS = " credits!";
    public static final String CONTINUE_PLAYING = "Do you want to play another round? \nType EXIT to leave game, press enter to play another round. \n";
    public static final String USERNAME_ALREADY_EXISTS = "Welcome %s. You have %.2f credits. %n";
    public static final String USERNAME_INVALID = "Please enter a valid username:\n" +
            "-Must be between 3 and 18 characters\n" +
            "-No special characters.";
    public static final String NEXT = "Next turn!";
    public static final String PLAYER_TURN = "It's your turn.";
    public static final String ALL_PLAYERS_FOLDED = "Everyone else folded!";
    public static final String IS_ALL_IN = "You're All in!";
    public static final String TAX_PAY = "You have payed " + ColorCodes.GREEN_BOLD_BRIGHT
            + Game.getTABLE_FEE() + ColorCodes.RESET + " credits to play this round.";

    public static final String CALL = " has called the bet!";
    public static final String PLAYER_CHECKED = " has checked.";
    public static final String MATCH_BET = "You have to call or raise the current bet.";
    public static final String PLAYER_HAS_TO_BET = "Do you want to match the bet?";


    public static final String CARD_SPACE = " ".repeat(5) + "\n" +
                                            " ".repeat(5) + "\n" +
                                            " ".repeat(5) + "\n";






}
