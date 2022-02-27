/*
 * @(#)AllinHandler.java        1.0 26/02/2022
 *
 * Copyright (c) MindSwap Academy - David Millasseau, Tiago Correia & João Gonçalves
 * All rights reserved.
 *
 * This software was produced to become our first group project.
 */

package academy.mindswap.commands;

import academy.mindswap.Server.Game;
import academy.mindswap.utils.Messages;
/**
 * If the player makes allin he bets all credits and
 * No more decisions can be made.
 */


public class AllinHandler implements CommandHandler {
    /**
     * Bet all the credits the player has.
     * Send a message to all players
     * @param game represents an instance of a member class game
     * @param playerHandler to access player properties and methods
     */
    @Override
    public void execute(Game game, Game.PlayerHandler playerHandler) {
        playerHandler.bet(playerHandler.getCredits());
        playerHandler.wentAllIn();
        playerHandler.doAction();

        game.broadCastMessage(playerHandler.getUsername() +
                Messages.ALL_IN +
                playerHandler.getCredits() +
                Messages.CREDITS);
    }
}
