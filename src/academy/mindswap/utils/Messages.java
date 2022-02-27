/*
 * @(#)Messages.java        1.0 26/02/2022
 *
 * Copyright (c) MindSwap Academy - David Millasseau, Tiago Correia & João Gonçalves
 * All rights reserved.
 *
 * This software was produced to become our first group project.
 */

package academy.mindswap.utils;

import academy.mindswap.Server.Game;


/**
 * Messages sent to console by the player and server
 */

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
    public static final String PLAYER_CALL = "What do you want to do? \nCHECK || BET || CALL || " +
                                                "ALL-IN || FOLD || RAISE || WALLET || HELP \n";

    public static final String CHECK_PLAYER = "Reading players decisions...";
    public static final String FOLDED = " has folded.\n";
    public static final String BET = " has bet ";
    public static final String ALL_IN = " went all in with ";
    public static final String CURRENT_PLAYER_DECIDING = " is deciding...";
    public static final String WAITING_TO_SEE_HAND = "Waiting for players too see their hands..";
    public static final String CREDITS = " credits!";
    public static final String CONTINUE_PLAYING = "Do you want to play another round? \nType" +
            ColorCodes.RED_BOLD_BRIGHT +" EXIT " + ColorCodes.RESET
            + "to leave the game, press enter to play another round.";
    public static final String USERNAME_ALREADY_EXISTS = "Welcome %s. You have %.2f credits. %n";
    public static final String USERNAME_INVALID = "Please enter a valid username:\n" +
            "-Must be between 3 and 18 characters\n" +
            "-No special characters.";
    public static final String NEXT = "Next turn!";
    public static final String PLAYER_TURN = "It's your turn.";
    public static final String ALL_PLAYERS_FOLDED = "Everyone else folded!";
    public static final String IS_ALL_IN = "You're All in!";
    public static final String TAX_PAY = ColorCodes.RESET + "You have payed " + ColorCodes.GREEN_BOLD_BRIGHT
            + Game.getTABLE_FEE() + ColorCodes.RESET + " credits to play this round.";


    public static final String RAISE = " has raised!";
    public static final String CALL = " has called the bet!";
    public static final String PLAYER_CHECKED = " has checked.";
    public static final String CANT_CALL = "You can't call if a bet hasn't been made.";
    public static final String CANT_RAISE = "You can't raise if a bet hasn't been made.";
    public static final String MATCH_BET = "You have to call or raise the current bet.";
    public static final String PLAYER_HAS_TO_BET = "Do you want to match the bet?\nRAISE || CALL || BET || ALLIN || FOLD";
    public static final String CARD_SPACE = " ".repeat(5) + "\n" +
                                            " ".repeat(5) + "\n" +
                                            " ".repeat(5) + "\n";
    public static final String PLEASE_BET = "Please make a bet.";
    public static final String DO_ACTION = "Make another action.";
    public static final String HELP = "/call : You match the current highest bet.\n" +
                                      "/bet : Bet an amount of money that must match or be higher than the current bet.\n" +
                                      "/raise : Bets the current highest bet + 50%.\n" +
                                      "/fold : You fold and lose this round.\n" +
                                      "/allin : You bet all your current credits. No more decisions can be made.\n"+
                                      "/check : Skips turn, can only be made when there's no higher bet.\n";

    public static final String CURRENT_POT = "Current pot: ";
    public static final String CANT_CONNECT = ColorCodes.RED_BOLD_BRIGHT +
            "Server not online, trying to connect again " + ColorCodes.RESET;






}
