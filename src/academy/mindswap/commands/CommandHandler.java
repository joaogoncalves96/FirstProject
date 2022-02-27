/*
 * @(#)CommandHandler.java        1.0 26/02/2022
 *
 * Copyright (c) MindSwap Academy - David Millasseau, Tiago Correia & João Gonçalves
 * All rights reserved.
 *
 * This software was produced to become our first group project.
 */

package academy.mindswap.commands;

import academy.mindswap.Server.Game;
import academy.mindswap.Server.GameExceptions.PlayerDisconnectedException;

import java.io.IOException;

/**
 * All the commands implement the interface command handler
 */

@FunctionalInterface
public interface CommandHandler {

    /**
     *This method represents the action that each player will have
     * @param game represent the instance of a member class game
     * @param playerHandler or access the properties and methods of player
     * @throws PlayerDisconnectedException when player disconnected the socket on this side
     */

    void execute(Game game, Game.PlayerHandler playerHandler) throws PlayerDisconnectedException, IOException;
}
