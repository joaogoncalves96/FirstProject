package academy.mindswap.commands;

import academy.mindswap.Server.Game;
import academy.mindswap.Server.GameExceptions.PlayerDisconnectedException;
import academy.mindswap.utils.Messages;

import java.io.IOException;

public class WalletHandler implements CommandHandler {
    @Override
    public void execute(Game game, Game.PlayerHandler playerHandler) throws PlayerDisconnectedException, IOException {
        playerHandler.sendMessage(String.valueOf(playerHandler.getCredits()));
        playerHandler.sendMessage(Messages.DO_ACTION);
    }
}
