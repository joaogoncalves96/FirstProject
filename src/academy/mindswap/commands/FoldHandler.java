/*
 * @(#)FoldHandler.java        1.0 26/02/2022
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
 * If the player folds, he will leave the round and lose that round
 */

public class FoldHandler implements CommandHandler{
    /**
     * Send a message to all players
     * @param game represents an instance of a member class game
     * @param playerHandler to access player properties and methods
     */
    @Override
    public void execute(Game game, Game.PlayerHandler playerHandler) {
        playerHandler.fold();
        playerHandler.doAction();
        game.broadCastMessage(playerHandler.getUsername() + Messages.FOLDED);
    }
}
