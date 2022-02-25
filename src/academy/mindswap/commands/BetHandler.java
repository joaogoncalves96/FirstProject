package academy.mindswap.commands;

import academy.mindswap.Server.Game;
import academy.mindswap.Server.GameExceptions.PlayerDisconnectedException;
import academy.mindswap.utils.Messages;

import java.io.IOException;

public class BetHandler implements CommandHandler {
    @Override
    public void execute(Game game, Game.PlayerHandler playerHandler) throws PlayerDisconnectedException {
        try {
            playerHandler.askForBet();
            game.broadCastMessage(playerHandler.getUsername() +
                    Messages.BET +
                    playerHandler.getPlayerLastBet() +
                    Messages.CREDITS);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
