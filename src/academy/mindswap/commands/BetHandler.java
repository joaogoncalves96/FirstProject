/*
 * @(#)BetHandler.java        1.0 26/02/2022
 *
 * Copyright (c) MindSwap Academy - David Millasseau, Tiago Correia & João Gonçalves
 * All rights reserved.
 *
 * This software was produced to become our first group project.
 */

package academy.mindswap.commands;

/**
 * If the player bets he bets whatever he wants.
 If one player bets, the next player can only bet greater than or equal to the previous bet.
 */


import academy.mindswap.Server.Game;
import academy.mindswap.Server.GameExceptions.PlayerDisconnectedException;
import academy.mindswap.utils.Messages;

import java.io.IOException;

public class BetHandler implements CommandHandler {
    @Override
    /**
     * Bet as many credits as the player wants
     * Send a message to all players
     * @param game represents an instance of a member class game
     * @param playerHandler to access player properties and methods
     * @throws PlayerDisconnectedException when player disconnected the socket on this side
     */
    public void execute(Game game, Game.PlayerHandler playerHandler) throws PlayerDisconnectedException, IOException {
            playerHandler.askForBet();
            game.broadCastMessage(playerHandler.getUsername() +
                    Messages.BET +
                    playerHandler.getPlayerLastBet() +
                    Messages.CREDITS);
    }
}
