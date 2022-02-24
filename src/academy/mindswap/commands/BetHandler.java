package academy.mindswap.commands;

import academy.mindswap.Server.Game;
import academy.mindswap.Server.GameExceptions.PlayerDisconnectedException;
import academy.mindswap.utils.Messages;

import java.io.IOException;

public class BetHandler implements CommandHandler {
    @Override
    public void execute(Game game, Game.PlayerHandler playerHandler) {
        try {
            playerHandler.askForBet();
            game.broadCastMessage(playerHandler.getUsername() +
                    Messages.BET +
                    playerHandler.getBet() +
                    Messages.CREDITS);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (PlayerDisconnectedException e) {
            e.printStackTrace();
        }
    }
}
