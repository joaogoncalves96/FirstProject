package academy.mindswap.commands;

import academy.mindswap.Server.Game;
import academy.mindswap.utils.Messages;

public class AllinHandler implements CommandHandler {
    @Override
    public void execute(Game game, Game.PlayerHandler playerHandler) {
        double bet = playerHandler.getCredits();
        playerHandler.wentAllIn();
        playerHandler.doAction();

        game.broadCastMessage(playerHandler.getUsername() +
                Messages.ALL_IN +
                bet +
                Messages.CREDITS);
    }
}
