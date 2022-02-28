/*
 * @(#)HelpHandler.java        1.0 26/02/2022
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
 * If the player makes this command, the console gives him all the commands that are possible to use
 * and their function
 */

public class HelpHandler implements CommandHandler {
    /**
     * Send a message to all players with all commands
     * @param game represents an instance of a member class game
     * @param playerHandler to access player properties and methods
     @throws PlayerDisconnectedException when player disconnected socket from this side
     */
    @Override
    public void execute(Game game, Game.PlayerHandler playerHandler) throws PlayerDisconnectedException, IOException {
        playerHandler.sendMessage(Messages.DO_ACTION);
        playerHandler.sendMessage(Messages.HELP);
    }
}
