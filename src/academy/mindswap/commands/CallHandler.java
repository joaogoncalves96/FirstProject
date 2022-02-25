package academy.mindswap.commands;

import academy.mindswap.Server.Game;
import academy.mindswap.Server.GameExceptions.PlayerDisconnectedException;
import academy.mindswap.utils.Messages;

import java.io.IOException;

public class CallHandler implements CommandHandler{
    @Override
    public void execute(Game game, Game.PlayerHandler playerHandler) throws PlayerDisconnectedException, IOException {
        if(game.getLastBet() == 0.0) {
            playerHandler.sendMessage(Messages.CANT_CALL);
            return;
        }

        playerHandler.doAction();
        double lastBet = game.getLastBet();
        playerHandler.bet(lastBet);
        game.broadCastMessage(playerHandler.getUsername() + Messages.CALL);
    }
}
