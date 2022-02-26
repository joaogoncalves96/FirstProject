package academy.mindswap.commands;

import academy.mindswap.Server.Game;
import academy.mindswap.Server.GameExceptions.PlayerDisconnectedException;
import academy.mindswap.utils.Messages;

import java.io.IOException;

public class CheckHandler implements CommandHandler {
    @Override
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
