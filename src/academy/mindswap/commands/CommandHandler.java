package academy.mindswap.commands;

import academy.mindswap.Server.Game;
import academy.mindswap.Server.GameExceptions.PlayerDisconnectedException;

@FunctionalInterface
public interface CommandHandler {

    void execute(Game game, Game.PlayerHandler playerHandler) throws PlayerDisconnectedException;
}
