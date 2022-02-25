package academy.mindswap.commands;

import academy.mindswap.Server.Game;
import academy.mindswap.utils.Messages;

public class CallHandler implements CommandHandler{
    @Override
    public void execute(Game game, Game.PlayerHandler playerHandler) {
        double lastBet = game.getLastBet();
        playerHandler.bet(lastBet);
        game.broadCastMessage(playerHandler.getUsername() + Messages.CALL);
    }

}
