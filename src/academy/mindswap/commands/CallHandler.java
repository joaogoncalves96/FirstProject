/*
 * @(#)CallHandler.java        1.0 26/02/2022
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
 * If the player calls it means he will call the first player's bet
 */

public class CallHandler implements CommandHandler{
    /**
     * If a bet has not been placed, the player cannot call
     * Send a message to all players
     * @param game represents an instance of a member class game
     * @param playerHandler to access player properties and methods
     @throws PlayerDisconnectedException when player disconnected socket from this side

     */
    @Override
    public void execute(Game game, Game.PlayerHandler playerHandler) throws PlayerDisconnectedException, IOException {
        if(game.getLastBet() == 0.0) {
            playerHandler.sendMessage(Messages.CANT_CALL);
            return;
        }

        playerHandler.doAction();
        double lastBet = game.getLastBet();
        playerHandler.bet(lastBet);
        game.broadCastMessage(playerHandler.getUsername() + Messages.CALL);
    }
}
