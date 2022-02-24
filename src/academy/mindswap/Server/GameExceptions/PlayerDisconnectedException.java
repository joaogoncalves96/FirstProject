package academy.mindswap.Server.GameExceptions;

public class PlayerDisconnectedException extends GameException {
    public PlayerDisconnectedException() {
        super("Player has disconnected suddenly, removing player from table.");
    }
}
