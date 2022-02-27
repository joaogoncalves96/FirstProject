/*
 * @(#)CheckHandler.java        1.0 26/02/2022
 *
 * Copyright (c) MindSwap Academy - David Millasseau, Tiago Correia & João Gonçalves
 * All rights reserved.
 *
 * This software was produced to become our first group project.
 */


package academy.mindswap.commands;

import academy.mindswap.Server.Game;
import academy.mindswap.Server.GameExceptions.PlayerDisconnectedException;
import academy.mindswap.utils.Messages;

import java.io.IOException;
/**
 * If the player checks it means that he will simply pass the play without betting anything,
 * if someone bets credits before him and more high, he will not be able to check
 */

public class CheckHandler implements CommandHandler {
    @Override
    /**
     * If you try to check, and there is a bet in the play, it prints that the player has to bet
     * Send a message to all players
     * @param game represents an instance of a member class game
     * @param playerHandler to access player properties and methods
     @throws PlayerDisconnectedException when player disconnected socket from this side
     */
    public void execute(Game game, Game.PlayerHandler playerHandler) throws PlayerDisconnectedException {
        if(!playerHandler.hasPlayerMatchedBet())  {
            try {
                playerHandler.sendMessage("You have to match the current bet.");
                return;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        game.broadCastMessage(playerHandler.getUsername() + Messages.PLAYER_CHECKED);
        playerHandler.doAction();
    }
}
