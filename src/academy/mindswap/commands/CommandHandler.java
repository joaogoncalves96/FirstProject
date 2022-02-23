package academy.mindswap.commands;

import academy.mindswap.Server.Game;

@FunctionalInterface
public interface CommandHandler {

    void execute(Game game, Game.PlayerHandler playerHandler);
}
