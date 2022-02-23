package academy.mindswap.commands;

import academy.mindswap.Server.Game;
import academy.mindswap.utils.Messages;

public class AllinHandler implements CommandHandler {
    @Override
    public void execute(Game game, Game.PlayerHandler playerHandler) {
        playerHandler.setBet(playerHandler.getCredits());
        game.broadCastMessage(playerHandler.getUsername() +
                Messages.ALL_IN +
                playerHandler.getCredits() +
                Messages.CREDITS);

    }
}
