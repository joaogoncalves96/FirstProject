package academy.mindswap.commands;

import academy.mindswap.Server.Game;
import academy.mindswap.Server.GameExceptions.PlayerDisconnectedException;
import academy.mindswap.utils.Messages;

import java.io.IOException;

public class RaiseHandler implements CommandHandler {
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
