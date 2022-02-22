package academy.mindswap.commands;

import academy.mindswap.server.Game;

public interface CommandHandler {

    void execute(Game game, Game.PlayerHandler playerHandler);
}
