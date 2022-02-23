package academy.mindswap.commands;

import academy.mindswap.Server.Game;
import academy.mindswap.utils.Messages;

public class FoldHandler implements CommandHandler{
    @Override
    public void execute(Game game, Game.PlayerHandler playerHandler) {
        playerHandler.fold();
        game.broadCastMessage(playerHandler.getUsername() + Messages.FOLDED);
    }
}
