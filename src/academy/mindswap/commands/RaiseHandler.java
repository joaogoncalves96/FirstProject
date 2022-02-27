/*
 * @(#)RaiseHandler.java        1.0 26/02/2022
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
 * Player bets current highest bet and +50%
 */

public class RaiseHandler implements CommandHandler {
    /**
     * If the last bet was void, you cannot bet.
     * Send a message to all players with all commands
     * @param game represents an instance of a member class game
     * @param playerHandler to access player properties and methods
     @throws PlayerDisconnectedException when player disconnected socket from this side
     */
    @Override
    public void execute(Game game, Game.PlayerHandler playerHandler) throws PlayerDisconnectedException, IOException {

        if(game.getLastBet() == 0.0) {
            playerHandler.sendMessage(Messages.CANT_RAISE);
            return;
        }
        double bet = game.getLastBet();

        playerHandler.doAction();
        playerHandler.bet(bet * 1.5);
        game.setLastBet(bet * 1.5);
        game.broadCastMessage(playerHandler.getUsername() +
                Messages.RAISE +
                (bet * 1.5) +
                Messages.CREDITS);

    }
}
